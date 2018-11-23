package concoursDeDessin;

public class Artiste extends Thread {
	boolean aUneFeuille = false;
	int id;
	Semaphore pileDeFeuilleSemaphore;
	Semaphore remettreDessinSemaphore;

	public Artiste(int id, Semaphore prendreFeuille, Semaphore remettreDessin) {
		super("Artiste " + id);
		this.pileDeFeuilleSemaphore = prendreFeuille;
		this.remettreDessinSemaphore = remettreDessin;
	}

	public void run() {

		while (true) {
			if (!this.aUneFeuille) {
				pileDeFeuilleSemaphore.P();
				if (PileDeFeuilles.nombreDeFeuilles != 0) {
					PileDeFeuilles.nombreDeFeuilles--;
					this.aUneFeuille = true;
					System.out.println(Thread.currentThread().getName() + " prend " + PileDeFeuilles.nombreDeFeuilles);
				}
				pileDeFeuilleSemaphore.V();
			}

			if (this.aUneFeuille) {
				remettreDessinSemaphore.P();
				this.aUneFeuille = false;
				PileDeDessin.nombreDessinRemis++;
				System.out.println(Thread.currentThread().getName() + " remet " + PileDeDessin.nombreDessinRemis);
				remettreDessinSemaphore.V();
			}
		}
	}

}
