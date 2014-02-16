package com.ema.othelloVE;

public class Jeton {

	public static final byte BLANC = 1;
	public static final byte NOIR = 2;
	public static final byte VIDE = 0;

	public static byte jetonAdversaire(byte jeton) {
		byte jetonAdversaire;
		switch (jeton) {
		case BLANC:
			jetonAdversaire = NOIR;
			break;
		case NOIR:
			jetonAdversaire = BLANC;
			break;
		default:
			jetonAdversaire = VIDE;
			break;
		}
		return jetonAdversaire;
	}

}
