package concoursDeDessin;

public class Artiste extends Thread {

	int id;
	Semaphore prendreFeuille;
	Semaphore remettreDessin;
	
	public Artiste(int id, Semaphore prendreFeuille, Semaphore remettreDession) {
		super("Artiste "+id);
		this.prendreFeuille = prendreFeuille;
		this.remettreDessin = remettreDession;
	}
	
	public void run() {
		prendreFeuille.P();
		prendreFeuille.V();
		remettreDessin.P();
		remettreDessin.V();
		System.out.println(Thread.currentThread().getName() + " a fini sa routine");
	}
	
	
	
}
