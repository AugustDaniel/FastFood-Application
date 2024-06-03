package com.example.fastfoodapplication;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BrokerHandler{
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

    private List<BrokerObserver> observers = new ArrayList<BrokerObserver>();
    public enum topicType {LEFT,RIGHT,GAS,BREAK}
    public HashMap<String, Boolean> cars;
    private String carTopic = "Test/";
    public void attach(BrokerObserver observer){
        observers.add(observer);
    }

    private BrokerHandler(){
//        cars = new HashMap<>();
    }


    public void createConnection(Context context){
        mqttAndroidClient = new MqttAndroidClient(context, BROKER_HOST_URL, CLIENT_ID);


        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(LOGTAG, "MQTT client lost connection to broker, cause: " + cause.getLocalizedMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(LOGTAG, "MQTT client received message " + message + " on topic " + topic);
                // Check what topic the message is for and handle accordingly
                //todo on specific topic handeling
                if(topic == TOPIC_BASE+TOPIC_ALL){
                    System.out.println(message);
                }
//                for (BrokerObserver observer : observers) {
//                    for (String observerTopic : observer.getSubscriptions()) {
//                        if(observerTopic == topic){
//                            observer.update(message);
//                        }
//                    }
//                }
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

    public void publishMessage(topicType topicType, String msg) {
        System.out.println("sending message plz");
        Log.d(LOGTAG, "MQTT SENDING MESSAGE PLZ");

        String topic = TOPIC_BASE+carTopic+topicType.toString();
        System.out.println(topic);
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
            mqttAndroidClient.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            Log.e(LOGTAG, "MQTT exception while publishing topic to MQTT broker, msg: " + e.getMessage() +
                    ", cause: " + e.getCause());
            e.printStackTrace();
        }
    }

    private void subscribeToTopic(String topic) {
        String finalTopic = TOPIC_BASE+topic;
        try {
            // Try to subscribe to the topic
            IMqttToken token = mqttAndroidClient.subscribe(topic, QUALITY_OF_SERVICE);
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

}

