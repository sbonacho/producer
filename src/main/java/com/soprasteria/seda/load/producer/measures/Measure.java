package com.soprasteria.seda.load.producer.measures;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Measure {
    private final String name;
    private Long initialTime;
    private Long finalTime;
    private Long elapsedTime;
    private Long count;
    private Long bytes;
    private final Map<Integer, Long> countSecond;
    private final Map<Integer, Long> bytesSecond;

    public Measure(String name) {
        this.initialTime = System.nanoTime();
        this.countSecond = new ConcurrentHashMap<>();
        this.bytesSecond = new ConcurrentHashMap<>();
        this.name = name;
        this.bytes = 0L;
        this.count = 0L;
    }

    public void add(Integer bytes){
        count++;
        this.bytes = this.bytes + bytes;
        Integer sec = Math.round(System.nanoTime() - initialTime);

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
}
