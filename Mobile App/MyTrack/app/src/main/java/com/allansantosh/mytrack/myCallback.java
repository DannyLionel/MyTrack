package com.allansantosh.mytrack;

import android.util.Log;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class myCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        ManageTracker.manage_garage bg = new ManageTracker.manage_garage();
        bg.execute(topic, message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
