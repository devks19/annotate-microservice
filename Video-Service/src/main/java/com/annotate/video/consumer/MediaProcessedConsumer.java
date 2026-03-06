package com.annotate.video.consumer;

import com.annotate.video.event.MediaProcessedEvent;
import com.annotate.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MediaProcessedConsumer {

    private final VideoService videoService;

    @RabbitListener(queues = "${media.processing.queue}")
    public void handleMediaProcessed(MediaProcessedEvent event) {
        videoService.markVideoReady(event.getVideoId(), event.getStreamUrl());
    }


}
