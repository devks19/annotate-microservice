package com.annotate.feedback.producer;

import com.annotate.feedback.event.FeedbackCreatedEvent;
import com.annotate.feedback.event.FeedbackUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedbackEventProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    public void feedbackCreated(Long feedbackId, Long videoId, Long viewerId) {
        rabbitTemplate.convertAndSend(
                exchange,
                "ann-feed-cr",
                new FeedbackCreatedEvent(feedbackId, videoId, viewerId)
        );
    }

    public void feedbackUpdated(Long feedbackId, String status) {
        rabbitTemplate.convertAndSend(
                exchange,
                "ann-feed-up",
                new FeedbackUpdatedEvent(feedbackId, status)
        );
    }


}
