package com.ema.othelloVE.model;


public class Coup {

	private int lig;
	private int col;
	private Jeton couleur;
	
	public Coup(int l, int c, Jeton codeCouleur) 
	{	lig=l;
		col=c;
		couleur = codeCouleur;
	}
	
	public int getLigne()
	{ 	return(lig);
	}

	public int getColonne()
	{ 	return(col);
	}
	
	public Jeton getCouleur(){
		return(couleur);
	}
	
	
}
