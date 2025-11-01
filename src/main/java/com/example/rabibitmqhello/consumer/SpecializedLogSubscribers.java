package com.example.rabibitmqhello.consumer;


import com.example.rabibitmqhello.config.TopicLogConfig;
import com.example.rabibitmqhello.dto.LogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 예시: Topic Exchange를 구독하는 전문화된 Consumer(구독자) 그룹
 * <p>
 * 이 클래스는 `LogProducerController`가 발행한 로그 메시지를
 * 각자의 '관심 주제(Topic)'에 따라 선택적으로 구독하여 처리하는
 * 여러 전문화된 Consumer들의 역할을 시뮬레이션합니다.
 * <p>
 * ---- 실제 아키텍처와의 관계 ----
 * 실제 마이크로서비스 환경에서는 아래의 각 리스너 메서드가
 * 완전히 독립된 별개의 서비스(애플리케이션)에 존재하게 됩니다.
 * - [로그 아카이빙 서비스]: 모든 로그를 수집하여 저장
 * - [장애 모니터링 서비스]: 에러 등급의 로그만 수신하여 긴급 알림 발송
 * - [지역별 분석 서비스]: 특정 국가의 로그만 수신하여 통계 처리
 * <p>
 * 이 예제는 이처럼 서로 다른 책임을 가진 서비스들이 어떻게 하나의
 * 이벤트 스트림(로그)을 공유하면서도 독립적으로 동작하는지를 보여줍니다.
 */

@Slf4j
@Component
public class SpecializedLogSubscribers {
  
  /**
   * [모든 로그 아카이버]의 구독자입니다.
   * 바인딩 패턴 'log.#'을 사용하여 'log.'로 시작하는 모든 메시지를 수신합니다.
   * 이 서비스의 책임은 모든 로그를 빠짐없이 기록하는 것입니다.
   */
  @RabbitListener(queues = TopicLogConfig.ALL_LOGS_QUEUE)
  public void consumeAllLogs(LogDto logDto) {
    log.info(
        "[AllLogsArchiver] Received log for archiving. RoutingKey Pattern: '{}'. Log: {}",
        TopicLogConfig.BINDING_PATTERN_ALL, logDto
    );
  }
  
  
  /**
   * [에러 감시 및 알림 서비스]의 구독자입니다.
   * 바인딩 패턴 'log.error.*'을 사용하여 'error' 등급의 로그만 수신합니다.
   * 이 서비스는 시스템의 장애를 감지하고 즉각 조치하는 책임을 가집니다.
   */
  @RabbitListener(queues = TopicLogConfig.ERROR_LOGS_QUEUE)
  public void consumeErrorLogs(LogDto logDto) {
    log.warn(
        "[ErrorAlerter] CRITICAL error detected! RoutingKey Pattern: '{}'. Log: {}",
        TopicLogConfig.BINDING_PATTERN_ERROR, logDto
    
    );
  }
  
  /**
   * [한국 지역 통계 분석기]의 구독자입니다.
   * 바인딩 패턴 'log.*.korea'를 사용하여 국가 코드가 'korea'인 로그만 수신합니다.
   * 이 서비스는 특정 지역의 사용자 활동을 분석하는 책임을 가집니다.
   */
  @RabbitListener(queues = TopicLogConfig.KOREAN_LOGS_QUEUE)
  public void consumeKoreanLogs(LogDto logDto) {
    log.info(
        "[KoreanStatsAnalyzer] Received a log from Korea. RoutingKey Pattern: '{}'. Log: {}",
        TopicLogConfig.BINDING_PATTERN_KOREA, logDto
    );
  }
  
  
  /**
   *
   
   curl -X POST http://localhost:8080/logs \
   -H "Content-Type: application/json" \
   -d '{
   "level": "error",
   "country": "korea",
   "message": "Login failed"
   }'
   
   */
}
