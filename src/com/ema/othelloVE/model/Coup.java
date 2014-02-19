package com.ema.othelloVE.model;


public class Coup {

	private int x;
	private int y;
	private Jeton couleur;
	
	public Coup(int x, int y, Jeton codeCouleur) 
	{	this.x=x;
		this.y=y;
		couleur = codeCouleur;
	}
	
	public int getX()
	{ 	return(x);
	}

	public int getY()
	{ 	return(y);
	}
	
	public Jeton getCouleur(){
		return(couleur);
	}
	
	
}
