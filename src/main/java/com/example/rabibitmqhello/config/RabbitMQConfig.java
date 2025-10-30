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
  public DirectExchange exchange() {
    return new DirectExchange(EXCHANGE_NAME);
  }
  
  @Bean
  public Queue queue() {
    // durable: true(기본값) - 서버가 재시작 되어도 큐가 유지됨
    return new Queue(QUEUE_NAME);
  }
  
  @Bean
  public Binding binding(Queue queue, DirectExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
  }
  
  /**
   * Spring AMQP가 사용할 메시지 컨버터(MessageConverter)를 Bean으로 등록합니다.
   *
   * 이 Bean을 등록함으로써 RabbitMQ로 메시지를 보낼 때(publish)는
   * Java 객체(예: OrderDto)가 자동으로 JSON 문자열로 변환되고,
   * 메시지를 받을 때(consume)는 JSON 문자열이 다시 Java 객체로 변환됩니다.
   *
   * @return Jackson 라이브러리를 사용하는 JSON 메시지 컨버터
   */
  @Bean
  public MessageConverter jsonMessageConverter() {
    //Jackson2: Java 객체와 json간의 변환을 담당하는 사실상의 표준 라이브러리
    return new Jackson2JsonMessageConverter();
  }
}
