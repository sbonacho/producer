package com.soprasteria.seda.load.producer.services;

import com.soprasteria.seda.load.producer.api.model.ProducerConfig;

public interface Producer {
    public void run(ProducerConfig config);
}
