package com.annotate.access.producer;

import com.annotate.access.event.AccessUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
//@Service
@RequiredArgsConstructor
public class AccessEventProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    public void accessUpdated(Long videoId, Long viewerId, String status) {
        rabbitTemplate.convertAndSend(
                exchange,
                "ann-acc-up",
                new AccessUpdatedEvent(videoId, viewerId, status)
        );
    }


}
