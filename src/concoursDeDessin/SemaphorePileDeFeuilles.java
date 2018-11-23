package concoursDeDessin;

public class SemaphorePileDeFeuilles {
	private int valeur;
	private int nbWait = 0;
	private String action;
	
	public SemaphorePileDeFeuilles(int i, String action) {
		this.valeur = i;
		this.action =  action;
	}

	public synchronized void P() {
		while (valeur == 0) {
			nbWait++;
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		valeur = 0;
	}

	public synchronized void V() {
		valeur = 1;
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
