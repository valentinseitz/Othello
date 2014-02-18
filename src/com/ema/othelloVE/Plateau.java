package com.ema.othelloVE;

import com.ema.othelloVE.Jeton;

public class Plateau {
	public static final int TAILLE = 8;
	private Jeton[][] othellier;

	public Plateau() {
		othellier = new Jeton[TAILLE][TAILLE];
	}

	public Plateau(Plateau plateau) {
		this();
		for (int i = 0; i < TAILLE; i++) {
			for (int j = 0; j < TAILLE; j++)
				othellier[i][j] = plateau.othellier[i][j];
		}
	}

	public void initPlateau() {
		synchronized (othellier) {
			for (int i = 0; i < TAILLE; i++) {
				for (int j = 0; j < TAILLE; j++)
					othellier[i][j] = Jeton.VIDE;
			}

			othellier[3][3] = Jeton.NOIR;
			othellier[4][3] = Jeton.BLANC;
			othellier[3][4] = Jeton.BLANC;
			othellier[4][4] = Jeton.NOIR;
		}
	}

	public void setJeton(int i, int j, Jeton couleur) {
		this.othellier[i][j] = couleur;
	}

	public Jeton getJeton(int x, int y) { // retourne la couleur du jeton présent
											// sur le plateau aux coordonnées
											// x,y
		Jeton jeton = Jeton.INEXISTANT;
		if (0 <= x && x < TAILLE && 0 <= y && y < TAILLE){
			jeton = othellier[x][y];
		}
		return jeton;
	}

	public int nombreJetons(Jeton couleur) { // retourne le nombre de jetons, de
											// la couleur donnée, présents sur
											// le plateau
		int nb = 0;
		synchronized (othellier) {
			for (int i = 0; i < TAILLE; i++) {
				for (int j = 0; j < TAILLE; j++) {
					if (othellier[i][j] == couleur) {
						nb++;
					}
				}
			}
		}
		return nb;
	}

}
