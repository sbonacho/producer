package com.sbonacho.seda.load.producer.measures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Measure {

    private static final Logger LOGGER = LoggerFactory.getLogger(Measure.class);

    private final String name;
    private Long initialTime;
    private Long finalTime;
    private Long elapsedTime;
    private Long count;
    private Long errors;
    private Long bytes;
    private final Map<Integer, Long> countSecond;
    private final Map<Integer, Long> bytesSecond;
    private final Long total;
    private boolean finished;

    public Measure(String name, Long total) {
        this.total = total;
        this.initialTime = System.nanoTime();
        this.countSecond = new ConcurrentHashMap<>();
        this.bytesSecond = new ConcurrentHashMap<>();
        this.name = name;
        this.bytes = 0L;
        this.count = 0L;
        this.errors = 0L;
        finished = false;
    }

    public void add(Integer bytes, boolean error){
        count++;
        if (count.equals(total)) {
            this.finish();
        }
        if (error) {
            errors++;
        }
        this.bytes = this.bytes + bytes;
        Integer sec = Math.round((float)(System.nanoTime() - initialTime)/1000000000);

        Long prev = countSecond.get(sec);
        prev = prev == null ? 0L : prev;
        countSecond.put(sec, prev + 1);

        prev = bytesSecond.get(sec);
        prev = prev == null ? 0L : prev;
        bytesSecond.put(sec, prev + bytes);
    }

    public void finish() {
        this.finalTime = System.nanoTime();
        this.elapsedTime = finalTime - initialTime;
        this.finished = true;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public Long getCount() {
        return count;
    }

    public Long getBytes() {
        return bytes;
    }
    public Map<Integer, Long> getCountSecond() {
        return countSecond;
    }

    public Map<Integer, Long> getBytesSecond() {
        return bytesSecond;
    }

    public Long getErrors() {
        return errors;
    }
    public Long getTotal() {
        return total;
    }

    @Override
    public String toString() {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
