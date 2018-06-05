package com.sbonacho.seda.load.producer.services;

import com.sbonacho.seda.load.model.ExecutionConfig;

public interface Producer {
    public void run(ExecutionConfig config);
}
