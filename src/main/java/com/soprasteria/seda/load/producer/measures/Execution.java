package com.soprasteria.seda.load.producer.measures;

public class Execution {

    public Long getTotal() {
        return this.measure.getTotal();
    }

    public static enum STATE {
        SUCCESS,
        ERROR
    }

    private final Measure measure;
    private Long count;

    public Execution(Long total, String name) {
        count = 0L;
        measure =  new Measure(name, total);
    }
    public void add(STATE state, Integer chars) {
        measure.add(chars, state.equals(STATE.ERROR));
    }
    public void addCount (){
        count++;
    }
    public Measure getMeasure() {
        return measure;
    }
    public Long getCount() {
        return count;
    }
    public Boolean isFinished() {
        return count >= measure.getTotal();
    }

}
