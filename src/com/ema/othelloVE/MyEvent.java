package com.ema.othelloVE;


public class MyEvent {
	private int y;
	private int x;
	private Joueur joueur;
	
	public MyEvent(Joueur joueur, int x, int y) {
		super();
		this.joueur = joueur;
		this.y = y;
		this.x = x;
	}
	public Joueur getJoueur() {
		return joueur;
	}
	public int getY() {
		return y;
	}
	public int getX() {
		return x;
	}
}

