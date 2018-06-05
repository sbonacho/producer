package com.sbonacho.seda.load.producer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

public class RandomPresence implements RandomMessage{

    private ObjectMapper mapper = new ObjectMapper();

    private RandomString random = new RandomString(new Integer[]{10});

    class PresenceLogged {
        private String clientId;
        private BigDecimal longitude;
        private BigDecimal latitude;
        private Integer heartrate;

        public String getClientId() {
            return random.next();
        }

        public BigDecimal getLongitude() {
            return BigDecimal.valueOf(120);
        }

        public BigDecimal getLatitude() {
            return BigDecimal.valueOf(38);
        }

        public Integer getHeartrate() {
            return 65;
        }
    }

    @Override
    public String next() {
        try {
            return mapper.writeValueAsString(new PresenceLogged());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
