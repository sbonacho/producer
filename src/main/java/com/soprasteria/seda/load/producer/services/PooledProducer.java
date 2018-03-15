package com.soprasteria.seda.load.producer.services;

import com.soprasteria.seda.load.model.ExecutionConfig;
import com.soprasteria.seda.load.producer.bus.alone.SenderAlone;
import com.soprasteria.seda.load.producer.bus.kafka.producer.ControlSender;
import com.soprasteria.seda.load.producer.bus.producer.Sender;
import com.soprasteria.seda.load.producer.measures.Execution;
import com.soprasteria.seda.load.producer.utils.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class PooledProducer implements Producer {

    @Value("${producer.globalTimeout}")
    private Integer seconds;

    private static final Logger LOGGER = LoggerFactory.getLogger(PooledProducer.class);

    @Autowired
    private ControlSender control;

    private void subRun(ExecutionConfig config){
        Sender<String> sender = new SenderAlone(config.getProducerConfig());
        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getThreads());
        final RandomString random = new RandomString(config.getLength());
        Execution execution = new Execution(config.getMessages(), config.getTopic());
        for (int i = 0; i < config.getMessages(); i++) {
            executor.submit(() -> {
                String message = random.nextString();
                sender.send(message, config.getTopic()).addCallback((result) -> {
                    execution.add(Execution.STATE.SUCCESS, message.length());
                }, (error) -> {
                    execution.add(Execution.STATE.ERROR, message.length());
                });
            });
        }

        try {
            // Esperar a que acaben todos los hilos -> Muy mejorable pero de momento se queda así..... ;)
            LOGGER.info("---- RUNNING ......");
            int i = 0;
            while ( ! execution.getMeasure().isFinished() &&  i < seconds * 10) {
                Thread.sleep(100);
                i++;
            }
            if ( ! execution.getMeasure().isFinished() )
                execution.getMeasure().finish();
            LOGGER.info("---- FINISH ......");
            print(execution, config);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run(ExecutionConfig config){
        control.send(config).addCallback((result) -> {
            LOGGER.info("-------------------------------------------------");
            this.subRun(config);
        }, (e) -> {
            LOGGER.error("Impossible to send control message {}", e);
        });
    }

    public void print(Execution execution, ExecutionConfig config) {
        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00000" );
        LOGGER.info("---------------- Configured: ---------");
        LOGGER.info("Messages: {}", config.getMessages());
        LOGGER.info("Topic: {}", config.getTopic());
        LOGGER.info("---------------- Results of Test: ---------");
        LOGGER.info("Total Bytes: {}", execution.getMeasure().getBytes());
        LOGGER.info("Messages OK: {}", execution.getMeasure().getCount());
        LOGGER.info("Messages KO: {}", execution.getMeasure().getErrors());
        Long elapsed = execution.getMeasure().getElapsedTime();
        elapsed = elapsed == null ? 0 : elapsed;
        LOGGER.info("Elapsed Time: {} s", df2.format((float)(elapsed)/1000000000));
        LOGGER.info("{}", execution.getMeasure());
    }
}
