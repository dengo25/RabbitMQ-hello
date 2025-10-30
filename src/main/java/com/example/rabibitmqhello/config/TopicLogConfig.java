package com.example.rabibitmqhello.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 예시: 콘텐츠 기반 라우팅을 위한 Topic Exchange 설정
 
 * 이 클래스는 메시지의 라우팅 키(Routing Key)를 패턴 매칭하여
 * 특정 주제(Topic)에 관심 있는 Consumer에게만 메시지를 전달하는
 * 시나리오를 RabbitMQ로 구현합니다.
 
 * ---- 비유: 뉴스 구독 시스템 ----
 * Topic Exchange는 다양한 카테고리의 뉴스를 발행하는 '뉴스 데스크'와 같습니다.
 * 각 Consumer(구독자)는 자신이 관심 있는 뉴스 카테고리 패턴을 등록하고,
 * 뉴스 데스크는 들어온 뉴스의 카테고리(라우팅 키)를 보고 구독자들에게 배포합니다.
 
 * ---- 와일드카드 규칙 ----
 * *  (별표): 정확히 '하나'의 단어(word)를 대체합니다. (예: `log.*.korea`)
 * #  (해시): '0개 이상'의 단어를 대체합니다. (예: `log.#`)
 
 * 이 예제에서는 로그 메시지를 라우팅하는 시스템을 구축합니다.
 */


@Configuration
public class TopicLogConfig {
  
  public static final String TOPIC_EXCHANGE_NAME = "logs.topic.exchange";
  
  public static final String ALL_LOGS_QUEUE = "logs.all.queue";
  public static final String ERROR_LOGS_QUEUE = "logs.error.queue";
  public static final String KOREAN_LOGS_QUEUE = "logs.korea.queue";
  
  // --- Binding Key Patterns (구독할 주제 패턴) ---
// '#' 패턴: 'log.'로 시작하는 모든 로그를 구독합니다.
  public static final String BINDING_PATTERN_ALL = "log.#";
  // '*' 패턴: 'log.error.'로 시작하고, 그 뒤에 한 단어가 더 오는 로그
// (예: log.error.auth, log.error.payment)만 구독합니다.
  public static final String BINDING_PATTERN_ERROR = "log.error.*";
  // '*' 패턴: 'log.'와 '.korea' 사이에 어떤 단어든 하나만 있는 로그
// (예: log.info.korea, log.warn.korea)만 구독합니다.
  public static final String BINDING_PATTERN_KOREA = "log.*.korea";
  
  @Bean
  public TopicExchange topicExchange() {
    return new TopicExchange(TOPIC_EXCHANGE_NAME);
  }

// --- Subscriber Queues ---
  
  @Bean
  public Queue allLogsQueue() {
    return new Queue(ALL_LOGS_QUEUE);
  }
  
  @Bean
  public Queue errorLogsQueue() {
    return new Queue(ERROR_LOGS_QUEUE);
  }
  
  @Bean
  public Queue koreanLogsQueue() {
    return new Queue(KOREAN_LOGS_QUEUE);
  }

// --- Bindings (어떤 패턴으로 구독할지 정의) ---
  @Bean
  public Binding allLogsBinding(Queue allLogsQueue, TopicExchange topicExchange) {
    // "allLogsQueue"는 "log.#" 패턴으로 Exchange를 구독합니다.
    return BindingBuilder.bind(allLogsQueue)
        .to(topicExchange)
        .with(BINDING_PATTERN_ALL);
  }
  
  public Binding errorLogsBinding(Queue errorLogsQueue, TopicExchange topicExchange) {
    // "errorLogsQueue"는 "log.error.*" 패턴으로 Exchange를 구독합니다.
    return BindingBuilder.bind(errorLogsQueue)
        .to(topicExchange)
        .with(BINDING_PATTERN_ERROR);
  }
  
  @Bean
  public Binding koreanLogsBinding(Queue koreanLogsQueue, TopicExchange topicExchange) {
    // "koreanLogsQueue"는 "log.*.korea" 패턴으로 Exchange를 구독합니다.
    return BindingBuilder.bind(koreanLogsQueue)
        .to(topicExchange)
        .with(BINDING_PATTERN_KOREA);
  }
  
  
  @Bean
  public MessageConverter jsonMessageConverter() {
    
    //Jackson2: Java 객체와 json간의 변환을 담당하는 사실상의 표준 라이브러리
    return new Jackson2JsonMessageConverter();
  }
}
