package es.andrewazor.containertest.subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import es.andrewazor.containertest.MessagePasser;
import jdk.jfr.Event;
import jdk.jfr.Label;

public class Subscriber implements Runnable {

    private static final int NUM_WORKERS = 8;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(NUM_WORKERS);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    private final MessagePasser messagePasser;
    private final MemoizingFactorial factorial;
    private final Fibonacci fibonacci;

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
        this.factorial = new MemoizingFactorial();
        this.factorial.setCached(false);
        this.fibonacci = new Fibonacci();
    }

    @Override
    public void run() {
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(this::monitorWorkQueue, 0, 100, TimeUnit.MILLISECONDS);
        SCHEDULED_EXECUTOR.schedule(() -> this.factorial.setCached(true), 10, TimeUnit.SECONDS);

        try {
            this.messagePasser.startListening(9090);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        for (int i = 0; i < NUM_WORKERS; i++) {
            EXECUTOR.submit(this::process);
        }
    }

    private void process() {
        while (true) {
            try {
                String msg = this.messagePasser.consume();
                int num = Integer.parseInt(msg);
                factorial.compute(fibonacci.compute(num));
            } catch (InterruptedException ie) {
            }
        }
    }

    private void monitorWorkQueue() {
        QueueLengthEvent evt = new QueueLengthEvent();
        evt.length = this.messagePasser.inQueueCount();
        evt.caching = this.factorial.isCached();
        evt.commit();
    }

    @Label("QueueLength")
    private static class QueueLengthEvent extends Event {
        @Label("QueueLength") int length;
        @Label("Caching") boolean caching;
    }

}