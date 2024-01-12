import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

	/*Programacion concurente y distribuitiva
	 * Actividad 2: Parte 2 Puente sbre un rio (Monitor en JAVA)
	 * Nombre y apellido: Bogdana Hirlav Tifrea
	 * DNI: Y7437620D
	 * Nombre del profesor: Jose Delgado
	 */


class PuenteMonitor {
    private final Lock lock = new ReentrantLock();
    private final Condition cochesDelNorte = lock.newCondition();
    private final Condition cochesDelSur = lock.newCondition();

    private boolean cocheCruzando = false;
    private boolean cocheEnEsperaDelNorte = false;
    private boolean cocheEnEsperaDelSur = false;

    public void cruzarPuenteDesdeElNorte() {
        lock.lock();
        try {
            while (cocheCruzando || cocheEnEsperaDelSur) {
                cocheEnEsperaDelNorte = true;
                cochesDelNorte.await();
                cocheEnEsperaDelNorte = false;
            }
            cocheCruzando = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void cruzarPuenteDesdeElSur() {
        lock.lock();
        try {
            while (cocheCruzando || cocheEnEsperaDelNorte) {
                cocheEnEsperaDelSur = true;
                cochesDelSur.await();
                cocheEnEsperaDelSur = false;
            }
            cocheCruzando = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void salirDelPuente() {
        lock.lock();
        try {
            cocheCruzando = false;
            if (cocheEnEsperaDelNorte) {
                cochesDelNorte.signal();
            } else if (cocheEnEsperaDelSur) {
                cochesDelSur.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}

class Coche implements Runnable {
    private final PuenteMonitor puente;
    private final String direccion;

    public Coche(PuenteMonitor puente, String direccion) {
        this.puente = puente;
        this.direccion = direccion;
    }

    @Override
    public void run() {
        if (direccion.equals("Norte")) {
            puente.cruzarPuenteDesdeElNorte();
        } else if (direccion.equals("Sur")) {
            puente.cruzarPuenteDesdeElSur();
        }

        // Simulacion de cruzar el puente
        try {
            System.out.println("Coche desde el " + direccion + " cruzando el puente.");
            Thread.sleep(1000);  // Simula el tiempo que toma cruzar el puente
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            puente.salirDelPuente();
            System.out.println("Coche desde el " + direccion + " ha salido del puente.");
        }
    }
}

public class PuenteSobreUnRio {
    public static void main(String[] args) {
        PuenteMonitor puente = new PuenteMonitor();

        Thread cocheNorte1 = new Thread(new Coche(puente, "Norte"));
        Thread cocheSur1 = new Thread(new Coche(puente, "Sur"));
        Thread cocheNorte2 = new Thread(new Coche(puente, "Norte"));
        Thread cocheSur2 = new Thread(new Coche(puente, "Sur"));

        cocheNorte1.start();
        cocheSur1.start();
        cocheNorte2.start();
        cocheSur2.start();
    }
}
