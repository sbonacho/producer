package com.sbonacho.seda.load.producer.bus.mqtt;

import com.sbonacho.seda.load.producer.bus.producer.Sender;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SenderMqtt implements Sender<String>, MqttCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(SenderMqtt.class);

    private MqttClient myClient;
    private MqttTopic topic;
    private MqttDeliveryToken token;
    private int pubQoS;
    private MqttConnectOptions connOpt = new MqttConnectOptions();

    public SenderMqtt(Map<String, Object> config) {
        LOGGER.info("SenderMqtt initializing");

        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);
        connOpt.setUserName(String.valueOf(config.get("username")));
        connOpt.setPassword(String.valueOf(config.get("password")).toCharArray());
        pubQoS = config.get("qos") != null ? (Integer) config.get("qos") : 0;
        // Connect to Broker
        try {
            myClient = new MqttClient(String.valueOf(config.get("brokerUrl")), String.valueOf(config.get("clientId")));
            myClient.setCallback(this);
            myClient.connect(connOpt);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        LOGGER.info("Connected to {} QoS -> {}", String.valueOf(config.get("brokerUrl")), pubQoS);
    }

    @Override
    public ListenableFuture send(String payload, String t) {
        return new ListenableFuture() {

            private void run () throws MqttException {
                if (topic ==null)
                    topic = myClient.getTopic(t);
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(pubQoS);
                message.setRetained(false);
                token = topic.publish(message);
                token.waitForCompletion();
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return token !=null && token.isComplete();
            }

            @Override
            public Object get() throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }

            @Override
            public void addCallback(ListenableFutureCallback listenableFutureCallback) {

            }

            @Override
            public void addCallback(SuccessCallback successCallback, FailureCallback failureCallback) {
                try {
                    run();
                    successCallback.onSuccess( true);
                } catch (MqttException e) {
                    failureCallback.onFailure(e);
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void connectionLost(Throwable t) {
        LOGGER.info("Connection Lost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        LOGGER.debug("message arrived");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOGGER.debug("message complete");
    }
}
