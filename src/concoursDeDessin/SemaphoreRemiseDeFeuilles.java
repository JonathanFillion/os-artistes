package concoursDeDessin;

public class SemaphoreRemiseDeFeuilles {
	private int valeur;
	private int nbWait = 0;
	private String action;
	private int feuillesRemises = 0;

	public SemaphoreRemiseDeFeuilles(int i, String action) {
		this.valeur = i;
		this.action = action;
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
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		valeur--;
		feuillesRemises++;
		System.out.println(Thread.currentThread().getName() + " remet un dessin #" + this.feuillesRemises);
	}

	public synchronized void V() {
		valeur++;
		// System.out.println(Thread.currentThread().getName() + " a fini de "+
		// this.action);
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
