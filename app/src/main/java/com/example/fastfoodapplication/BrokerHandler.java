package com.example.fastfoodapplication;

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

public class BrokerHandler implements MqttCallback {
    private static final String LOGTAG = "BrokerHandler";

    // Settings for connecting to Public HiveMQ broker
    private static final String BROKER_HOST_URL = "tcp://broker.hivemq.com:1883";
    private static final String USERNAME = null;
    private static final String PASSWORD = "";

    private static final String TOPIC_BASE = "avanstibreda/ti/1.4/A1/";

    private static final String CLIENT_ID = "MQTTExample_" + UUID.randomUUID().toString();
    private static final int QUALITY_OF_SERVICE = 0;

    private MqttAndroidClient mqttAndroidClient;

    public static BrokerHandler instance = new BrokerHandler();
    private List<BrokerObserver> observers = new ArrayList<BrokerObserver>();
    public enum topicType {LEFT,RIGHT,GAS,BREAK}
    public HashMap<String, Boolean> cars;
    private String carTopic = "";
    public void attach(BrokerObserver observer){
        observers.add(observer);
    }

    private BrokerHandler(){
        cars = new HashMap<>();

        //#region MQTT setup
        // Set up connection options for the connection to the MQTT broker
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        // Add more options if necessary
        try {
            // Try to connect to the MQTT broker
            IMqttToken token = mqttAndroidClient.connect(options);
            // Set up callbacks for the result
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(LOGTAG, "MQTT client is now connected to MQTT broker");
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
        //#endregion


    }

    public void publishMessage(topicType topicType, String msg) {
        String topic = TOPIC_BASE+carTopic+topicType.toString();
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
        topic = TOPIC_BASE+topic;
        try {
            // Try to subscribe to the topic
            IMqttToken token = mqttAndroidClient.subscribe(topic, QUALITY_OF_SERVICE);
            // Set up callbacks to handle the result
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(LOGTAG, "MQTT client is now subscribed to topic " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(LOGTAG, "MQTT failed to subscribe to topic " + topic + " because: " +
                            exception.getLocalizedMessage());
                }
            });
        } catch (MqttException e) {
            Log.e(LOGTAG, "MQTT exception while subscribing to topic on MQTT broker, reason: " +
                    e.getReasonCode() + ", msg: " + e.getMessage() + ", cause: " + e.getCause());
            e.printStackTrace();
        }
    }


    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        for (BrokerObserver observer : observers) {
            for (String observerTopic : observer.getSubscriptions()) {
                if(observerTopic == topic){
                    observer.update(message);
                }
            }
        }

    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(LOGTAG, "MQTT client lost connection to broker, cause: " + cause.getLocalizedMessage());
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(LOGTAG, "MQTT client delivery complete");
    }


}

