package com.ema.othelloVE.controler;

import com.ema.othelloVE.model.Jeton;
import com.ema.othelloVE.model.Plateau;

public class ControleurPlateau {

	public static boolean coupPossible(Plateau plateau, int x, int y, Jeton jeton) {
		boolean possible;

		// Pourqu'il soit possible de placer le jeton, la place doit �tre vide
		possible = plateau.getJeton(x, y) == Jeton.VIDE;
		// Eventuellement possible?
		if (possible) {
			// Le placement est �ventuellement possible
			// Le placement n'est possible que s'il y a une droite qui peut se
			// former
			possible = false;
			// Parcours des cellules aux alentours
			for (int i = -1; !possible && i <= 1; i++) {
				for (int j = -1; !possible && j <= 1; j++) {
					// Une cellule adjacente?
					if (i != 0 || j != 0) {
						// La droite donn�e par la cellule test�e et la cellule
						// adjacente est-elle possible?
						possible = droitePossible(plateau, x, y, jeton, i, j);
					}
				}
			}
		}
		return possible;
	}

	private static boolean droitePossible(Plateau plateau, int x, int y,
			Jeton jeton, int dirX, int dirY) {
		boolean possible;
		int distance;
		int droiteX;
		int droiteY;
		Jeton jetonATester;
		boolean continuer;

		continuer = true;
		possible = true;
		droiteX = x;
		droiteY = y;
		distance = 0;
		while (possible && continuer) {
			droiteX = droiteX + dirX;
			droiteY = droiteY + dirY;
			// Coordonn�es dans le plateau?
			possible = droiteX >= 0 && droiteX < Plateau.TAILLE
					&& droiteY >= 0 && droiteY < Plateau.TAILLE;
			if (possible) {
				// Quel est le jeton � tester?
				jetonATester = plateau.getJeton(droiteX, droiteY);
				// Selon le type de jeton
				switch (jetonATester) {
				case VIDE:
					// Si l'on rencontre un jeton vide la droite est impossible
					possible = false;
					continuer = false;
					break;
				case NOIR:
				case BLANC:
					// Un jeton de joueur
					if (jetonATester == jeton) {
						// Si c'est un jeton du m�me jeton il faut qu'il y en
						// ait au moins un de couleur oppos�e entre (distance >
						// 0)
						possible = distance > 0;
						continuer = false;
					} else {
						// Si l'on est sur un jeton de couleur oppos�e il faut
						// regarder le suivant sur la droite pour savoir si la
						// droite permet un placement
						possible = true;
						continuer = true;
					}
					break;
				default:
					possible = false;
					continuer = false;
				}
				// On va un jeton plus loin
				distance++;
			} else {
				// pas la peine de continuer, on a d�pass� le bord du plateau
				continuer = false;
			}
		}

		return possible;
	}

	public static void retournePions(Plateau plateau, int x, int y) {
		// Parcours des cellules aux alentours
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// Une cellule adjacente?
				if (i != 0 || j != 0) {
					// La droite donn�e par la cellule test�e et la cellule
					// adjacente est-elle possible?
					if (droitePossible(plateau, x, y, plateau.getJeton(x, y),
							i, j)) {
						retourneDroite(plateau, x, y, i, j);
					}
				}
			}
		}
	}

	private static void retourneDroite(Plateau plateau, int x, int y, int dirX,
			int dirY) {
		Jeton jeton;
		int droiteX;
		int droiteY;
		Jeton jetonARetourner;
		boolean continuer;

		droiteX = x;
		droiteY = y;
		jeton = plateau.getJeton(x, y);
		continuer = true;
		while (continuer) {
			droiteX = droiteX + dirX;
			droiteY = droiteY + dirY;
			jetonARetourner = plateau.getJeton(droiteX, droiteY);
			// Selon le type de jeton
			switch (jetonARetourner) {
			case VIDE:
				// Si l'on rencontre un jeton vide on est en boit de droite
				continuer = false;
				break;
			case NOIR:
			case BLANC:
				// Un jeton de joueur
				if (jetonARetourner == jeton) {
					// Si c'est un jeton du m�me jeton il faut qu'il y en ait au
					// moins un de couleur oppos�e entre (distance > 0)
					continuer = false;
				} else {
					// Si l'on est sur un jeton de couleur oppos�e il faut le
					// retourner et continuer sur la droite
					plateau.setJeton(droiteX, droiteY, jeton);
					continuer = true;
				}
				break;
			default:
				continuer = false;
			}
		}
	}

	public static int nbRetournementsPossibles(Plateau plateau, int x, int y,
			Jeton jeton) {
		int nb = 0;

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// Une cellule adjacente?
				if (i != 0 || j != 0) {
					// La droite donn�e par la cellule test�e et la cellule
					// adjacente est-elle possible?
					nb += nbPossibleSurDroite(plateau, x, y, jeton, i, j);
				}
			}
		}

		return nb;
	}

	private static int nbPossibleSurDroite(Plateau plateau, int x, int y,
			Jeton jeton, int dirX, int dirY) {
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
			continuer = droiteX >= 0 && droiteX < Plateau.TAILLE
					&& droiteY >= 0 && droiteY < Plateau.TAILLE;
			if (continuer) {
				jetonATester = plateau.getJeton(droiteX, droiteY);
				// Selon le type de jeton
				switch (jetonATester) {
				case VIDE:
					// Si l'on rencontre un jeton vide on est en boit de droite
					nb = 0;
					continuer = false;
					break;
				case NOIR:
				case BLANC:
					// Un jeton de joueur
					if (jetonATester == jeton) {
						// Si c'est un jeton du m�me jeton il faut qu'il y en
						// ait au moins un de couleur oppos�e entre (distance >
						// 0)
						continuer = false;
					} else {
						// Si l'on est sur un jeton de couleur oppos�e il faut
						// le retourner et continuer sur la droite
						nb++;
						continuer = true;
					}
					break;
				default:
					nb = 0;
					continuer = false;
				}
			}
		}

		return nb;
	}

	public static boolean peutJouer(Plateau plateau, Jeton jeton) {
		boolean possible;

		possible = false;
		// Parcours des cellules aux alentours
		for (int i = 0; !possible && i < Plateau.TAILLE; i++) {
			for (int j = 0; !possible && j < Plateau.TAILLE; j++) {
				// Une cellule adjacente?
				possible = coupPossible(plateau, i, j, jeton);
			}
		}

		return possible;
	}

}