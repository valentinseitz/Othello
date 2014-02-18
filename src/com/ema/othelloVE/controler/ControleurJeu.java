package com.ema.othelloVE.controler;

import java.util.ArrayList;

import com.ema.othelloVE.gui.IhmPlateau;
import com.ema.othelloVE.gui.IhmScore;
import com.ema.othelloVE.model.Coup;
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

	public ControleurJeu(int level, boolean IANoir, boolean IA) {
		// initialisation du plateau
		// nitialisationdes joueurs en fonction de la s�lection de l'interface
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
		// initialisation du lien entre le contr�leur et l'ihm plateau
		ihmPlateau = ihm;
	}

	public void setIhmScore(IhmScore score) {
		// initialisation fdu lien entre le contr�leur et l'ihm score
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
		// thread du contr�leur
		// attente d' �v�nements fournis par l'interface graphique (EventMotion)
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

		if (joueurEnCours.isIA()) {
			((JoueurIA) joueurEnCours).calculCoup();
		}
		while (!fin) {
			MyEvent event = null;
			synchronized (events) {
				try {
					if (events.isEmpty()) {
						events.wait();
					}
					event = events.remove(0);
					// L'evenement vient-il du joueur dont c'est le tour de
					// jouer?
					if (event.getJoueur() == joueurEnCours) {
						// v�rifier si coup valide
						if (ControleurPlateau.coupPossible(plateau,
								event.getX(), event.getY(),
								joueurEnCours.getCouleur())) {
							// mettre � jour le plateau par retournement des
							// pions
							// exemple : mise � jour du plateau par pion jou�
							// par l'humain :
							plateau.setJeton(event.getX(), event.getY(),
									joueurEnCours.getCouleur());
							ControleurPlateau.retournePions(plateau,
									event.getX(), event.getY());
							// faire le changement du joueur courant
							changeJoueurEnCours();

							// mise � jour de l'affichage
							updateUI();
						}

						// verification si joueur en cours peut jouer
						if (!ControleurPlateau.peutJouer(plateau,
								joueurEnCours.getCouleur())) {
							// faire le changement du joueur courant
							changeJoueurEnCours();
							// mise � jour de l'affichage
							updateUI();
							// Si l'autre joueur non plus ne peut pas jouer,
							// c'est
							// forc�ment la fin de la partie
							if (!ControleurPlateau.peutJouer(plateau,
									joueurEnCours.getCouleur())) {
								fin = true;
							}
						}

						// si joueur en cours est de type IA : mettre � jour
						// iaReflechi et lancer la demande de calcul du coup:
						if (!fin && joueurEnCours.isIA()) {
							((JoueurIA) joueurEnCours).calculCoup();
						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// fin de partie : mise � jour de l'interface score
		updateMessageFin();
	}

	protected void updateUI() {
		// mise � jour des interfaces et affichage
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
		if (!joueurEnCours.isIA()){
			publishEvent(x, y, joueurEnCours);
		}
	}

	public void publishEvent(int x, int y, Joueur joueur) {
		MyEvent event;
		event = new MyEvent(joueur, x, y);
		// abonnement aux �v�nements �mis par le joueur IA
		synchronized (events) {
			events.add(event);
			events.notifyAll();
		}
	}

	private void updateMessageFin() { // mise � jour interface score et
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
			msg = "FIN DE LA PARTIE : " + gagnant + " a gagn� !";

		ihmScore.setScoreFin(msg, plateau.nombreJetons(Jeton.BLANC),
				plateau.nombreJetons(Jeton.NOIR), joueurEnCours.getCouleur());
		ihmScore.postInvalidate();

	}

}