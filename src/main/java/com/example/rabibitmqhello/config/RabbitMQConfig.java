package com.example.rabibitmqhello.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  
  public static final String EXCHANGE_NAME = "hello.exchange";
  public static final String QUEUE_NAME = "hello.queue";
  public static final String ROUTING_KEY = "hello.key.#";
}
