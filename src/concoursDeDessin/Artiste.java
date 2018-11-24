package concoursDeDessin;

public class Artiste extends Thread {
	boolean aUneFeuille = false;
	boolean ilNeRestePlusDeFeuilles = false;
	private enum Etat {RIEN,POSSEDE_FEUIL, POSSEDE_FEUIL_ET_CRAYONS, DESSIN_FAIT}
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
	}

	public void run() {

		while (true) {
			if (etat == Etat.RIEN) {
				pileDeFeuilleSemaphore.P();
				if (PileDeFeuilles.nombreDeFeuilles != 0) {
					feuilleId = PileDeFeuilles.nombreDeFeuilles;
					PileDeFeuilles.nombreDeFeuilles--;
					etat = Etat.POSSEDE_FEUIL;
					System.out.println(Thread.currentThread().getName() + " prend " + feuilleId);
				} else {
					ilNeRestePlusDeFeuilles = true;
				}
				pileDeFeuilleSemaphore.V();
			}
			while (etat == Etat.POSSEDE_FEUIL) {
				if (!crayonsPossede[0]) {
					crayonsSemaphores[0].P();
					crayonsPossede[0] = true;
					testNombreDeCrayons++;
				}
				if (!crayonsPossede[1]) {
					crayonsSemaphores[1].P();
					crayonsPossede[1] = true;
					testNombreDeCrayons++;
				}
				if (!crayonsPossede[2]) {
					crayonsSemaphores[2].P();
					crayonsPossede[2] = true;
					testNombreDeCrayons++;
				}
				if (!crayonsPossede[3]) {
					crayonsSemaphores[3].P();
					crayonsPossede[3] = true;
					testNombreDeCrayons++;
				}

				if (possedeTousLesCrayons()) {
					//System.out.println(Thread.currentThread().getName() + " a tous ses crayons. Nombre = " + testNombreDeCrayons);
					etat = Etat.POSSEDE_FEUIL_ET_CRAYONS;
				}
			}
			
			if (etat == Etat.POSSEDE_FEUIL_ET_CRAYONS) {
				System.out.println(Thread.currentThread().getName() + " fait un dessin sur feuille " + this.feuilleId);
				etat = Etat.DESSIN_FAIT;
				for (int i = 0; i < 4; i++) {
					crayonsPossede[i] = false;
					crayonsSemaphores[i].V();
					testNombreDeCrayons--;
				}
			}
			
			if (etat == Etat.DESSIN_FAIT) {
				remettreDessinSemaphore.P();
				etat = Etat.RIEN;
				PileDeDessin.nombreDessinRemis++;
				System.out.println(Thread.currentThread().getName() + " remet " + feuilleId);
				remettreDessinSemaphore.V();
			}

			if(ilNeRestePlusDeFeuilles && !possedeTousLesCrayons()) {
				System.out.println(Thread.currentThread().getName() + " termine au complet ");
				break;
			}
		}
	}

	public boolean possedeTousLesCrayons() {
		return crayonsPossede[0] && crayonsPossede[1] && crayonsPossede[2] && crayonsPossede[3];
	}
}
