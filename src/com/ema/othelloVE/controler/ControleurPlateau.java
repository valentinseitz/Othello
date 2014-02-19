package com.ema.othelloVE.controler;

import com.ema.othelloVE.model.Jeton;
import com.ema.othelloVE.model.Plateau;

public class ControleurPlateau {

	public static boolean peutJouer(Plateau plateau, Jeton jeton) {
		boolean possible;

		possible = false;
		// Parcours des cellules aux alentours
		for (int i = 0; !possible && i < Plateau.TAILLE; i++) {
			for (int j = 0; !possible && j < Plateau.TAILLE; j++) {
				// Une cellule adjacente?
				possible = nbRetournements(plateau, i, j, jeton, false) > 0;
			}
		}

		return possible;
	}

	public static int nbRetournements(Plateau plateau, int x, int y, Jeton jeton, boolean retourner) {
		int nb = 0;
		if (plateau.getJeton(x, y) == Jeton.VIDE){
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					// Une cellule adjacente?
					if (i != 0 || j != 0) {
						// La droite donnée par la cellule testée et la cellule adjacente est-elle possible?
						nb += nbRetournements(plateau, x, y, jeton, i, j, retourner);
					}
				}
			}
		}
		return nb;
	}

	private static int nbRetournements(Plateau plateau, int x, int y, Jeton jeton, int dirX, int dirY,
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
			
			jetonATester = plateau.getJeton(droiteX, droiteY);
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
				plateau.setJeton(droiteX, droiteY, jeton);
			}while(droiteX != x || droiteY != y);
		}
		
		return nb;
	}

}
