package com.example.tome.chatsoba;

import java.io.Serializable;
import java.util.Date;

public class Poruka implements Serializable{
	private String izvor;
	private String vrijeme;
	private String sadrzaj;

	Poruka() {
		izvor=null;
		vrijeme=null;
		sadrzaj=null;
	}
	Poruka(String izvor,String sadrzaj) {
		this.izvor=izvor;
		this.sadrzaj=sadrzaj;
		this.vrijeme= new Date().toGMTString();
	}
	void novaPoruka(String izvor,String sadrzaj){
		this.izvor=izvor;
		this.sadrzaj=sadrzaj;
		this.vrijeme= new Date().toGMTString();
	}
	String ispisPoruke(){
		return "[ Kljent: " + this.izvor+ " | Datum: " + this.vrijeme +" | Poruka :"+this.sadrzaj+"]";
	}
}