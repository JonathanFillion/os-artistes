package concoursDeDessin;

import java.util.Arrays;


/**
 * 
 * Puisque les artistes prennent les crayons dans le même ordre, il n'y aura pas d'interblocages.
 * Par exemple, si art1, art2, art3 réservent le premier crayon. Les autres artistes attendront que le premier crayon soit libérer.
 * Les autres crayons seront prit par art1, art2, art3, dessineront leur dessin et relacheront ensuite les crayons.
 *
 */



public class Artiste extends Thread {
	boolean aUneFeuille = false;
	boolean ilNeRestePlusDeFeuilles = false;
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

		while (true) {
			// aquerir feuille
			if (etat == Etat.RIEN) {
				pileDeFeuilleSemaphore.P();
				if (parametresPubliques.nombreDeFeuilles >= 0) {
					feuilleId = parametresPubliques.nombreDeFeuilles;
					parametresPubliques.nombreDeFeuilles--;
					etat = Etat.POSSEDE_FEUIL;
					if (debug)
						System.out.println(Thread.currentThread().getName() + " prend " + feuilleId);
				} else {
					ilNeRestePlusDeFeuilles = true;
				}
				pileDeFeuilleSemaphore.V();
			}
			// Aquerir les crayons un a la suite de l'autre
			while (etat == Etat.POSSEDE_FEUIL) {
				if (!crayonsPossede[0]) {
					crayonsSemaphores[0].P();
					crayonsPossede[0] = true;
					testNombreDeCrayons++;
					augmenterCrayonsPubliques();
				}
				if (!crayonsPossede[1]) {
					crayonsSemaphores[1].P();
					crayonsPossede[1] = true;
					testNombreDeCrayons++;
					augmenterCrayonsPubliques();
				}
				if (!crayonsPossede[2]) {
					crayonsSemaphores[2].P();
					crayonsPossede[2] = true;
					testNombreDeCrayons++;
					augmenterCrayonsPubliques();
				}
				if (!crayonsPossede[3]) {
					crayonsSemaphores[3].P();
					crayonsPossede[3] = true;
					testNombreDeCrayons++;
					augmenterCrayonsPubliques();
				}

				if (possedeTousLesCrayons()) {
					etat = Etat.POSSEDE_FEUIL_ET_CRAYONS;
				}
			}
			// Pret a dessiner
			if (etat == Etat.POSSEDE_FEUIL_ET_CRAYONS) {
				String msg = Thread.currentThread().getName() + " fait un dessin sur feuille "
						+ getRealPageNumber();
				System.out.println(msg);
				parametresPubliques.sortie += "\n" + msg;
				etat = Etat.DESSIN_FAIT;
				// Relacher les crayons + relacher une semaphore du crayon i
				for (int i = 0; i < 4; i++) {
					crayonsPossede[i] = false;
					crayonsSemaphores[i].V();
					testNombreDeCrayons--;
				}
				reinitCrayonsPubliques();
			}
			// Remise du dessein
			if (etat == Etat.DESSIN_FAIT) {
				remettreDessinSemaphore.P();
				etat = Etat.RIEN;
				parametresPubliques.dessinRemis[getRealPageNumber()] = Thread.currentThread().getName();
				// Message d'affichage du devoir -- laisser en place
				String msg = Thread.currentThread().getName() + " a livré un dessin sur feuille "
						+ getRealPageNumber();
				System.out.println(msg);
				parametresPubliques.sortie += "\n" + msg;
				remettreDessinSemaphore.V();
			}

			if (ilNeRestePlusDeFeuilles && !possedeTousLesCrayons()) {
				// TODO changer un bool publique afin que fil "juge" declare un gagnant.
				if (debug) {
					System.out.println(Thread.currentThread().getName() + " termine au complet ");
				}
				this.aFini = true;
				break;
			}
		}
	}

	/**
	 * Fonction de debug
	 */
	public void augmenterCrayonsPubliques() {
		sharedData.nombreDeFeuillesParArtiste[id]++;
		if (debug)
			System.out.println(Arrays.toString(sharedData.nombreDeFeuillesParArtiste) + " par artiste " + this.id);
	}
	//Pour rendre les numeros de pages normaux de 0 a 49
	public int getRealPageNumber() {
		return Math.abs((this.feuilleId - parametresPubliques.paramInitNombre + 1));
	}
	

	/**
	 * Fonction de debug
	 */
	public void reinitCrayonsPubliques() {
		sharedData.nombreDeFeuillesParArtiste[id] = 0;
		if (debug)
			System.out.println(Arrays.toString(sharedData.nombreDeFeuillesParArtiste) + " par artiste " + this.id);
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
