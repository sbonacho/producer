package com.soprasteria.seda.load.producer.services;

import com.soprasteria.seda.load.model.ExecutionConfig;

public interface Producer {
    public void run(ExecutionConfig config);
}
