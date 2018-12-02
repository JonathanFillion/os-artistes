package concoursDeDessin;

/**
 * Classique semaphore
 * @author jo
 *
 */

public class Semaphore {
	private int valeur;
	private int nbWait = 0;
	
	public Semaphore(int i) {
		this.valeur = i;
	}

	public synchronized void P() {
		while (valeur <= 0) {
			nbWait++;
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		valeur--;
	}

	public synchronized void V() {
		valeur++;
		if (nbWait > 0) {
			nbWait--;
			notify();
		}
	}

	public synchronized int getNbWait() {
		return nbWait;
	}

	public synchronized int getS() {
		return valeur;
	}
}
