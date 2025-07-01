package de.hfu.cnc;

import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MessageConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class);

    @RabbitListener(queues = "#{queue.name}")
    private void receive(String input) {
        LOG.info("Received: {}", input);
    }
}
