package concoursDeDessin;

public class Artiste extends Thread {
	boolean aUneFeuille = false;
	int feuilleId;
	// Crayon Rouge, Bleu, Jaune, Vert
	boolean[] crayonsPossede = { false, false, false, false };
	int id;
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
			if (!this.aUneFeuille) {
				pileDeFeuilleSemaphore.P();
				if (PileDeFeuilles.nombreDeFeuilles != 0) {
					feuilleId = PileDeFeuilles.nombreDeFeuilles;
					PileDeFeuilles.nombreDeFeuilles--;
					this.aUneFeuille = true;
					System.out.println(Thread.currentThread().getName() + " prend " + feuilleId);
				}
				pileDeFeuilleSemaphore.V();
			}
			while (true && !possedeTousLesCrayons() && this.aUneFeuille) {
				if (!crayonsPossede[0]) {
					crayonsSemaphores[0].P();
					crayonsPossede[0] = true;

				}
				if (!crayonsPossede[1]) {
					crayonsSemaphores[1].P();
					crayonsPossede[1] = true;

				}
				if (!crayonsPossede[2]) {
					crayonsSemaphores[2].P();
					crayonsPossede[2] = true;

				}
				if (!crayonsPossede[3]) {
					crayonsSemaphores[3].P();
					crayonsPossede[3] = true;

				}

				if (possedeTousLesCrayons()) {
					System.out.println(Thread.currentThread().getName() + " a tous ses crayons");
					break;
				}
			}
			
			if (possedeTousLesCrayons()) {
				for (int i = 0; i < 4; i++) {
					crayonsPossede[i] = false;
					crayonsSemaphores[i].V();
				}
				System.out.println(Thread.currentThread().getName() + " a relaché tous ses crayons");
			}
			
			if (this.aUneFeuille) {
				remettreDessinSemaphore.P();
				this.aUneFeuille = false;
				PileDeDessin.nombreDessinRemis++;
				System.out.println(Thread.currentThread().getName() + " remet " + feuilleId);
				remettreDessinSemaphore.V();
			}

			
		}
	}

	public boolean possedeTousLesCrayons() {
		return crayonsPossede[0] && crayonsPossede[1] && crayonsPossede[2] && crayonsPossede[3];
	}
}
