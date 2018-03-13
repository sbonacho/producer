package com.soprasteria.seda.load.producer.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProducerConfig extends AbstractDto {
    private Long messages;
    private Integer threads;
    private Integer[] length;
    private String[] topics;

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

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }
}
