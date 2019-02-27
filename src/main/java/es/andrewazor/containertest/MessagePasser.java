package es.andrewazor.containertest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class MessagePasser {

    private enum State {
        WAITING,
        RUNNING,
        STOPPED,
        ;
    }

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(4);

    private final BlockingQueue<String> inQ = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> outQ = new LinkedBlockingQueue<>();
    private volatile State state = State.WAITING;

    public void startListening(int port) throws IOException {
        stop();
        state = State.RUNNING;
        EXECUTOR.submit(new ListeningConnector(new ServerSocket(port)));
    }

    public void connect(InetAddress host, int port) throws IOException {
        stop();
        state = State.RUNNING;
        EXECUTOR.submit(new Connector(host, port));
    }

    public void stop() {
        state = State.STOPPED;
    }

    public boolean hasMessages() {
        return !inQ.isEmpty();
    }

    public String consume() {
        return inQ.remove();
    }

    public void send(String s) {
        outQ.add(s);
    }

    private class Connector implements Runnable {
        private final Socket socket;

        Connector(InetAddress addr, int port) throws IOException {
            this.socket = new Socket(addr, port);
        }

        @Override
        public void run() {
            try {
                EXECUTOR.submit(new Sender(socket));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private class ListeningConnector implements Runnable {
        private final ServerSocket socket;

        ListeningConnector(ServerSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connector starting");
            try {
                Socket s = socket.accept();
                System.out.println(String.format("Connection established: %s", s.getInetAddress().getHostName()));
                EXECUTOR.submit(new Receiver(s));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private abstract class Messenger implements Runnable {
        private final Socket s;

        Messenger(Socket s) {
            this.s = s;
        }

        @Override
        public final void run() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));) {
                handleConnection(br, bw);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                try {
                    s.close();
                } catch (IOException ie) { }
            }
        }

        abstract void handleConnection(BufferedReader br, BufferedWriter bw) throws IOException, InterruptedException;
    }

    private class Receiver extends Messenger {
        Receiver(Socket s) {
            super(s);
        }

        @Override
        void handleConnection(BufferedReader br, BufferedWriter bw) throws IOException, InterruptedException {
            System.out.println("Receiver started");
            String inputLine;
            while (state.equals(State.RUNNING) && (inputLine = br.readLine()) != null) {
                inQ.add(inputLine);
            }
            System.out.println("Receiver completed");
        }
    }

    private class Sender extends Messenger {
        Sender(Socket s) {
            super(s);
        }

        @Override
        void handleConnection(BufferedReader br, BufferedWriter bw) throws IOException, InterruptedException {
            System.out.println("Sender started");
            String toSend;
            while ((toSend = outQ.take()) != null && state.equals(State.RUNNING)) {
                bw.write(toSend);
                bw.newLine();
                bw.flush();
            }
            System.out.println("Sender completed");
        }
    }

}