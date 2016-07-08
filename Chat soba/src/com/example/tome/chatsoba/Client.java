package com.example.tome.chatsoba;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private String ime;

	public static void main(String[] args) {
		for (int i = 0; i < 1; i++) {
			Konekcija c = new Konekcija();
			new Thread(c).start();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class Konekcija implements Runnable {
	private Socket sc;
	static int brojkorisnika = 0;

	Konekcija() {
		brojkorisnika++;
	}

	public void run() {
		int korisnik = brojkorisnika;
		String host = "localhost";
		int port = 20151;
		while (true) {
			try {
				System.out.println("Pokusava se spojit");
				sc = new Socket(host, port);
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
		ObjectOutputStream out = null;
		ObjectInputStream is = null;
		try {
			out = new ObjectOutputStream(sc.getOutputStream());
			is = new ObjectInputStream(sc.getInputStream());
			new Thread(new Primi(is, korisnik)).start();
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		int brojporuke = 0;
		while (!sc.isClosed()) {
			try {
				Poruka p = new Poruka("" + korisnik, "" + brojporuke);
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
			brojporuke++;
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class Primi implements Runnable {//stalno prima poruke
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
				System.out.println(p.ispisPoruke()); // }
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