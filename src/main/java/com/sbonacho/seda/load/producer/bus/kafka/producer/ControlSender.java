package com.sbonacho.seda.load.producer.bus.kafka.producer;

import com.sbonacho.seda.load.model.ExecutionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class ControlSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlSender.class);

    @Value("${producer.controlTopic}")
    private String control;

    @Autowired
    private KafkaTemplate<String, ExecutionConfig> kafkaTemplate;

    public ListenableFuture send(ExecutionConfig config) {
        return kafkaTemplate.send(control, config);
    }
}