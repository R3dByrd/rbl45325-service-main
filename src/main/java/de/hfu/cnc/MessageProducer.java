package de.hfu.cnc;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.DirectExchange;

@EnableScheduling
@Component
public class MessageProducer {

    private int counter = 0;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange direct;

    @Value("${service.routingKey}")
    private String routingKey;

    @Scheduled(fixedDelay = 1000, initialDelay = 5000)
    public void send() {
        template.convertAndSend(
            direct.getName(),
            routingKey,
            routingKey + " Message " + counter++
        );
    }
}
