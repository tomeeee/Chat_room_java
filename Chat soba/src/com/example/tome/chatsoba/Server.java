package com.example.tome.chatsoba;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Server {
	static List<Runnable> comserver = Collections.synchronizedList(new ArrayList<Runnable>()); // za svakega po 2
	private static ServerSocket ss;
	private static Socket con = null;

	public static void main(String[] args) {
		int port = 20151;
		AktivnoThredova at = new AktivnoThredova();
		new Thread(at).start();
		
		try {
			ss = new ServerSocket(port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				con = ss.accept();
				System.out.println("spojen");
				synchronized (comserver) {
					comserver.add(new ComServer(con));
					new Thread(comserver.get(comserver.size() - 1)).start();
				}
			} catch (IOException ex) {
				System.out.println("nije uspila konekcija client-server");
			}

			/*
			 * if (comserver.size() > at.getBrThread()) { ComServer cs = null;
			 * for (int i = 0; i < comserver.size(); i++) { cs = (ComServer)
			 * comserver.get(i); if (cs.getSocketClosedStatus()) {
			 * comserver.remove(i); } } }
			 */

		}
	}
}

class ComServer implements Runnable {
	private List<Poruka> poruke = Collections.synchronizedList(new LinkedList<Poruka>());//lista poruka(od drugih) koje treba poslat nazad clinetu
	private Socket cs = null;

	ComServer(Socket con) {
		cs = con;
	}

	public void run() {
		ObjectInputStream is = null;
		ObjectOutputStream os = null;
		try {
			is = new ObjectInputStream(cs.getInputStream());
			os = new ObjectOutputStream(cs.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();// dodat
		}
		InputClient inputC = new InputClient(is, this.hashCode());
		new Thread(inputC).start();
		while (true) {
			prosljediClient(os);
			try {
				Thread.sleep(500);
				if (cs.isClosed()) {
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	void dodajPoruku(Poruka pnova) {
		synchronized (poruke) {
			this.poruke.add(pnova);
		}
	}

	boolean getSocketClosedStatus() {
		return cs.isClosed();
	}

	void prosljediClient(ObjectOutputStream os) {
		synchronized (poruke) {
			while (poruke.size() > 0) {
				try {
					os.writeObject(poruke.remove(0));
					os.flush();
					System.out.println("prosljedeno Clientu");
				} catch (IOException e) {
					System.out.println("nije uspilo prosljedit kljentu");// e.printStackTrace();
				}
			}
		}
	}
}

class InputClient implements Runnable {
	private ObjectInputStream is = null;
	private int hash;

	InputClient(ObjectInputStream is, int hash) {
		this.is = is;
		this.hash = hash;
	}

	public void run() {
		Poruka p = null;
		ComServer cs = null;
		int brclient = 0;
		while (true) {
			try {
				p = (Poruka) is.readObject();// p.ispisPoruke();  
				brclient = Server.comserver.size();
				for (int i = 0; i < brclient; i++) {
					if (hash != Server.comserver.get(i).hashCode()) {
						cs = (ComServer) Server.comserver.get(i);
						cs.dodajPoruku(p);
					}
				}
			} catch (IOException | ClassNotFoundException ex) {
				System.out.println("greska prosljedi\n" + ex);
				// ex.printStackTrace();
				try {
					for (int i = 0; i < brclient; i++) { 
						synchronized (Server.comserver) {
							brclient = Server.comserver.size();
							if (hash == Server.comserver.get(i).hashCode()) {
								Server.comserver.remove(i);
								break;
							}
						}
					}
					is.close();
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

class AktivnoThredova implements Runnable {
	private int num = 0;

	public void run() {
		while (true) {
			num = Thread.activeCount();
			System.out.println("Aktivno Thread: " + num + " lista :" + Server.comserver.size());
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	int getBrThread() {
		return (num) / 2;
	}
}