package es.andrewazor.containertest.subscriber;

import es.andrewazor.containertest.MessagePasser;

public class Subscriber implements Runnable {

    private final MessagePasser messagePasser;

    public static void main(final String[] args) {
        final Thread t = new Thread(new Subscriber());
        t.setDaemon(true);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    Subscriber() {
        this.messagePasser = new MessagePasser();
    }

    @Override
    public void run() {
        try {
            this.messagePasser.startListening(9090);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            while (true) {
                while (this.messagePasser.hasMessages()) {
                    String msg = this.messagePasser.consume();
                    System.out.println(String.format("Received \"%s\"", msg));
                }
                System.out.println("\n");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    break;
                }
            }
        } finally {
            this.messagePasser.stop();
        }
    }

}