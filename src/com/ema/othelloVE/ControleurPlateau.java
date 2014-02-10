package com.ema.othelloVE;

public class ControleurPlateau {
	
	public boolean coupPossible(Plateau plateau, int x, int y, byte jeton){
		boolean possible;
		
		if (plateau.getJeton(x, y) == Jeton.VIDE) {
			possible = true;
		} else {
			possible = false;
		}
		
		return possible;
	}

}
