package concoursDeDessin;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * Attend que tous les artistes mettrent leur statut à "terminer"
 * Ensuite genere la pige au hasard et annonce le vainqueur
 *
 */

public class Juge extends Thread{

	Semaphore pileDeDessinSemaphore;
	Artiste[] artistes;
	
	public Juge(Semaphore pileDeDessinSemaphore,Artiste[] artistes) {
		this.pileDeDessinSemaphore = pileDeDessinSemaphore;
		this.artistes = artistes;
	}
	/**
	 * Le juge attend que tous les artistes indiquent qu'ils ont fini, ensuite le juge reserve la pile de dessins, genere un nombre aleatoir
	 * et choisi le gagnant, il ecrit aussi dans le fichier
	 */
	public void run() {
		int rand = (int) (Math.random() * (parametresPubliques.paramInitNombre));
		
		while(!artistesOntFini());
		parametresPubliques.sortie += "\nTous les fils artistes ont terminé";
		System.out.println("Tous les fils artistes ont terminé");
		
		pileDeDessinSemaphore.P();
		
		System.out.println("LE GAGNANT EST ... " + parametresPubliques.dessinRemis[rand]+ " pour le dessin sur la feuille " + rand);
		parametresPubliques.sortie += "\nLE GAGNANT EST ... " + parametresPubliques.dessinRemis[rand] + " pour le dessin sur la feuille " + rand;
		pileDeDessinSemaphore.V();
		
		//Le juge ecrit dans le fichier demande par les exigences
		try {
			FileWriter fileWriter = new FileWriter("D:/DevINF3723_Fillion_MageauPetrin.txt");
			fileWriter.write(parametresPubliques.sortie);
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	//Vérif que les artistes ont tous fini
	public boolean artistesOntFini() {
		boolean ontFini = true;
		for(int i = 0; i < artistes.length;i++) {
			ontFini = artistes[i].aFini && ontFini;
		}
		return ontFini;
	}
}
