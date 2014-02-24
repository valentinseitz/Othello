package com.ema.othelloVE.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import com.ema.othelloVE.model.Jeton;

public class Plateau {
	private static final int FORT = 8;
	private static final int X = 1;
	private static final int C = 2;
	private static final int NORMAL = 4;
	private static final int[][] ponderation = {
		{FORT,C,NORMAL,NORMAL,NORMAL,NORMAL,C,FORT},
		{C,X,NORMAL,NORMAL,NORMAL,NORMAL,X,C},
		{NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL},
		{NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL},
		{NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL},
		{NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL,NORMAL},
		{C,X,NORMAL,NORMAL,NORMAL,NORMAL,X,C},
		{FORT,C,NORMAL,NORMAL,NORMAL,NORMAL,C,FORT}};
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

	private void setJeton(int i, int j, Jeton couleur) {
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

	public int nombreJetons(Jeton couleur) {
		//Le nombre de jetons, de la couleur donnée, présents sur le plateau
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

	public boolean coupPossible(int x, int y, Jeton jeton, boolean retourner) {
		boolean possible = false;
		if (getJeton(x, y) == Jeton.VIDE){
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					// Une cellule adjacente?
					if (i != 0 || j != 0) {
						// La droite donnée par la cellule testée et la cellule adjacente est-elle possible?
						possible = possible || coupPossible(x, y, jeton, i, j, retourner);
					}
				}
			}
		}
		return possible;
	}

	private boolean coupPossible(int x, int y, Jeton jeton, int dirX, int dirY,
			boolean retourner) {
		int nb = 0;
		
		int droiteX;
		int droiteY;
		Jeton jetonATester;
		boolean continuer;
	
		droiteX = x;
		droiteY = y;
		continuer = true;
		while (continuer) {
			droiteX = droiteX + dirX;
			droiteY = droiteY + dirY;
			
			jetonATester = getJeton(droiteX, droiteY);
			// Selon le type de jeton
			switch (jetonATester) {
			case VIDE:
			case INEXISTANT:
				// Si l'on rencontre un jeton vide on est en boit de droite
				nb = 0;
				continuer = false;
				break;
			case NOIR:
			case BLANC:
				// Un jeton de joueur
				//On s'arrête di l'on a trouvé un jeton de la couleur du joueur
				continuer = jetonATester != jeton;
				if (continuer){
					//Pas encore un jeton du jouer, c'est un jeton d'adversaire de plus qu'il est possible de retourner
					nb++;
				}
				break;
			default:
				nb = 0;
				continuer = false;
			}
		}
		
		if (nb > 0 && retourner){
			//On reparcours la droite en sens inverse
			do{
				droiteX = droiteX - dirX;
				droiteY = droiteY - dirY;
				//Le jeton est maintenant de la couleur du joueur
				setJeton(droiteX, droiteY, jeton);
			}while(droiteX != x || droiteY != y);
		}
		
		return nb > 0;
	}

	public List<Point> coupsPossibles(Jeton jeton){
		List<Point> coupsPossibles = new ArrayList<Point>();
		for (int i = 0; i < Plateau.TAILLE; i++) {
			for (int j = 0; j < Plateau.TAILLE; j++) {
				// Une cellule adjacente?
				if (coupPossible(i, j, jeton, false)){
					coupsPossibles.add(new Point(i,j));
				}
			}
		}
		return coupsPossibles;
	}

	public boolean peutJouer(Jeton jeton) {
		boolean possible;
	
		possible = false;
		// Parcours des cellules aux alentours
		for (int i = 0; !possible && i < Plateau.TAILLE; i++) {
			for (int j = 0; !possible && j < Plateau.TAILLE; j++) {
				// Une cellule adjacente?
				possible = coupPossible(i, j, jeton, false);
			}
		}
	
		return possible;
	}
	
	public int getHeuristique(Jeton couleur){
		return nombreJetons(couleur) - nombreJetons(couleur.getAdversaire());
		
		
	}

}
