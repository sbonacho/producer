package com.sbonacho.seda.load.producer.bus.alone;

import com.sbonacho.seda.load.producer.bus.producer.Sender;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;

public class SenderAlone implements Sender<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SenderAlone.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public SenderAlone(Map<String, Object> config) {
        LOGGER.info("Initializing Kafka Producer ...");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        ProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(config);
        this.kafkaTemplate = new KafkaTemplate<>(factory);
    }

    @Override
    public ListenableFuture send(String event, String topic) {
        LOGGER.debug("message -> {}; topic -> {}", event, topic);
        return kafkaTemplate.send(topic, event);
    }
}
