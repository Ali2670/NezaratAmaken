//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ibm.hamsafar.services;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QueueConnector {
    private static ConnectionFactory factory;
    private static Connection connection;
    private static ExecutorService executorService;
    private static Channel channel;
    private static Address address;

    static {
        int corePoolSize = 10;
        int maxPollSize = 800;
        int keepAlive = 5;
        executorService = new ThreadPoolExecutor(corePoolSize, maxPollSize, (long) keepAlive, TimeUnit.MINUTES, new LinkedBlockingQueue());
        factory = new ConnectionFactory();
        factory.setRequestedHeartbeat(5);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setSharedExecutor(executorService);
        factory.setHost("172.16.160.7:5672");
        factory.setUsername("hamed");
        factory.setPassword("12345678");
        String addressesStr = "172.16.160.7:5672";
        String[] split = addressesStr.trim().split(":");
        address = new Address(split[0], Integer.parseInt(split[1]));
        openChannel();
    }


    public QueueConnector() {
    }

    public static void send(byte[] message, String queue) {
        try {
            channel().basicPublish(queue, "", new AMQP.BasicProperties.Builder()
                    .contentType("text/plain")

                    .build(), message);
        } catch (IOException var3) {
            throw new RuntimeException("could not send message to queue " + queue + ", message: " + message, var3);
        }
    }

    private static void openChannel() {
        while (true) {
            try {
                connection = factory.newConnection(new Address[]{address});
                open();
                return;
            } catch (Exception var3) {
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException var2) {
                    ;
                }
            }
        }
    }

    private static void open() throws Exception {
        channel = connection.createChannel();
    }

    private static Channel channel() {
        try {
            if (channel == null || !channel.isOpen()) {
                open();
            }

            return channel;
        } catch (Exception var1) {
            throw new RuntimeException("channel could not be opened to rabbitmq at address " + address, var1);
        }
    }

    public static QueueingConsumer listen(String queue) throws IOException {
        QueueingConsumer consumer = new QueueingConsumer(channel());

        channel().basicConsume(queue, true, consumer);
        return consumer;
    }

    private static class MessageConsumer extends DefaultConsumer {
        public MessageConsumer(Channel channel) {
            super(channel);
        }

        public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, final byte[] body) throws IOException {
            QueueConnector.executorService.submit(new Runnable() {
                public void run() {
                    System.out.println(new String(body));
                }
            });
        }
    }
}
