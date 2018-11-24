package concoursDeDessin;

public class ConcoursMain {

	int nbFeuille;
	Semaphore prendreFeuille = new Semaphore(1);
	Semaphore remettreDessin = new Semaphore(1);
	Semaphore crayonRougeSemaphore = new Semaphore(3);
	Semaphore crayonBleuSemaphore = new Semaphore(3);
	Semaphore crayonJauneSemaphore = new Semaphore(3);
	Semaphore crayonVertSemaphore = new Semaphore(3);
	Artiste[] artistes;

	public ConcoursMain() {
		artistes = new Artiste[5];

		for (int i = 0; i < artistes.length; i++) {
			artistes[i] = new Artiste(i, prendreFeuille, remettreDessin, new Semaphore[] { crayonRougeSemaphore,
					crayonBleuSemaphore, crayonJauneSemaphore, crayonVertSemaphore });
		}
		for (int i = 0; i < artistes.length; i++) {
			artistes[i].start();
		}

	}

	public static void main(String[] args) {
		ConcoursMain cm = new ConcoursMain();
	}

}
