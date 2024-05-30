package com.example.fastfoodapplication;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

public interface BrokerObserver {
    ArrayList<String> subscriptions = new ArrayList<>();
    void update(MqttMessage data);
    default void sendMessage(BrokerHandler.topicType topicType, String message){
        System.out.println("Broker observer pattern sendMessage");
    }
    default void addSubscription(String subsription){
        subscriptions.add(subsription);
    }
    default ArrayList<String> getSubscriptions(){
        return subscriptions;
    }
}


