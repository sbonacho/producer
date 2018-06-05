package com.sbonacho.seda.load.producer.bus.producer;

import org.springframework.util.concurrent.ListenableFuture;

public interface Sender<P> {
    ListenableFuture send(P payload, String topic);
}
