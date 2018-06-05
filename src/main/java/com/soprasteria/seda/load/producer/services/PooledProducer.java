package com.soprasteria.seda.load.producer.services;

import com.soprasteria.seda.load.model.ExecutionConfig;
import com.soprasteria.seda.load.producer.bus.alone.SenderAlone;
import com.soprasteria.seda.load.producer.bus.kafka.producer.ControlSender;
import com.soprasteria.seda.load.producer.bus.mqtt.SenderMqtt;
import com.soprasteria.seda.load.producer.bus.producer.Sender;
import com.soprasteria.seda.load.producer.measures.Execution;
import com.soprasteria.seda.load.producer.utils.RandomMessage;
import com.soprasteria.seda.load.producer.utils.RandomPresence;
import com.soprasteria.seda.load.producer.utils.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class PooledProducer implements Producer {

    @Value("${producer.globalTimeout}")
    private Integer seconds;

    @Value("${producer.logPeriod}")
    private Integer logPeriod;


    private static final Logger LOGGER = LoggerFactory.getLogger(PooledProducer.class);

    @Autowired
    private ControlSender control;

    public void run(ExecutionConfig config){
        control.send(config).addCallback((result) -> {
            LOGGER.info("run: INIT");
            Sender<String> sender = null;
            RandomMessage random = null;
            switch (config.getQueueManager()) {
                case "mqtt":
                    sender = new SenderMqtt(config.getProducerConfig());
                    random = new RandomPresence();
                    break;
                default:
                    sender = new SenderAlone(config.getProducerConfig());
                    random = new RandomString(config.getLength());
            }
            final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getThreads());
            final Execution execution = new Execution(config.getMessages(), config.getTopic());

            this.execute(config, execution, sender, executor, random);
            this.waitForTheEnd(execution);
            this.print(execution);
            sender = null;
            System.gc();
        }, (e) -> {
            LOGGER.error("Impossible to send control message {}", e);
        });
    }
    private void execute(ExecutionConfig config, Execution execution, Sender<String> sender, ThreadPoolExecutor executor, RandomMessage random){
        if (! execution.isFinished()) {
            executor.submit(() -> {
                String message = random.next();
                if (new BigDecimal(execution.getCount()).divide(BigDecimal.valueOf(logPeriod)).remainder(BigDecimal.ONE).equals(BigDecimal.ZERO))
                    LOGGER.info("acks: {} - {}/{}", execution.getMeasure().getCount(), execution.getCount(), execution.getTotal());
                sender.send(message, config.getTopic()).addCallback((result) -> {

                    execution.add(Execution.STATE.SUCCESS, message.length());
                    if (config.getWaitForAck()) {
                        execution.addCount();
                        this.execute(config, execution, sender, executor, random);
                    }
                }, (error) -> {

                    execution.add(Execution.STATE.ERROR, message.length());
                    if (config.getWaitForAck()) {
                        execution.addCount();
                        this.execute(config, execution, sender, executor, random);
                    }
                });
                if (! config.getWaitForAck()) {
                    execution.addCount();
                    this.execute(config, execution, sender, executor, random);
                }
            });
        }
    }

    /**
     * Esperar a que acaben todos los hilos -> Mejorable, pero de momento se queda así... ;)
     * @param execution
     */
    private void waitForTheEnd (Execution execution) {
        try {
            int i = 0;
            while ( ! execution.getMeasure().isFinished() &&  i < seconds * 10) {
                Thread.sleep(100);
                i++;
            }
            if ( ! execution.getMeasure().isFinished() )
                execution.getMeasure().finish();
            LOGGER.info("---- FINISH ......");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void print(Execution execution) {
        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00000" );
        Long elapsed = execution.getMeasure().getElapsedTime();
        elapsed = elapsed == null ? 0 : elapsed;
        LOGGER.info("Elapsed Time: {} s", df2.format((float)(elapsed)/1000000000));
        LOGGER.info("{}", execution.getMeasure());
    }
}