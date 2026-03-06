package com.annotate.media.producer;

import com.annotate.media.event.MediaProcessedEvent;
import com.annotate.media.event.MediaUploadedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MediaEventProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${media.uploaded}")
    private String routingKey1;

    @Value("${media.processed}")
    private String routingKey2;

    private final RabbitTemplate rabbitTemplate;

    public void mediaUploaded(Long videoId, String s3Key) {
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey1,
                new MediaUploadedEvent(videoId, s3Key)
        );
    }

    public void mediaProcessed(Long videoId, String streamUrl) {
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey2,
                new MediaProcessedEvent(videoId, streamUrl)
        );
    }


}
