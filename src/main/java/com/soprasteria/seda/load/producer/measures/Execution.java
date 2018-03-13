package com.soprasteria.seda.load.producer.measures;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Execution {

    private final String[] states = {""};
    private final Map<String, Measure> measures;

    public Execution() {
        this.measures = new ConcurrentHashMap<>();
    }

    public void init(String name) {
        measures.put(name, new Measure(name));
    }

    public void add(String name, Integer chars) {
        Integer bytes = chars * 2;
        measures.get(name).add(bytes);
    }

    public void finish() {
        for (String name: measures.keySet()) {
            measures.get(name).finish();
        }
    }
}
