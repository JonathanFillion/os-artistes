package concoursDeDessin;

import java.util.Arrays;


/**
 * 
 * Puisque les artistes prennent les crayons dans le mÃªme ordre (d'abord rouge, puis bleu, etc.), il n'y aura pas d'interblocages.
 * Par exemple, si art1, art2, art3 rÃ©servent le premier crayon. Les autres artistes attendront que le premier crayon soit libÃ©rer.
 * Les autres crayons seront prit par art1, art2, art3, dessineront leur dessin et relacheront ensuite les crayons.
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
	// Crayon Rouge, Bleu, Jaune, Vert
	boolean[] crayonsPossede = { false, false, false, false };
	int id;
	int testNombreDeCrayons = 0;
	Semaphore pileDeFeuilleSemaphore;
	Semaphore remettreDessinSemaphore;
	// Semaphore Crayon Rouge, Bleu, Jaune, Vert
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
				// TODO changer un bool publique afin que fil "juge" declare un gagnant.
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
		pileDeFeuilleSemaphore.V();
	}
	
	private void prendreCrayons() {
		for (int i = 0; i < parametresPubliques.nombreDeCrayons; i++) {
			if (!crayonsPossede[i]) {
				crayonsSemaphores[i].P();
				crayonsPossede[i] = true;
				testNombreDeCrayons++;
				augmenterCrayonsPubliques();
			}
		}

		if (possedeTousLesCrayons()) {
			etat = Etat.POSSEDE_FEUIL_ET_CRAYONS;
		}
	}
	
	private void dessiner() {
		String msg = Thread.currentThread().getName() + " fait un dessin sur feuille "
				+ getRealPageNumber();
		System.out.println(msg);
		parametresPubliques.sortie += "\n" + msg;
		etat = Etat.DESSIN_FAIT;
		
		// Relacher les crayons et leur semaphore associée
		for (int i = 0; i < parametresPubliques.nombreDeCrayons; i++) {
			crayonsPossede[i] = false;
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
		etat = Etat.RIEN;
		parametresPubliques.dessinRemis[getRealPageNumber()] = Thread.currentThread().getName();
		// Message d'affichage du devoir -- laisser en place
		String msg = Thread.currentThread().getName() + " a livrÃ© un dessin sur feuille "
				+ getRealPageNumber();
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
	
	//Pour rendre les numeros de pages normaux de 0 a 49
	public int getRealPageNumber() {
		return Math.abs((this.feuilleId - parametresPubliques.paramInitNombre + 1));
	}
	
	public void reinitCrayonsPubliques() {
		sharedData.nombreDeFeuillesParArtiste[id] = 0;
		if (debug){
			System.out.println(Arrays.toString(sharedData.nombreDeFeuillesParArtiste) + " par artiste " + this.id);
		}
	}

	/**
	 * Si l'artiste possede tous les crayons necessaires, retourne true
	 * 
	 * @return
	 */
	public boolean possedeTousLesCrayons() {
		return crayonsPossede[0] && crayonsPossede[1] && crayonsPossede[2] && crayonsPossede[3];
	}
}
