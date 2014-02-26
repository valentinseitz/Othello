package com.ema.othelloVE.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.util.Log;

import com.ema.othelloVE.model.Jeton;

public class Plateau {
	private static final int[][] ponderation = {
		{256,	1,		25,		26,		26,		25,		1,		256},
		{1,		0,		5,		6,		6,		5,		0,		1},
		{25,	5,		15,		16,		16,		15,		5,		25},
		{26,	6,		16,		16,		16,		16,		6,		26},
		{26,	6,		16,		16,		16,		16,		6,		26},
		{25,	5,		15,		16,		16,		15,		5,		25},
		{1,		0,		5,		6,		6,		5,		0,		1},
		{256,	1,		25,		26,		26,		25,		1,		256}};
	private static final int[][] pondeCoin = {
		{32728,	8182,	4096,	2048,	1024,	512,	256,	128},
		{8182,	64,		64,		64,		64,		64,		64,		64},
		{4096,	64,		32,		32,		32,		32,		32,		32},
		{2048,	64,		32,		16,		16,		16,		16,		16},
		{1024,	64,		32,		16,		8,		8,		8,		8},
		{512,	64,		32,		16,		8,		4,		4,		4},
		{256,	64,		32,		16,		8,		4,		2,		2},
		{128,	64,		32,		16,		8,		4,		2,		1}};
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
						possible = coupPossible(x, y, jeton, i, j, retourner) || possible;
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
		int heuristique;
		heuristique = ponderation(couleur) + ponderationCoins(couleur) - ponderationCoins(couleur.getAdversaire());
		return heuristique;
		
		
	}
	
	/**
	 * Pondération de base de la situation de la grille (prend en compte l'adversaire, pondérations d'accès aux coins)
	 * @param couleur Couleur pour laquelle on souhaite la pondération de la grille
	 * @return La pondération de la grille pour un joueur donné
	 */
	private int ponderation(Jeton couleur){
		//Le nombre de jetons, de la couleur donnée, présents sur le plateau
				int nb = 0;
				synchronized (othellier) {
					for (int i = 0; i < TAILLE; i++) {
						for (int j = 0; j < TAILLE; j++) {
							if (othellier[i][j] == couleur) {
								nb += ponderation[i][j];
							} else if (othellier[i][j] == couleur.getAdversaire()){
								nb -= ponderation[i][j];
							}
						}
					}
				}
				return nb;
	}
	
	/**
	 * Pondération du "bétonnage" de coin, placement de pions imprenables (prend en compte l'adversaire, à partir des coins)
	 * @param couleur Couleur pour laquelle on souhaite la pondération de la grille
	 * @return La pondération de la grille pour un joueur donné
	 */
	private int ponderationCoins(Jeton couleur){
		int ponde = 0;
		int[][] coin = {{0,0,1,1},{0,7,1,-1},{7,0,-1,1},{7,7,-1,-1}};
		int max;
		Jeton couleurQuart;
		//POur chaque coinde l'othellier
		for (int c = 0; c < coin.length; c++){
			max = TAILLE;
			//Couleur de la case de coin
			couleurQuart = othellier[coin[c][0]][coin[c][1]];
			//Case vide?
			if (couleurQuart != Jeton.VIDE){
				//Case non vide
				for (int i = 0; i < TAILLE; i++) {
					for (int j = 0; j < TAILLE; j++) {
						//Cette case appartient-elle au détenteur de la case de coin?
						if (j < max && othellier[coin[c][0]+(coin[c][2]*i)][coin[c][1]+(coin[c][3]*j)] == couleurQuart) {
							//Ce joueur ou l'adversaire
							if (couleurQuart == couleur){
								//Le joueur, c'est bien pour lui (+)
								ponde += pondeCoin[i][j];
							} else {
								//L'adversaire, c'est mauvais pour le joueur (-)
								ponde -= pondeCoin[i][j];
							}
						} else {
							max = Math.min(max, j);
						}
					}
				}
			}
		}
		return ponde;
	}

}
