package com.ema.othelloVE;

import android.graphics.Color;

public enum Jeton {

	BLANC, NOIR, VIDE, INEXISTANT;
	
	public Jeton getAdversaire(){
		Jeton adversaire;
		switch (this) {
		case BLANC:
			adversaire = NOIR;
			break;
		case NOIR:
			adversaire = BLANC;
			break;
		default:
			adversaire = INEXISTANT;
			break;
		}
		return adversaire;
	}
	
	public int getCouleur(){
		int couleur;
		switch (this) {
		case BLANC:
			couleur = Color.WHITE;
			break;
		case NOIR:
			couleur = Color.BLACK;
			break;
		case VIDE:
			couleur = Color.GREEN;
			break;
		default:
			couleur = Color.TRANSPARENT;
			break;
		}
		return couleur;
	}

}
