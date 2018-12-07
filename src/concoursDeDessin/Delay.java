package concoursDeDessin;

public class Delay {
	// génère un nombre entre 0 et N-1 inclus
	public static int aleat(int N) {
		return (int) ((Math.random() * 997) % N);
	}

	// le thread s’endort pendant n millisecondes
	public static void pause(int n) {
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

	// attente d’un nombre aléatoire de ms entre 0 et n
	public static void attente(int n) {
		try {
			Thread.sleep(aleat(n));
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}
}
