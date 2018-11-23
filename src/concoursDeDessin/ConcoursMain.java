package concoursDeDessin;


public class ConcoursMain {

	Semaphore prendreFeuille;
	Semaphore remettreDessin;
	Artiste[] artistes;
	
	public ConcoursMain() {
		prendreFeuille = new Semaphore(1, "prendre une feuille");
		remettreDessin = new Semaphore(1, "remettre un dessin");
		artistes = new Artiste[5];
		for(int i = 0 ; i < artistes.length; i++) {
			artistes[i] = new Artiste(i, this.prendreFeuille, this.remettreDessin);
		}
		for(int i = 0; i < artistes.length; i++) {
			artistes[i].start();
		}
		
	}
	
	
	public static void main(String[] args) {
		ConcoursMain cm = new ConcoursMain();
	}
	
	

}
