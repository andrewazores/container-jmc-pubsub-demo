package es.andrewazor.containertest.publisher;

import java.net.InetAddress;

import es.andrewazor.containertest.MessagePasser;

public class Publisher implements Runnable {

    private final MessagePasser messagePasser;

    public static void main(final String[] args) {
        final Thread t = new Thread(new Publisher());
        t.setDaemon(true);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    Publisher() {
        this.messagePasser = new MessagePasser();
    }

    @Override
    public void run() {
        try {
            this.messagePasser.connect(InetAddress.getByName("jmx-subscriber"), 9090);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            while (true) {
                for (int i = 0; i < 2048; i++) {
                    final int num = (int) (Math.random() * 20);
                    String s = Integer.toString(num);
                    this.messagePasser.send(s);
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException ie) {
        } finally {
            this.messagePasser.stop();
        }
    }

}