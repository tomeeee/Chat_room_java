package com.example.tome.chatsoba;

/**
 * Created by Tome on 23.1.2016..
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


class Client implements Runnable {
    private Socket sc;
    private ObjectOutputStream out = null;
    private String ime;
    static int brojkorisnika = 0;
    static MainActivity main;

    Client(MainActivity main) {
        brojkorisnika++;
        this.main=main;
    }

    public void run() {
        int korisnik = brojkorisnika;
        String host = "192.168.0.12";
        int port = 20151;


        while (true) {
            try {
                System.out.println("Pokusava se spojit");
                sc = new Socket(host,port);
                if (sc.isConnected())
                    break;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ObjectInputStream is = null;
        try {
            out = new ObjectOutputStream(sc.getOutputStream());
            new Thread(new Primi(new ObjectInputStream(sc.getInputStream()), korisnik)).start();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //
        //int brojporuke = 0;
        //while (!sc.isClosed()) {
        //try {
        //Poruka p = new Poruka("android " + korisnik, "" + brojporuke);
        //out.writeObject(p);
        //out.flush();
        //} catch (IOException e) {
        //e.printStackTrace();
        //try {
        //sc.close();
        //} catch (IOException e1) {
        //e1.printStackTrace();
        //}
        //}
        //brojporuke++;
        //try {
        //Thread.sleep(5000);
        //} catch (InterruptedException e) {
        //e.printStackTrace();
        //}
        //}
    }
     void posalji(String msg) {
         if (!sc.isClosed()) {
             try {
                 Poruka p = new Poruka("android ", msg);
                 out.writeObject(p);
                 out.flush();
             } catch (IOException e) {
                 e.printStackTrace();
                 try {
                     sc.close();
                 } catch (IOException e1) {
                     e1.printStackTrace();
                 }
             }
         }
     }
}

class Primi implements Runnable {
    private Poruka p = null;
    ObjectInputStream is = null;
    int korisnik;

    Primi(ObjectInputStream inputs, int korisnik) {
        this.korisnik = korisnik;
        this.is = inputs;
    }

    public void run() {
        while (true) {
            try {
                p = (Poruka) is.readObject();
                System.out.print("primio korisnik : " + korisnik + "    ");
                System.out.println(p.ispisPoruke());
                Client.main.ispis(p.ispisPoruke());
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                try {
                    is.close();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
