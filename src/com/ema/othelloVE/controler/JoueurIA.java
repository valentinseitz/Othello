package com.ema.othelloVE.controler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ema.othelloVE.model.Coup;
import com.ema.othelloVE.model.Jeton;
import com.ema.othelloVE.model.Plateau;

import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;

public class JoueurIA extends Joueur {

	private int force;
	private Coup p;

	private static final int DEBUTANT = 1;
	private static final int MOYEN = 2;
	private static final int EXPERT = 3;
	private ControleurJeu controlJeu;

	public JoueurIA(Jeton couleur, Plateau plateau, int niveau,
			ControleurJeu control) {
		super(couleur, plateau, true);
		force = niveau;
		controlJeu = control;
	}

	private class JoueurExpert extends AsyncTask<Void, Void, Coup> {
		// Classe permettant de lancer une tâche en fond(pour les calculs longs)
		protected void onPreExecute() {
		}

		protected Coup doInBackground(Void... aVoid) {
			p = calculCoupExpert();
			return p;
		}

		protected void onPostExecute(Coup coupExpert) {
			p = coupExpert;
			sendResult(p);
		}
	}

	private void sendResult(Coup coup) { // retour du coup calculé au contrôleur
		controlJeu.publishEvent(coup.getX(), coup.getY(), this);
	}

	public void calculCoup() { // calcul du coup à jouer en fonction de la force
								// du joueurIA
		p = null;
		switch (force) {
		case DEBUTANT:
			p = calculCoupDebutant(); // fonction courte qui peut s'exécuter
										// suite à la demande du contrôleur
			sendResult(p);
			break;
		case MOYEN:
			p = calculCoupMoyen(); // fonction courte qui peut s'exécuter suite
									// à la demande du contrôleur
			sendResult(p);
			break;
		case EXPERT:
			new JoueurExpert().execute(); // fonction chronophage : à lancer en
											// tâche de fond
			break;

		}

	}

	private Coup calculCoupDebutant() { // retourne un coup possible choisi
										// aléatoirement
		List<Point> possibles = new ArrayList<Point>();
		Random rand = new Random();
		int index;
		Coup coup;
		for (int i = 0; i < Plateau.TAILLE; i++) {
			for (int j = 0; j < Plateau.TAILLE; j++) {
				if (ControleurPlateau.nbRetournements(plateau, i, j,
						this.couleur, false) > 0) {
					possibles.add(new Point(i, j));
				}
			}
		}
		if (possibles.size() > 0) {
			index = rand.nextInt(possibles.size());
			coup = new Coup(possibles.get(index).x, possibles.get(index).y,
					this.couleur);
		} else {
			coup = null;
		}

		return coup;

	}

	private Coup calculCoupMoyen() { // retourne le coup qui maximise les
										// retournements
										// sur arbre de recherche développé à 1
										// niveau
		// Retourne le meilleur coup obtenu par recherche Minmax sur arbre de
		// recherche développé à 2 ou 3 niveaux
		return alphaBeta(this.plateau, this.couleur, 1);
	}

	private Coup calculCoupExpert() {
		// Retourne le meilleur coup obtenu par recherche Minmax sur arbre de
		// recherche développé à 2 ou 3 niveaux
		return alphaBeta(this.plateau, this.couleur, 3);
	}

	private Coup alphaBeta(Plateau plateau, Jeton joueur, int profondeur) {
		// Initialisation à -infini pour alpha et +infini pour beta
		List<Point> possibles;
		List<Point> meilleursPossibles = new ArrayList<Point>();
		int heuristique;
		int maxHeuristique;

		maxHeuristique = Integer.MIN_VALUE;
		possibles = ControleurPlateau.coupsPossibles(plateau, joueur);
		for (Point coup : possibles){
				heuristique = alphaBeta(new Noeud(plateau, coup,
						joueur, profondeur), Integer.MIN_VALUE,
						Integer.MAX_VALUE);
				if (heuristique >= maxHeuristique) {
					if (heuristique > maxHeuristique) {
						meilleursPossibles.clear();
						maxHeuristique = heuristique;
					}
					meilleursPossibles.add(coup);
				}
		}

		Random rand = new Random();
		int index;
		Coup coup;
		if (meilleursPossibles.size() > 0) {
			index = rand.nextInt(meilleursPossibles.size());
			coup = new Coup(meilleursPossibles.get(index).x, meilleursPossibles.get(index).y,
					this.couleur);
		} else {
			coup = null;
		}

		return coup;
	}

	private int alphaBeta(Noeud noeud, int alpha, int beta) {
		//Alpha est toujous inférieur à beta
		int heuristique;
		boolean coupe = false; // P à vrai s'il y a une coupure alpha ou beta
		if (noeud.isFeuille()) {
			heuristique = noeud.getHeuristique();
		} else {
			if (!noeud.isMax()) {
				heuristique = Integer.MAX_VALUE;// +infini
				while (!coupe && noeud.aFilsSuivant()) {
					heuristique = Math.min(heuristique,
							alphaBeta(noeud.getFilsSuivant(), alpha, beta));
					if (alpha >= heuristique) {
						// Coupure beta
						coupe = true;
					} else {
						beta = Math.min(beta, noeud.getHeuristique());
					}
				}
			} else {
				heuristique = Integer.MIN_VALUE;// -infini
				while (!coupe && noeud.aFilsSuivant()) {
					heuristique = Math.max(heuristique,
							alphaBeta(noeud.getFilsSuivant(), alpha, beta));
					if (beta <= heuristique) {
						// Coupure beta
						coupe = true;
					} else {
						alpha = Math.max(alpha, noeud.getHeuristique());
					}
				}
			}
		}
		return heuristique;
	}

	private class Noeud {

		boolean max;
		int profondeur;
		Jeton joueur;
		Plateau plateau;
		List<Point> coupsPossibles;
		int coupCourant;

		public Noeud(Plateau plateau, Point coup, Jeton joueur, int profondeur) {
			this(plateau, coup, joueur, profondeur, true);
		}

		private Noeud(Plateau plateau, Point coup, Jeton joueur,
				int profondeur, boolean max) {
			Jeton joueurNoeud;
			// Niveau max ou pas?
			this.max = max;
			this.joueur = joueur;
			// Quelle profondeur et quel joueur?
			if (this.max) {
				this.profondeur = profondeur - 1;
				joueurNoeud = this.joueur;
			} else {
				this.profondeur = profondeur;
				joueurNoeud = this.joueur.getAdversaire();
			}
			// Clone du plateau
			this.plateau = new Plateau(plateau);
			// Un coup à jouer?
			// On joue le coup
			if (coup != null) {
				ControleurPlateau.nbRetournements(this.plateau, coup.x, coup.y,
						joueurNoeud, true);
			}
			//
			// On demande les coups possibles de l'adversaire
			coupsPossibles = ControleurPlateau.coupsPossibles(plateau,
					joueurNoeud.getAdversaire());
			// Pas encore de coup courant
			coupCourant = -1;
		}

		public boolean isFeuille() {
			// La profondeur suivante est 0, on est donc sur la feuille
			return profondeur <= 1 && !this.max;
		}

		public boolean isMax() {
			// Noeud de niveau max
			return this.max;
		}

		public int getHeuristique() {
			// Le nombre de jeton du joueur, moins celui de son adversaire
			return plateau.nombreJetons(joueur)
					- plateau.nombreJetons(joueur.getAdversaire());
		}

		public boolean aFilsSuivant() {
			// Il y a un autre coup possible
			return !isFeuille() && coupCourant + 1 < coupsPossibles.size() || (coupCourant==-1 && coupsPossibles.size()==0);
		}

		public Noeud getFilsSuivant() {
			Noeud fils;
			if (aFilsSuivant()) {
				// On passe sur le coup suivant
				coupCourant++;
					// On crée un nouveau noeud qui correspond au coup
				if (coupCourant < coupsPossibles.size()){
					fils = new Noeud(this.plateau,
							coupsPossibles.get(coupCourant), joueur,
							this.profondeur, !max);
				} else {
					fils = new Noeud(this.plateau,
							null, joueur,
							this.profondeur, !max);
				}
			} else {
				// Il n'y a pas de fils suivant
				fils = null;
			}
			return fils;
		}

	}

}