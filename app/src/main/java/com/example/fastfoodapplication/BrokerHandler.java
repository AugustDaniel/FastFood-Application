package com.example.fastfoodapplication;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.fastfoodlib.util.Lap;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BrokerHandler {
    private static final String LOGTAG = "BrokerHandler";

    public static BrokerHandler instance = new BrokerHandler();

    // Settings for connecting to Public HiveMQ broker
    private static final String BROKER_HOST_URL = "tcp://broker.hivemq.com:1883";
    private static final String USERNAME = null;
    private static final String PASSWORD = "";

    private static final String TOPIC_BASE = "avanstibreda/ti/1.4/A1/";
    private static final String TOPIC_ALL = "#";

    private static final String CLIENT_ID = "MQTTExample_" + UUID.randomUUID().toString();
    private static final int QUALITY_OF_SERVICE = 0;

    private MqttAndroidClient mqttAndroidClient;

    public enum topicType {LEFT, RIGHT, GAS, BREAK, LINE, RESET}

    public String clientCar = "";

    private int passedLineCount = 0;
    private LocalTime lapStart;

    private volatile boolean isOnController;

    private BrokerHandler() {
    }


    public void createConnection(Context context, ControllerActivity controllerActivity) {
//        this.context = context;
        mqttAndroidClient = new MqttAndroidClient(context, BROKER_HOST_URL, CLIENT_ID);

        isOnController = true;
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(LOGTAG, "MQTT client lost connection to broker, cause: " + cause.getLocalizedMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                Log.d(LOGTAG, "MQTT client received message " + message.toString() + " on topic " + topic);
                // Check what topic the message is for and handle accordingly

                String carTopic = topic.toString().split("/")[4];
                String secondaryTopic = topic.toString().split("/")[5];

//                System.out.println(clientCar);
//                System.out.println(carTopic);
//                System.out.println(secondaryTopic);

                System.out.println(isOnController);
                if (!isOnController) {
                    return;
                }

                if (Objects.equals(secondaryTopic, "isClaimed") && message.toString().equals("f") && clientCar.length() == 0) {

                    clientCar = carTopic;
                    controllerActivity.carNameText.setText(clientCar);
                    String topicPart = topic.toString().split("/")[4] + "/isClaimed";
                    publishMessage(topicPart, "t");
                    System.out.println("Device coupled to Hardware topic: " + topic.toString().split("/")[4]);
                } else if (carTopic.equals(clientCar) && secondaryTopic.equals(topicType.LINE.toString())) {
                    if (message.toString().equals("z")) {
                        passedLineCount++;
                        System.out.println(passedLineCount);
                        if (passedLineCount == 1) {
                            lapStart = LocalTime.now();
                        } else if (passedLineCount > 1) {
                            System.out.println("lap done");
                            LocalTime minutes = LocalTime.now().minusMinutes(lapStart.getMinute());
                            LocalTime seconds = LocalTime.now().minusSeconds(lapStart.getSecond());
                            LocalTime nano = LocalTime.now().minusNanos(lapStart.getNano());
                            LocalTime lapTime = LocalTime.of(0, minutes.getMinute(), seconds.getSecond(), nano.getNano());
                            System.out.println("new lap: " + lapTime);
                            passedLineCount = 0;
                            controllerActivity.sendLaps(lapTime);
                        }

                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(LOGTAG, "MQTT client delivery complete");
            }
        });

        connectToBroker(mqttAndroidClient, null);
    }

    private void connectToBroker(MqttAndroidClient client, String clientId) {
        // Set up connection options for the connection to the MQTT broker
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        // Add more options if necessary
        try {
            // Try to connect to the MQTT broker
            IMqttToken token = client.connect(options);
            // Set up callbacks for the result
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(LOGTAG, "MQTT client is now connected to MQTT broker");

                    subscribeToTopic(TOPIC_ALL);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(LOGTAG, "MQTT client failed to connect to MQTT broker: " +
                            exception.getLocalizedMessage());
                }
            });
        } catch (MqttException e) {
            Log.e(LOGTAG, "MQTT exception while connecting to MQTT broker, reason: " +
                    e.getReasonCode() + ", msg: " + e.getMessage() + ", cause: " + e.getCause());
            e.printStackTrace();
        }
    }

    public void publishMessage(String topic, String msg) {
        String finalTopic = TOPIC_BASE + topic;
        System.out.println("publishing message to topic: " + finalTopic);
        byte[] encodedPayload = new byte[0];
        try {
            // Convert the message to a UTF-8 encoded byte array
            encodedPayload = msg.getBytes("UTF-8");
            // Store it in an MqttMessage
            MqttMessage message = new MqttMessage(encodedPayload);
            // Set parameters for the message
            message.setQos(QUALITY_OF_SERVICE);
            message.setRetained(false);
            // Publish the message via the MQTT broker
            mqttAndroidClient.publish(finalTopic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            Log.e(LOGTAG, "MQTT exception while publishing topic to MQTT broker, msg: " + e.getMessage() +
                    ", cause: " + e.getCause());
            e.printStackTrace();
        }
    }

    public void publishMessage(topicType topicType, String msg) {
        String topic = clientCar + "/" + topicType.toString();
        publishMessage(topic, msg);
    }

    private void subscribeToTopic(String topic) {
        String finalTopic = TOPIC_BASE + topic;
        try {
            // Try to subscribe to the topic
            IMqttToken token = mqttAndroidClient.subscribe(finalTopic, QUALITY_OF_SERVICE);
            // Set up callbacks to handle the result
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(LOGTAG, "MQTT client is now subscribed to topic " + finalTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(LOGTAG, "MQTT failed to subscribe to topic " + finalTopic + " because: " +
                            exception.getLocalizedMessage());
                }
            });
        } catch (MqttException e) {
            Log.e(LOGTAG, "MQTT exception while subscribing to topic on MQTT broker, reason: " +
                    e.getReasonCode() + ", msg: " + e.getMessage() + ", cause: " + e.getCause());
            e.printStackTrace();
        }
    }

    public void setIsOnController(boolean b) {
        isOnController = b;

        if (mqttAndroidClient != null) {
            mqttAndroidClient.close();
        }
    }
}

