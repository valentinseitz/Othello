package com.ema.othelloVE;

public class ControleurPlateau {
	
	public static boolean coupPossible(Plateau plateau, int x, int y, byte jeton){
		boolean possible;
		
		//Pourqu'il soit possible de placer le jeton, la place doit être vide
		possible = plateau.getJeton(x, y) == Jeton.VIDE;
		//Eventuellement possible?
		if (possible){
			//Le placement est éventuellement possible
			//Le placement n'est possible que s'il y a une droite qui peut se former
			possible = false;
			//Parcours des cellules aux alentours
			for (int i =  -1; !possible && i <= 1; i++){
				for (int j =  -1; !possible && j <= 1; j++){
					//Une cellule adjacente?
					if (i != 0 || j != 0){
						//La droite donnée par la cellule testée et la cellule adjacente est-elle possible?
						possible = droitePossible(plateau, x, y, jeton, i, j);
					}
				}
			}
		}
		return possible;
	}
	
	private static boolean droitePossible(Plateau plateau, int x, int y, byte jeton, int dirX, int dirY){
		boolean possible;
		int distance;
		int droiteX;
		int droiteY;
		byte jetonATester;
		boolean continuer;
		
		continuer = true;
		possible = true;
		droiteX = x;
		droiteY = y;
		distance = 0;
		while (possible && continuer){
			droiteX = droiteX + dirX;
			droiteY = droiteY + dirY;
			//Coordonnées dans le plateau?
			possible = droiteX >= 0 && droiteX < plateau.getNbLignes() && droiteY >= 0 && droiteY < plateau.getNbLignes();
			if (possible){
				//Quel est le jeton à tester?
				jetonATester = plateau.getJeton(droiteX, droiteY);
				//Selon le type de jeton
				switch (jetonATester){
					case Jeton.VIDE :
						//Si l'on rencontre un jeton vide la droite est impossible
						possible = false;
						continuer = false;
						break;
					case Jeton.NOIR :
					case Jeton.BLANC : 
						//Un jeton de joueur
						if (jetonATester == jeton){
							//Si c'est un jeton du même jeton il faut qu'il y en ait au moins un de couleur opposée entre (distance > 0)
							possible = distance > 0;
							continuer = false;
						} else {
							//Si l'on est sur un jeton de couleur opposée il faut regarder le suivant sur la droite pour savoir si la droite permet un placement
							possible = true;
							continuer = true;
						}
						break;
					default:
						possible = false;
						continuer = false;
				}
				//On va un jeton plus loin
				distance++;
			} else {
				//pas la peine de continuer, on a dépassé le bord du plateau
				continuer = false;
			}
		}
		
		return possible;
	}
	
	public static void retournePions(Plateau plateau, int x, int y){
		//Parcours des cellules aux alentours
		for (int i =  -1; i <= 1; i++){
			for (int j =  -1; j <= 1; j++){
				//Une cellule adjacente?
				if (i != 0 || j != 0){
					//La droite donnée par la cellule testée et la cellule adjacente est-elle possible?
					if (droitePossible(plateau, x, y, plateau.getJeton(x, y), i, j)){
						retourneDroite(plateau, x, y, i, j);
					}
				}
			}
		}
	}
	
	private static void retourneDroite(Plateau plateau, int x, int y, int dirX, int dirY){
		byte jeton;
		int droiteX;
		int droiteY;
		byte jetonARetourner;
		boolean continuer;
		
		droiteX = x;
		droiteY = y;
		jeton = plateau.getJeton(x, y);
		continuer = true;
		while (continuer){
			droiteX = droiteX + dirX;
			droiteY = droiteY + dirY;
			jetonARetourner = plateau.getJeton(droiteX, droiteY);
			//Selon le type de jeton
			switch (jetonARetourner){
				case Jeton.VIDE :
					//Si l'on rencontre un jeton vide on est en boit de droite
					continuer = false;
					break;
				case Jeton.NOIR :
				case Jeton.BLANC : 
					//Un jeton de joueur
					if (jetonARetourner == jeton){
						//Si c'est un jeton du même jeton il faut qu'il y en ait au moins un de couleur opposée entre (distance > 0)
						continuer = false;
					} else {
						//Si l'on est sur un jeton de couleur opposée il faut le retourner et continuer sur la droite
						plateau.setPlateau(droiteX, droiteY, jeton);
						continuer = true;
					}
					break;
				default:
					continuer = false;
			}
		}
	}

}
