package concoursDeDessin;

import java.util.Arrays;

/**
 * 
 * Puisque les artistes prennent les crayons dans le même ordre (d'abord rouge,
 * puis bleu, etc.), il n'y aura pas d'interblocages. Par exemple, si art1,
 * art2, art3 réservent le premier crayon. Les autres artistes attendront que
 * le premier crayon soit libérer. Les autres crayons seront prit par art1,
 * art2, art3, dessineront leur dessin et relacheront ensuite les crayons.
 *
 */

public class Artiste extends Thread {
	boolean aUneFeuille = false;
	boolean pileDeFeuillesEstVide = false;
	boolean debug = false;
	boolean aFini = false;

	private enum Etat {
		RIEN, POSSEDE_FEUIL, POSSEDE_FEUIL_ET_CRAYONS, DESSIN_FAIT, INTER
	}

	private Etat etat = Etat.RIEN;
	int feuilleId;
	// Crayons Rouge, Bleu, Jaune, Vert
	boolean[] possedeCrayon = { false, false, false, false };
	int id;
	int testNombreDeCrayons = 0;
	Semaphore pileDeFeuilleSemaphore;
	Semaphore remettreDessinSemaphore;
	// Semaphore des crayons Rouge, Bleu, Jaune, Vert
	Semaphore[] crayonsSemaphores;

	public Artiste(int id, Semaphore prendreFeuille, Semaphore remettreDessin, Semaphore[] crayons) {
		super("Artiste " + id);
		this.pileDeFeuilleSemaphore = prendreFeuille;
		this.remettreDessinSemaphore = remettreDessin;
		this.crayonsSemaphores = crayons;
		this.id = id;
	}

	public void run() {

		while (this.aFini == false) {
			// aquerir feuille
			if (etat == Etat.RIEN) {
				prendreFeuille();
			}

			// Aquerir les crayons un a la suite de l'autre
			while (etat == Etat.POSSEDE_FEUIL) {
				prendreCrayons();
			}

			// Pret a dessiner
			if (etat == Etat.POSSEDE_FEUIL_ET_CRAYONS) {
				dessiner();
			}

			// Remise du dessin
			if (etat == Etat.DESSIN_FAIT) {
				remettreDessin();
			}

			if (pileDeFeuillesEstVide && !possedeTousLesCrayons()) {
				// TODO changer un bool publique afin que fil "juge" declare un
				// gagnant.
				if (debug) {
					System.out.println(Thread.currentThread().getName() + " termine au complet ");
				}
				this.aFini = true;
			}
		}
	}

	private void prendreFeuille() {
		pileDeFeuilleSemaphore.P();
		if (parametresPubliques.nombreDeFeuilles >= 0) {
			feuilleId = parametresPubliques.nombreDeFeuilles;
			parametresPubliques.nombreDeFeuilles--;
			etat = Etat.POSSEDE_FEUIL;
			if (debug)
				System.out.println(Thread.currentThread().getName() + " prend " + feuilleId);
		} else {
			pileDeFeuillesEstVide = true;
		}
		Delay.attente(8); // Prendre une nouvelle feuille de papier nécessite un
							// certain temps
		pileDeFeuilleSemaphore.V();
	}

	private void prendreCrayons() {
		for (int i = 0; i < parametresPubliques.nombreDeCrayons; i++) {
			if (!possedeCrayon[i]) {
				crayonsSemaphores[i].P();
				possedeCrayon[i] = true;
				testNombreDeCrayons++;
				augmenterCrayonsPubliques();
			}
		}

		if (possedeTousLesCrayons()) {
			etat = Etat.POSSEDE_FEUIL_ET_CRAYONS;
		}
	}

	private void dessiner() {
		String msg = Thread.currentThread().getName() + " fait un dessin sur feuille " + getRealPageNumber();
		System.out.println(msg);
		parametresPubliques.sortie += "\n" + msg;
		etat = Etat.DESSIN_FAIT;

		// Relacher les crayons et leur semaphore associée
		for (int i = 0; i < parametresPubliques.nombreDeCrayons; i++) {
			possedeCrayon[i] = false;
			crayonsSemaphores[i].V();
			testNombreDeCrayons--;
		}

		reinitCrayonsPubliques();
	}

	/**
	 * Remettre le dessin au juge et relâcher les semaphores associées
	 */
	private void remettreDessin() {
		remettreDessinSemaphore.P();

		Delay.attente(8); // Retourner un dessin au jury et le placer dans la
							// pile de remise prend un certain temps

		etat = Etat.RIEN;
		parametresPubliques.dessinRemis[getRealPageNumber()] = Thread.currentThread().getName();
		// Message d'affichage du devoir -- laisser en place
		String msg = Thread.currentThread().getName() + " a livré un dessin sur feuille " + getRealPageNumber();
		System.out.println(msg);
		parametresPubliques.sortie += "\n" + msg;
		remettreDessinSemaphore.V();
	}

	public void augmenterCrayonsPubliques() {
		sharedData.nombreDeFeuillesParArtiste[id]++;
		if (debug) {
			System.out.println(Arrays.toString(sharedData.nombreDeFeuillesParArtiste) + " par artiste " + this.id);
		}
	}

	// Pour rendre les numeros de pages normaux de 0 a 49
	public int getRealPageNumber() {
		return Math.abs((this.feuilleId - parametresPubliques.paramInitNombre + 1));
	}

	public void reinitCrayonsPubliques() {
		sharedData.nombreDeFeuillesParArtiste[id] = 0;
		if (debug) {
			System.out.println(Arrays.toString(sharedData.nombreDeFeuillesParArtiste) + " par artiste " + this.id);
		}
	}

	/**
	 * Si l'artiste possede tous les crayons necessaires, retourne true
	 * 
	 * @return
	 */
	public boolean possedeTousLesCrayons() {
		return possedeCrayon[0] && possedeCrayon[1] && possedeCrayon[2] && possedeCrayon[3];
	}
}
