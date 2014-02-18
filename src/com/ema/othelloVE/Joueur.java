package com.ema.othelloVE;

import com.ema.othelloVE.Plateau;

public class Joueur {
	
	   protected Jeton couleur;
	   protected Plateau plateau;
	   protected boolean isIA;
	   

	    public Joueur(Jeton couleurJeton, Plateau plateauOthello, boolean ia)
	    {	plateau = plateauOthello;
	        couleur = couleurJeton;
	        isIA = ia;
	    }
	   
	    public Jeton getCouleur()
	    {	return couleur;
	    }

	    public boolean isIA()
	    {	return isIA;
	    }


}
