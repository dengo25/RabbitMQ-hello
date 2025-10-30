package com.example.rabibitmqhello.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.Bidi;


/**
 * 예시: 발행/구독(Pub/Sub) 패턴을 위한 Fanout Exchange 설정
 * 이 클래스는 하나의 메시지(이벤트)를 여러 Consumer에게 '방송(Broadcasting)'하는
 * 발행/구독 시나리오를 RabbitMQ로 구현합니다.
 *
 * ---- 시나리오 ----
 * "사용자 비활성화(User Deactivated)"라는 이벤트가 발생했을 때,
 * 이 사실을 알아야 하는 여러 서비스(이메일, 인증, 피드)가 있습니다.
 * 1. Publisher: "사용자 A가 비활성화되었습니다!" 라는 메시지를
 *               Fanout Exchange에 한 번만 발행합니다.
 * 2. Fanout Exchange: 자신에게 연결된 모든 Queue에 메시지를 복사하여 전달합니다.
 * 3. Subscribers: 각자 자신의 Queue에서 메시지를 받아 필요한 처리를 합니다.
 *                 (이메일 발송, 토큰 무효화, 피드 정리 등)
 *
 * ---- 이점 ----
 * Publisher는 Subscriber가 누구인지, 몇 개인지 전혀 알 필요가 없습니다.
 * 나중에 "분석 서비스"가 추가되어도 Publisher의 코드는 변경할 필요 없이,
 * 새로운 Queue와 Binding만 추가하면 시스템을 유연하게 확장할 수 있습니다.
 */


@Configuration
public class FanoutEventConfig {
  
  public static final String FANOUT_EXCHANGE_NAME = "user.events.exchange";
  
  public static final String EMAIL_QUEUE_NAME = "user.deactivated.email.queue";
  public static final String AUTH_QUEUE_NAME = "user.deactivated.auth.queue";
  public static final String FEED_QUEUE_NAME = "user.deactivated.feed.queue";
  
  @Bean
  public FanoutExchange fanoutExchange() {
    return new FanoutExchange(FANOUT_EXCHANGE_NAME);
  }
  
  @Bean
  public Queue emailQueue() {
    // durable: true(기본값) - 서버가 재시작 되어도 큐가 유지됨
    return new Queue(EMAIL_QUEUE_NAME);
  }
  
  @Bean
  public Queue authQueue() {
    return new Queue(AUTH_QUEUE_NAME);
  }
  
  @Bean
  public Queue feedQueue() {
    return new Queue(FEED_QUEUE_NAME);
  }
  
  // 3개의 큐 모두 동일한 FanoutExchange에 바인딩
  // fanout은 라우터키가 불필요 -> 다 뿌리기 때문에
  @Bean
  public Binding emailBinding(Queue emailQueue, FanoutExchange fanoutExchange) {
    return BindingBuilder.bind(emailQueue).to(fanoutExchange);
  }
  
  @Bean
  public Binding authBinding(Queue authQueue, FanoutExchange fanoutExchange) {
    return BindingBuilder.bind(authQueue).to(fanoutExchange);
  }
  
  @Bean
  public Binding feedBinding(Queue feedQueue, FanoutExchange fanoutExchange) {
    return BindingBuilder.bind(feedQueue).to(fanoutExchange);
  }
  
  
  @Bean
  public MessageConverter jsonMessageConverter() {
    
    //Jackson2: Java 객체와 json간의 변환을 담당하는 사실상의 표준 라이브러리
    return new Jackson2JsonMessageConverter();
  }
}
