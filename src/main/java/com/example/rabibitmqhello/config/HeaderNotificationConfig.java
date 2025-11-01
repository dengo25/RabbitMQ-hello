package com.example.rabibitmqhello.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


/**
 * 예시: 메시지 헤더(Header) 기반 라우팅을 위한 Headers Exchange 설정
 
 * 이 클래스는 라우팅 키가 아닌 메시지의 '헤더' 속성을 기반으로
 * 메시지를 라우팅하는 시나리오를 RabbitMQ로 구현합니다.
 
 * ---- 비유: 택배 상자의 스티커 ----
 * Headers Exchange는 택배의 '주소(라우팅 키)'가 아닌, 상자에 붙은
 * '스티커(헤더)'를 보고 분류하는 시스템과 같습니다.
 * (예: "취급주의", "긴급배송", "베타테스터용" 스티커)
 
 * ---- 핵심 속성: x-match ----
 * 바인딩 시 `x-match` 속성을 'all' 또는 'any'로 지정할 수 있습니다.
 * - all (AND): 바인딩에 정의된 '모든' 헤더가 메시지에 존재하고 값도 일치해야 함.
 * - any (OR): 바인딩에 정의된 헤더 중 '하나라도' 메시지에 존재하고 값이 일치하면 됨.
 */

@Configuration
public class HeaderNotificationConfig {
  
  public static final String HEADERS_EXCHANGE_NAME = "notification.headers.exchange";
  
  private static final String SMS_NOTIFICATION_QUEUE = "notification.sms.queue";
  private static final String KAKAO_NOTIFICATION_QUEUE = "notification.kakao.queue";
  
  @Bean
  public HeadersExchange headersExchange() {
    return new HeadersExchange(HEADERS_EXCHANGE_NAME);
  }
  
  @Bean
  public Queue smsQueue() {
    return new Queue(SMS_NOTIFICATION_QUEUE);
  }
  
  @Bean
  public Queue kakaoQueue() {
    return new Queue(KAKAO_NOTIFICATION_QUEUE);
  }
  
  // --- Bindings (어떤 헤더 조건으로 구독할지 정의) ---
  
  /**
   * [시나리오 1: all 매칭 – AND 조건]
   * `kakaoQueue`는 '모든' 조건이 일치하는 메시지만 받습니다.
   * 조건: 헤더에 'channel'이 'kakao'이고, 'beta-tester'가 'true'인 경우.
   * (x-match = all)
   */
  @Bean
  public Binding kakaoBing(Queue kakaoQueue, HeadersExchange headersExchange) {
    Map<String, Object> headers = Map.of(
        "channel", "kakao",
        "beta-tester", "true"
    );
    
    return BindingBuilder.bind(kakaoQueue)
        .to(headersExchange)
        .whereAll(headers).match();
  }
  
  /**
   * [시나리오 2: any 매칭 – OR 조건]
   * `smsQueue`는 '어느 하나라도' 조건이 일치하는 메시지를 받습니다.
   * 조건: 헤더에 'channel'이 'sms'이거나, 또는 'priority'가 'high'인 경우.
   * (x-match = any)
   */
  
  @Bean
  public Binding smsBinding(Queue smsQueue, HeadersExchange headersExchange) {
    Map<String, Object> headers = Map.of(
        "channel", "sms",
        "priority", "high"
    );
    
    return BindingBuilder.bind(smsQueue)
        .to(headersExchange)
        .whereAny(headers).match();
    
  }
  
  
  @Bean
  public MessageConverter jsonMessageConverter() {
    //Jackson2: Java 객체와 json간의 변환을 담당하는 사실상의 표준 라이브러리
    return new Jackson2JsonMessageConverter();
  }
}
