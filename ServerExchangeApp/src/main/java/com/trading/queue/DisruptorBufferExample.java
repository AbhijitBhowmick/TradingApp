//package com.trading.queue;
//
//import com.lmax.disruptor.*;
//import com.lmax.disruptor.dsl.Disruptor;
//
//import java.nio.charset.StandardCharsets;
//import java.util.concurrent.Executors;
//
//public class DisruptorBufferExample {
//    // Event class
//    public static class DataEvent {
//        public String data;
//    }
//
//    // Factory for the Disruptor
//    public static class DataEventFactory implements EventFactory<DataEvent> {
//        public DataEvent newInstance() { return new DataEvent(); }
//    }
//
//    // Event handler (Consumer)
//    public static class DataEventHandler implements EventHandler<DataEvent> {
//        public void onEvent(DataEvent event, long sequence, boolean endOfBatch) {
//            process(event.data);
//        }
//        private void process(String data) { /* handle and persist */ }
//    }
//
//    public static void main(String[] args) {
//        int bufferSize = 1024;
//        Disruptor<DataEvent> disruptor = new Disruptor<>(
//                new DataEventFactory(),
//                bufferSize,
//                Executors.defaultThreadFactory()
//        );
//
//        disruptor.handleEventsWith(new DataEventHandler());
//        disruptor.start();
//
//        RingBuffer<DataEvent> ringBuffer = disruptor.getRingBuffer();
//
//        // Producer example
//        for (int i = 0; i < 10000; i++) {
//            long sequence = ringBuffer.next();
//            try {
//                DataEvent event = ringBuffer.get(sequence);
//                event.data = "data" + i;
//            } finally {
//                ringBuffer.publish(sequence);
//            }
//        }
//    }
//}
//
