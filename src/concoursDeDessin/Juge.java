package concoursDeDessin;

public class Juge extends Thread{

	Semaphore pileDeDessinSemaphore;
	Artiste[] artistes;
	
	public Juge(Semaphore pileDeDessinSemaphore,Artiste[] artistes) {
		this.pileDeDessinSemaphore = pileDeDessinSemaphore;
		this.artistes = artistes;
	}
	
	public void run() {
		
		while(!artistesOntFini());
		System.out.println("Tous les fils artistes ont terminé");
		
		pileDeDessinSemaphore.P();
		int rand = (int) (Math.random() * (parametresPubliques.paramInitNombre));
		System.out.println("LE GAGNANT EST ... " + parametresPubliques.dessinRemis[rand]);
		
	}
	
	public boolean artistesOntFini() {
		boolean ontFini = true;
		for(int i = 0; i < artistes.length;i++) {
			ontFini = artistes[i].aFini && ontFini;
		}
		return ontFini;
	}
}
