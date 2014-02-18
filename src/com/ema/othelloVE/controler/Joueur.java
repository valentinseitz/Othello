package com.ema.othelloVE.controler;

import com.ema.othelloVE.model.Jeton;
import com.ema.othelloVE.model.Plateau;

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
