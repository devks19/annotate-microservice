package com.annotate.media.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//public class RabbitMQConfig {
//
//    @Value("${rabbitmq.exchange.name}")
//    private String exchangeName;
//
//    @Value("${media.processing.queue}")
//    private String mediaProcessingQueue;
//
//    @Value("${media.uploaded}")
//    private String mediaUploadedRoutingKey;
//
//    @Bean
//    public TopicExchange exchange() {
//        return new TopicExchange(exchangeName, true, false);
//    }
//
//    @Bean
//    public Queue mediaProcessingQueue() {
//        return new Queue(mediaProcessingQueue, true);
//    }
//
//    @Bean
//    public Binding mediaUploadedBinding() {
//        return BindingBuilder
//                .bind(mediaProcessingQueue())
//                .to(exchange())
//                .with(mediaUploadedRoutingKey);
//    }
//
//    @Bean
//    public Jackson2JsonMessageConverter messageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public RabbitAdmin rabbitAdmin(ConnectionFactory cf) {
//        return new RabbitAdmin(cf);
//    }
//
//    @PostConstruct
//    public void init() {
//        System.out.println("RABBIT CONFIG LOADED");
//    }
//
////    @Bean
////    CommandLineRunner rabbitTest(RabbitTemplate rabbitTemplate) {
////        return args -> {
////            rabbitTemplate.convertAndSend("ann-ex-m", "ann-med-up", "hello");
////            System.out.println("RabbitMQ message sent");
////        };
////    }
//}

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${media.processing.queue}")
    private String mediaQueue;

    @Value("${media.uploaded}")
    private String mediaUploadedRoutingKey;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

//    @Bean
//    public Queue mediaProcessingQueue() {
//        return new Queue(mediaQueue);
//    }

    @Bean
    public Binding mediaBinding() {
        return BindingBuilder
                .bind(mediaProcessingQueue())
                .to(exchange())
                .with(mediaUploadedRoutingKey);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true); // this is the part everyone forgets
        return admin;
    }

    @Bean
    public Queue mediaProcessingQueue() {
        System.out.println("CREATING MEDIA QUEUE: " + mediaQueue);
        return new Queue(mediaQueue);
    }
}
