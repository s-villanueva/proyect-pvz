package org.example;

/**
 * Hilo
 *
 * @author Marcos Quispe
 * @since 1.0
 */
public class Hilo implements Runnable {
    Thread t;
    int valorAdicion;

    public Hilo(int valorAdicion) {
        this.valorAdicion = valorAdicion;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        int i = 1;
        while (i < 10) {
            System.out.println(i);
            i += valorAdicion;
            try {
                Thread.sleep(1000); // 1seg
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Hilo finalizado! + valorAdicion: " + valorAdicion);
    }

    public static void main(String[] args) {
        Hilo h = new Hilo(1);
        Hilo h2 = new Hilo(2);
        System.out.println("main Finalizado");
    }
}
