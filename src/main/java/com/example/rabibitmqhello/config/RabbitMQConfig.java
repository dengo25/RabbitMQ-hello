package com.example.rabibitmqhello.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  
  public static final String EXCHANGE_NAME = "example.exchange";
  public static final String QUEUE_NAME = "example.queue";
  public static final String ROUTING_KEY = "example.key.#";
  
  @Bean
  public MessageConverter jsonMessageConverter() {
    //Jackson2: Java 객체와 json간의 변환을 담당하는 사실상의 표준 라이브러리
    return new Jackson2JsonMessageConverter();
  }
}
