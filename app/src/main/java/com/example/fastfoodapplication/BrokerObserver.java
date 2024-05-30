package com.example.fastfoodapplication;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

public interface BrokerObserver {
    BrokerHandler brokerHandler = BrokerHandler.instance;
    ArrayList<String> subscriptions = new ArrayList<>();
    void update(MqttMessage data);
    default void sendMessage(BrokerHandler.topicType topicType, String message){
        brokerHandler.publishMessage(topicType,message);
    }
    default void addSubscription(String subsription){
        subscriptions.add(subsription);
    }
    default ArrayList<String> getSubscriptions(){
        return subscriptions;
    };
}


