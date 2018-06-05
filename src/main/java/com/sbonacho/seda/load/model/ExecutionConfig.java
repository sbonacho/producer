package com.sbonacho.seda.load.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionConfig extends AbstractDto {
    private Long messages;
    private String queueManager;
    private Integer threads;
    private Integer[] length;
    private Map<String, Object> producerConfig;
    private String topic;
    private Boolean waitForAck;

    public Long getMessages() {
        return messages;
    }

    public void setMessages(Long messages) {
        this.messages = messages;
    }

    public Integer[] getLength() {
        return length;
    }

    public void setLength(Integer[] length) {
        this.length = length;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Map<String, Object> getProducerConfig() {
        return producerConfig;
    }

    public void setProducerConfig(Map<String, Object> producerConfig) {
        this.producerConfig = producerConfig;
    }

    public Boolean getWaitForAck() {
        return waitForAck;
    }

    public void setWaitForAck(Boolean waitForAck) {
        this.waitForAck = waitForAck;
    }

    public String getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }
}
