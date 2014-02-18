package com.ema.othelloVE.gui;

import com.ema.othelloVE.controler.ControleurJeu;
import com.ema.othelloVE.model.Coup;
import com.ema.othelloVE.model.Jeton;
import com.ema.othelloVE.model.Plateau;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class IhmPlateau extends View {

	private static final int NB_LIG = 8;
	private static final int NB_COL = NB_LIG;
	private static final int NB_COORD = 4;

	private int tailleJeton;
	private int tailleGrille;
	private int largeurPlateau;
	private Plateau othellier;
	private ControleurJeu controleur;
	private Context context;
	private Coup lastCoup;


	public IhmPlateau(Context aContext, AttributeSet aAttrs) {
		super(aContext, aAttrs);
		context=aContext;
	}


	public void setControleur(ControleurJeu jeu) {
		controleur = jeu;
	}

	public void initPlateau(Plateau plateau) {
		othellier = plateau;
	}
	
	public void initlastCoup(Coup coup)
	{
		lastCoup=coup;
	}

	protected void onDraw(Canvas aCanvas) {
		int width = getWidth();
		int height = getHeight();

		Log.v(this.toString(), "onDraw " + width + height);

		// orientation tablette
		if (width > height) {
			// landscape
			largeurPlateau = height;
		} else {
			// portrait
			largeurPlateau = width;
		}

		// calcul taille Grille
		if ((largeurPlateau % NB_LIG) >= 0.5)
			tailleGrille = largeurPlateau / NB_LIG + 1;
		else
			tailleGrille = largeurPlateau / NB_LIG;

		// ajustement taille Jeton
		tailleJeton = tailleGrille / 2 - 2;

		Log.v(this.toString(), "onDraw " + tailleGrille);

		// affichage plateau
		affichePlateau(aCanvas);

		// affichage jetons
		try {
			
			afficheJetons(aCanvas);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

		
	protected void afficheJetons(Canvas aCanvas) {
		Log.v(this.toString(), "drawButtons");
		int x, y; // coordonnées affichage
		
		if (othellier != null) {

			for (int i = 0; i < NB_COL; i++) {
				for (int j = 0; j < NB_LIG; j++) {
					Jeton jeton = othellier.getJeton(i, j);
					if( (jeton != Jeton.VIDE) || (lastCoup!=null && lastCoup.getLigne()== i && lastCoup.getColonne()==j)){
						x = (i * tailleGrille) + tailleGrille / 2;
						y = (j * tailleGrille) + tailleGrille / 2;
						afficheJeton(aCanvas, x, y, jeton);
					}
				}
			}
		}
		Log.v(this.toString(), "drawButtons <<");
	}

	protected void afficheJeton(Canvas aCanvas, int aX, int aY, Jeton aColor) {
		Log.v(this.toString(), "drawButton");
		Paint paint = new Paint();

		paint.setColor(aColor.getCouleur());

		aCanvas.drawCircle(aX, aY, tailleJeton, paint);
	}

	protected void affichePlateau(Canvas aCanvas) {
		// grille : 14 lignes, 4 coordonnées par ligne
		float grid[] = new float[(NB_COL - 1 + NB_LIG - 1) * NB_COORD];

		Log.v(this.toString(), "affichagePlateau");

		Paint green = new Paint();
		green.setColor(Color.GREEN);

		Rect r = new Rect(0, 0, largeurPlateau, largeurPlateau);
		aCanvas.drawRect(r, green);

		// white grid
		Paint white = new Paint();
		white.setColor(Color.WHITE);

		for (int i = 0; i < NB_LIG - 1; i++) {
			grid[NB_COORD * i] = 0;
			grid[NB_COORD * i + 1] = tailleGrille * (i + 1);
			grid[NB_COORD * i + 2] = largeurPlateau;
			grid[NB_COORD * i + 3] = tailleGrille * (i + 1);
		}

		int verticalStart = (NB_COL - 1) * NB_COORD;

		for (int i = 0; i < NB_COL - 1; i++) {
			//
			grid[NB_COORD * i + verticalStart] = tailleGrille * (i + 1);
			grid[NB_COORD * i + verticalStart + 1] = 0;
			grid[NB_COORD * i + verticalStart + 2] = tailleGrille * (i + 1);
			grid[NB_COORD * i + verticalStart + 3] = largeurPlateau;
		}

		aCanvas.drawLines(grid, white);
	}

	@Override
	public boolean onTouchEvent(MotionEvent aEvent) {

		if (aEvent.getAction() == MotionEvent.ACTION_DOWN) {
			Point place = positionSurPlateau(aEvent.getX(), aEvent.getY());
			if (place.x < NB_COL && place.y < NB_LIG) {
				controleur.publishEvent(place.x, place.y);
			}
		}
		return true;
	}

	protected Point positionSurPlateau(float aX, float aY) {
		Point place = new Point(NB_COL + 1, NB_LIG + 1);

		if (aX >= 0 && aX <= tailleGrille * NB_COL) {
			if (aY >= 0 && aY <= tailleGrille * NB_LIG) {
				place.x = (int) aX / tailleGrille;
				place.y = (int) aY / tailleGrille;
			}
		}
		return place;
	}

}