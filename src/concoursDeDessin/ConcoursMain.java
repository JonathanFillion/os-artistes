package concoursDeDessin;


public class ConcoursMain {

	int nbFeuille;
	Semaphore prendreFeuille = new Semaphore(1);
	Semaphore remettreDessin = new Semaphore(1);
	Artiste[] artistes;
	
	public ConcoursMain() {
		artistes = new Artiste[5];
		
		for(int i = 0 ; i < artistes.length; i++) {
			artistes[i] = new Artiste(i, prendreFeuille, remettreDessin);
		}
		for(int i = 0; i < artistes.length; i++) {
			artistes[i].start();
		}
		
	}
	
	
	public static void main(String[] args) {
		ConcoursMain cm = new ConcoursMain();
	}
	
	

}
