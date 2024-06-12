package com.example.fastfoodapplication;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

public class BrokerHandler {
    private static final String logTag = BrokerHandler.class.getName();

    public static BrokerHandler instance = new BrokerHandler();

    // Settings for connecting to Public HiveMQ broker
    private static final String brokerHostUrl = "tcp://broker.hivemq.com:1883";
    private static final String username = null;
    private static final String password = "";

    private static final String topicBase = "avanstibreda/ti/1.4/A1/";
    private static final String topicAll = "#";

    private static final String clientID = "MQTTExample_" + UUID.randomUUID().toString();
    private static final int qualityOfService = 0;

    private MqttAndroidClient mqttAndroidClient;

    public enum topicType {LEFT, RIGHT, GAS, BREAK, LINE, RESET}

    public String clientCar = "";

    private boolean passedFinishAtStart;
    private boolean hasPassedCheckpoint;
    private LocalTime lapStart;

    private volatile boolean isOnController;

    private BrokerHandler() {
    }

    public void createConnection(Context context, ControllerActivity controllerActivity) {
        mqttAndroidClient = new MqttAndroidClient(context, brokerHostUrl, clientID);

        isOnController = true;
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(logTag, "MQTT client lost connection to broker, cause: " + cause.getLocalizedMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Check what topic the message is for and handle accordingly

                String carTopic = topic.split("/")[4];
                String secondaryTopic = topic.split("/")[5];

                Log.d(logTag, String.valueOf(isOnController));
                if (!isOnController) {
                    return;
                }

                if (Objects.equals(secondaryTopic, "isClaimed") && message.toString().equals("f") && clientCar.isEmpty()) {

                    clientCar = carTopic;
                    controllerActivity.carNameText.setText(clientCar);
                    String topicPart = topic.split("/")[4] + "/isClaimed";
                    publishMessage(topicPart, "t");
                    Log.d(logTag, "Device coupled to Hardware topic: " + topic.split("/")[4]);
                } else if (carTopic.equals(clientCar) && secondaryTopic.equals(topicType.LINE.toString())) {
                    if (message.toString().equals("z")) {

                        if (hasPassedCheckpoint) {
                            Log.d(logTag, "lap done");
                            LocalTime minutes = LocalTime.now().minusMinutes(lapStart.getMinute());
                            LocalTime seconds = LocalTime.now().minusSeconds(lapStart.getSecond());
                            LocalTime nano = LocalTime.now().minusNanos(lapStart.getNano());
                            LocalTime lapTime = LocalTime.of(0, minutes.getMinute(), seconds.getSecond(), nano.getNano());
                            Log.d(logTag, "new lap: " + lapTime);
                            controllerActivity.sendLaps(lapTime);
                        }

                        lapStart = LocalTime.now();
                        hasPassedCheckpoint = false;
                        passedFinishAtStart = true;
                    } else if (passedFinishAtStart && message.toString().equals("w")) {
                        Log.d(logTag, "checkpoint reached");
                        hasPassedCheckpoint = true;
                    }

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(logTag, "MQTT client delivery complete");
            }
        });

        connectToBroker(mqttAndroidClient, null);
    }

    private void connectToBroker(MqttAndroidClient client, String clientId) {
        // Set up connection options for the connection to the MQTT broker
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        // Add more options if necessary
        try {
            // Try to connect to the MQTT broker
            IMqttToken token = client.connect(options);
            // Set up callbacks for the result
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(logTag, "MQTT client is now connected to MQTT broker");

                    subscribeToTopic(topicAll);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(logTag, "MQTT client failed to connect to MQTT broker: " +
                            exception.getLocalizedMessage());
                }
            });
        } catch (MqttException e) {
            Log.e(logTag, "MQTT exception while connecting to MQTT broker, reason: " +
                    e.getReasonCode() + ", msg: " + e.getMessage() + ", cause: " + e.getCause());
            e.printStackTrace();
        }
    }

    public void publishMessage(String topic, String msg) {
        String finalTopic = topicBase + topic;
        Log.d(logTag, "publishing message to topic: " + finalTopic);
        byte[] encodedPayload = new byte[0];
        try {
            // Convert the message to a UTF-8 encoded byte array
            encodedPayload = msg.getBytes("UTF-8");
            // Store it in an MqttMessage
            MqttMessage message = new MqttMessage(encodedPayload);
            // Set parameters for the message
            message.setQos(qualityOfService);
            message.setRetained(false);
            // Publish the message via the MQTT broker
            mqttAndroidClient.publish(finalTopic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            Log.e(logTag, "MQTT exception while publishing topic to MQTT broker, msg: " + e.getMessage() +
                    ", cause: " + e.getCause());
            e.printStackTrace();
        }
    }

    public void publishMessage(topicType topicType, String msg) {
        String topic = clientCar + "/" + topicType.toString();
        publishMessage(topic, msg);
    }

    private void subscribeToTopic(String topic) {
        String finalTopic = topicBase + topic;
        try {
            // Try to subscribe to the topic
            IMqttToken token = mqttAndroidClient.subscribe(finalTopic, qualityOfService);
            // Set up callbacks to handle the result
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(logTag, "MQTT client is now subscribed to topic " + finalTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(logTag, "MQTT failed to subscribe to topic " + finalTopic + " because: " +
                            exception.getLocalizedMessage());
                }
            });
        } catch (MqttException e) {
            Log.e(logTag, "MQTT exception while subscribing to topic on MQTT broker, reason: " +
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

