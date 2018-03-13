package com.soprasteria.seda.load.producer.bus.kafka.producer;

import com.soprasteria.seda.load.producer.bus.producer.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class SenderImpl<E> implements Sender<E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SenderImpl.class);

    @Autowired
    private KafkaTemplate<String, E> kafkaTemplate;

    public ListenableFuture send(E event, String topic) {
        LOGGER.info("message -> {}; topic -> {}", event, topic);
        return kafkaTemplate.send(topic, event);
    }
}