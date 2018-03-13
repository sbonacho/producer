package com.soprasteria.seda.load.producer.services;

import com.soprasteria.seda.load.producer.api.model.ProducerConfig;
import com.soprasteria.seda.load.producer.bus.kafka.producer.SenderImpl;
import com.soprasteria.seda.load.producer.bus.producer.Sender;
import com.soprasteria.seda.load.producer.measures.Execution;
import com.soprasteria.seda.load.producer.utils.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class PooledProducer implements Producer {

    @Value("${producer.globalTimeout}")
    private Integer seconds;


    private static final Logger LOGGER = LoggerFactory.getLogger(PooledProducer.class);

    @Autowired
    private Sender<String> sender;

    public void run(ProducerConfig config){
        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getThreads());
        final ThreadPoolExecutor topicExec = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getTopics().length);
        final RandomString random = new RandomString(config.getLength());
        Execution execution = new Execution();
        execution.init("total-success");
        execution.init("total-error");
        execution.init("total-sent");
        for (String topic: config.getTopics()) {
            topicExec.submit(() -> {
                execution.init(topic+"-success");
                execution.init(topic+"-error");
                execution.init(topic+"-sent");
                for (int i = 0; i < config.getMessages(); i++) {
                    executor.submit(() -> {
                        String message = random.nextString();
                        sender.send(message, topic).addCallback((result) -> {
                            execution.add(topic+"-success", message.length());
                        }, (error) -> {
                            execution.add(topic+"-error", message.length());
                        });
                        execution.add(topic+"-sent", message.length());
                    });
                }
            });
        }
        try {
            LOGGER.info("---- waiting? ......");
            Thread.sleep(10);
            int i = 0;
            seconds = seconds * 100;
            LOGGER.info("-getActiveCount---------->{}", executor.getActiveCount());
            LOGGER.info("-getCompletedTaskCount--->{}", executor.getCompletedTaskCount());
            LOGGER.info("-getQueue---------------->{}", executor.getQueue());
            LOGGER.info("-getTaskCount------------>{}", executor.getTaskCount());
            LOGGER.info("-isTerminating----------->{}", executor.isTerminating());
            while ( executor.getTaskCount() >  executor.getCompletedTaskCount()) {
                Thread.sleep(10);
                LOGGER.info("-getActiveCount---------->{}", executor.getActiveCount());
                LOGGER.info("-getCompletedTaskCount--->{}", executor.getCompletedTaskCount());
                LOGGER.info("-getQueue---------------->{}", executor.getQueue());
                LOGGER.info("-getTaskCount------------>{}", executor.getTaskCount());
                LOGGER.info("-isTerminating----------->{}", executor.isTerminating());
                i++;
            }
            //executor.shutdown();
            LOGGER.info("---- FINISH ......");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
