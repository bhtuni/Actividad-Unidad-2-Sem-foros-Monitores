import java.util.concurrent.Semaphore;

/*Programacion concurente y distribuitiva
	 * Actividad 2: Parte 1 Problema de uso de recursos (Semaforo en JAVA)
	 * Nombre y apellido: Bogdana Hirlav Tifrea
	 * DNI: Y7437620D
	 * Nombre del profesor: Jose Delgado
	 */


public class UsoDeRecursos {
	    private Semaphore semaforoAcceso;  // Controla el acceso al recurso
	    private Semaphore semaforoUnidades;  // Cuenta de unidades disponibles del recurso
	    private int unidadesDisponibles;  // Variable para llevar la cuenta de las unidades disponibles

	    public UsoDeRecursos(int k) {
	        semaforoAcceso = new Semaphore(1);  // Inicializado con 1 para exclusion mutua
	        semaforoUnidades = new Semaphore(k);  // Inicializado con la cantidad total de unidades del recurso
	        unidadesDisponibles = k;
	    }

	    public void reserva(int r) {
	        try {
	            semaforoAcceso.acquire();  // Bloquea el acceso a la sección critica

	            if (r <= unidadesDisponibles) {
	                semaforoUnidades.acquire(r);  // Intenta reservar r unidades del recurso
	                unidadesDisponibles -= r;
	                System.out.println(Thread.currentThread().getName() + " ha reservado " + r + " unidades del recurso.");
	            } else {
	                System.out.println(Thread.currentThread().getName() + " no puede reservar " + r + " unidades. Recursos insuficientes.");
	            }

	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        } finally {
	            semaforoAcceso.release();  // Libera el acceso a la seccion crítica
	        }
	    }

	    public void libera(int l) {
	        try {
	            semaforoAcceso.acquire();  // Bloquea el acceso a la seccion crítica

	            semaforoUnidades.release(l);  // Libera l unidades del recurso
	            unidadesDisponibles += l;
	            System.out.println(Thread.currentThread().getName() + " ha liberado " + l + " unidades del recurso.");

	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        } finally {
	            semaforoAcceso.release();  // Libera el acceso a la seccion crítica
	        }
	    }

	    public static void main(String[] args) {
	        final UsoDeRecursos recurso = new UsoDeRecursos(5);

	        // Crear y ejecutar varios hilos para simular procesos que reservan y liberan unidades del recurso
	        for (int i = 0; i < 10; i++) {
	            final int procesoId = i;
	            new Thread(() -> {
	                recurso.reserva(2);
	                // Simular alguna tarea utilizando las unidades del recurso
	                try {
	                    Thread.sleep(1000);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	                recurso.libera(1);
	            }, "Proceso-" + procesoId).start();
	        }
	    }
}
