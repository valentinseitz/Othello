package com.ema.othelloVE.controler;

import java.util.ArrayList;

import com.ema.othelloVE.gui.IhmPlateau;
import com.ema.othelloVE.gui.IhmScore;
import com.ema.othelloVE.model.Jeton;
import com.ema.othelloVE.model.MyEvent;
import com.ema.othelloVE.model.Plateau;

import android.util.Log;

public class ControleurJeu implements Runnable {

	private Plateau plateau;
	private IhmPlateau ihmPlateau;
	private IhmScore ihmScore;

	private int niveauIA;
	private boolean isIANoir;
	private boolean isIA;

	private Joueur joueur1;
	private Joueur joueur2;
	private Joueur joueurEnCours;

	private final String TAG = ControleurJeu.class.getSimpleName();

	public ControleurJeu(int level) {
		niveauIA = level;
		plateau = new Plateau();
		joueur1 = new JoueurIA(Jeton.NOIR, plateau, niveauIA, this);
		joueur2 = new JoueurIA(Jeton.BLANC, plateau, niveauIA, this);
		joueurEnCours = joueur1;
	}

	public ControleurJeu(int level, boolean IANoir, boolean IA) {
		// initialisation du plateau
		// nitialisationdes joueurs en fonction de la sélection de l'interface
		// utilisateur
		niveauIA = level;
		isIANoir = IANoir;
		isIA = IA;
		plateau = new Plateau();

		if (!isIA) {
			joueur1 = new Joueur(Jeton.NOIR, plateau, false);
			joueur2 = new Joueur(Jeton.BLANC, plateau, false);
			joueurEnCours = joueur1;
		} else {
			if (isIANoir) {
				joueur1 = new JoueurIA(Jeton.NOIR, plateau, niveauIA, this);
				joueur2 = new Joueur(Jeton.BLANC, plateau, false);
				joueurEnCours = joueur1;

			} else {
				joueur2 = new JoueurIA(Jeton.BLANC, plateau, niveauIA, this);
				joueur1 = new Joueur(Jeton.NOIR, plateau, false);
				joueurEnCours = joueur1;
			}
		}

	}

	public void setIhm(IhmPlateau ihm) {
		// initialisation du lien entre le contrôleur et l'ihm plateau
		ihmPlateau = ihm;
	}

	public void setIhmScore(IhmScore score) {
		// initialisation fdu lien entre le contrôleur et l'ihm score
		ihmScore = score;
	}

	public void start() {
		// initialisation des interfaces et affichage
		plateau.initPlateau();
		ihmPlateau.initPlateau(plateau);
		ihmScore.setScore(plateau.nombreJetons(Jeton.BLANC),
				plateau.nombreJetons(Jeton.NOIR), Jeton.NOIR);
		updateUI();
	}

	public void changeIACouleur(boolean IANoir) {
		// modification de la couleur de l'automate par demande de l'interface
		isIANoir = IANoir;
	}

	public void changeNiveauIA(int level) {
		// modification du niveau de l'automate par demande de l'interface
		niveauIA = level;
	}

	private ArrayList<MyEvent> events = new ArrayList<MyEvent>();

	@Override
	public void run() {
		// thread du contrôleur
		// attente d' événements fournis par l'interface graphique (EventMotion)
		// si mode de jeu manuel
		// ou par l'automate (EventCoupIA) et par l'interface graphique
		// (EventMotion) alternativement si mode de jeu semi-automatique

		Log.v(TAG, "start");
		boolean fin = false;
		plateau.initPlateau();
		ihmPlateau.initPlateau(plateau);
		ihmScore.setScore(plateau.nombreJetons(Jeton.BLANC),
				plateau.nombreJetons(Jeton.NOIR), Jeton.NOIR);
		updateUI();
		while (!fin) {
			if (joueurEnCours.isIA()) {
				((JoueurIA) joueurEnCours).calculCoup();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			MyEvent event = null;
			while (event == null || event.getJoueur() != joueurEnCours) {
				synchronized (events) {
					try {
						if (events.isEmpty()) {
							events.wait();
						}
						event = events.remove(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (plateau.coupPossible(event.getX(),
					event.getY(), joueurEnCours.getCouleur(), true)) {
				// faire le changement du joueur courant
				changeJoueurEnCours();
			}

			// verification si joueur en cours peut jouer
			if (!plateau.peutJouer(
					joueurEnCours.getCouleur())) {
				// faire le changement du joueur courant
				changeJoueurEnCours();
				// Si l'autre joueur non plus ne peut pas jouer
				if (!plateau.peutJouer(
						joueurEnCours.getCouleur())) {
					// C'est forcément la fin de la partie
					fin = true;
				}
			}
			// mise à jour de l'affichage
			updateUI();
		}
		// fin de partie : mise à jour de l'interface score
		updateMessageFin();
	}

	protected void updateUI() {
		// mise à jour des interfaces et affichage
		ihmScore.setScore(plateau.nombreJetons(Jeton.BLANC),
				plateau.nombreJetons(Jeton.NOIR), joueurEnCours.getCouleur());

		ihmPlateau.postInvalidate();
		ihmScore.postInvalidate();
	}

	private void changeJoueurEnCours()
	// modification du joueur en cours
	{
		if (joueurEnCours == joueur1)
			joueurEnCours = joueur2;
		else
			joueurEnCours = joueur1;

	}

	public void publishEvent(int x, int y) {
		if (!joueurEnCours.isIA()) {
			publishEvent(x, y, joueurEnCours);
		}
	}

	public void publishEvent(int x, int y, Joueur joueur) {
		MyEvent event;
		event = new MyEvent(joueur, x, y);
		// abonnement aux événements émis par le joueur IA
		synchronized (events) {
			events.add(event);
			events.notifyAll();
		}
	}

	private void updateMessageFin() { // mise à jour interface score et
										// affichage
		String gagnant = "", msg;
		Boolean egalite = false;
		if (plateau.nombreJetons(Jeton.BLANC) > plateau
				.nombreJetons(Jeton.NOIR))
			gagnant = "BLANC";
		else if (plateau.nombreJetons(Jeton.BLANC) < plateau
				.nombreJetons(Jeton.NOIR))
			gagnant = "NOIR";
		else
			egalite = true;
		if (egalite)
			msg = "FIN DE LA PARTIE :  EGALITE ENTRE LES JOUEURS !";
		else
			msg = "FIN DE LA PARTIE : " + gagnant + " a gagné !";

		ihmScore.setScoreFin(msg, plateau.nombreJetons(Jeton.BLANC),
				plateau.nombreJetons(Jeton.NOIR), joueurEnCours.getCouleur());
		ihmScore.postInvalidate();

	}

}