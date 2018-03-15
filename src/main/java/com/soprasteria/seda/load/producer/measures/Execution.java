package com.soprasteria.seda.load.producer.measures;

public class Execution {

    public static enum STATE {
        SUCCESS,
        ERROR
    }

    private final Measure measure;

    public Execution(Long total, String name) {
        measure =  new Measure(name, total);
    }
    public void add(STATE state, Integer chars) {
        Integer bytes = chars * 2;
        measure.add(bytes, state.equals(STATE.ERROR));
    }
    public Long getTotal() {
        return measure.getTotal();
    }
    public Measure getMeasure() {
        return measure;
    }


}
