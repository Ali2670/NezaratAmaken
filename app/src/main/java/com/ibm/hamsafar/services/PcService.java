package com.ibm.hamsafar.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.ibm.hamsafar.utils.DateUtil;
import com.ibm.hamsafar.utils.NotificationUtils;
import com.ibm.hamsafar.utils.Tools;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Hamed on 05/06/2015.
 */
public class PcService extends Service {
    public static BlockingDeque<String> queue = new LinkedBlockingDeque<String>();
    Gson gson;
    Address address;
    //    DataBaseHelper dbHelper;
    String macAddress;
    IBinder mBinder = new LocalBinder();
    ConnectionFactory factory = new ConnectionFactory();
    Thread subscribeThread;
    Thread publishThread;
    private Channel channel;
    private Connection connection;
    private NotificationUtils notificationUtils;

    public PcService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        //        *********************************************************************
        try {
            macAddress = Tools.MacAddress();

            setupConnectionFactory();

            subscribe();
            publishToAMQP();

        } catch (Exception var2) {
            var2.printStackTrace();
        }

//        *********************************************************************
    }


    private void setupConnectionFactory() {
        try {
            factory.setRequestedHeartbeat(5);
            factory.setAutomaticRecoveryEnabled(true);
            factory.setHost("hamed-heydari.com");
            factory.setPort(5672);
            factory.setUsername("hamed");
            factory.setPassword("12345678");
            String addressesStr = "hamed-heydari.com:5672";
            String[] split = addressesStr.trim().split(":");
            address = new Address(split[0], Integer.parseInt(split[1]));


        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    public void publishMessage(String message) {
        //Adds a message to internal blocking queue
        try {
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void subscribe() {
        try {
            subscribeThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (true) {

                        try {
                            if (channel == null || !channel.isOpen()) {
                                connection = factory.newConnection();
                                connection.addShutdownListener(new ShutdownListener() {
                                    public void shutdownCompleted(ShutdownSignalException cause) {

                                        System.out.println("");
                                    }
                                });
                                channel = connection.createChannel();

                                DefaultConsumer consumer = new DefaultConsumer(channel) {
                                    @Override
                                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                                        try {
//                                        String message = new String(body, "UTF-8");

//                                        gson = new Gson();
//                                        ReadIt m = gson.fromJson(message, ReadIt.class);
//                                        MsgReaded readed = new MsgReaded();
                                            try {

                                            } finally {
                                                channel.basicAck(envelope.getDeliveryTag(), false);

                                            }
                                        } catch (Exception e) {
                                            System.out.println("err");
                                        }
                                    }
                                };
                                channel.basicConsume(macAddress, false, consumer);
                            }
                        } catch (Exception e1) {
                            channel = null;

                        }
                        try {
                            Thread.sleep(1000 * 60 * 100);
                        } catch (Exception e) {

                        }

                    }

                }
            }

            );
            subscribeThread.start();
        } catch (Exception e) {

        }
    }

    private boolean CheckDate(String tarikh) {
        if (DateUtil.getCurrentDate().compareTo(tarikh) >= 0) {
            return false;
        }
        return true;
    }

    public void publishToAMQP() {
        try {


            publishThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Connection connection = factory.newConnection();
                            Channel ch = connection.createChannel();
                            ch.confirmSelect();

                            while (true) {
                                if (queue != null && queue.size() > 0) {
                                    String message = queue.takeFirst();
                                    try {
                                        ch.basicPublish("", "StartUp", new AMQP.BasicProperties.Builder()
                                                .contentType("text/plain")

                                                .build(), message.getBytes());
                                        ch.waitForConfirmsOrDie();
                                    } catch (Exception e) {
                                        Log.d("", "[f] " + message);
                                        throw e;
                                    }
                                }
                            }
                        } catch (InterruptedException e) {
                            break;
                        } catch (Exception e) {
                            Log.d("", "Connection broken: " + e.getClass().getName());
                            try {
                                Thread.sleep(5000); //sleep and then try again
                            } catch (InterruptedException e1) {
                                break;
                            }
                        }
                    }
                }
            });
            publishThread.start();
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            publishThread.interrupt();
            subscribeThread.interrupt();
            System.out.println("Service Closed Succsess ...");
            /*    if (dbHelper != null)
            dbHelper.close();*/
        } catch (Exception e) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
//        return START_STICKY;
    }

    private void showNotificationMessage(Context context, String title, String message, Intent intent, String color) {

        notificationUtils = new NotificationUtils(context);


        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent, color);
    }

    public class LocalBinder extends Binder {
        public PcService getServerInstance() {
            return PcService.this;
        }
    }
}