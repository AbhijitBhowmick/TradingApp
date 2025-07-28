package com.trading.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class QueueBufferExample {
    private static final int CAPACITY = 10000;
    private static final BlockingQueue<String> queue = new ArrayBlockingQueue<>(CAPACITY);

    // Producer Thread
    static class Producer implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String data = fetchData(); // e.g., incoming WebSocket data
                    queue.put(data); // blocks if queue is full
                }
            } catch (InterruptedException ignored) {}
        }
        private String fetchData() { /* fetch or mock data implementation */ return "data"; }
    }

    // Consumer Thread
    static class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String data = queue.take(); // blocks if queue is empty
                    processData(data);
                }
            } catch (InterruptedException ignored) {}
        }
        private void processData(String data) { /* handle and persist */ }
    }

    public static void main(String[] args) {
        new Thread(new Producer()).start();
        new Thread(new Consumer()).start();
    }
}
