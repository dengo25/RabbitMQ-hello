package com.example.rabibitmqhello.consumer;

import com.example.rabibitmqhello.config.HeaderNotificationConfig;
import com.example.rabibitmqhello.dto.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 예시: Headers Exchange를 구독하는 전문화된 알림 Consumer 그룹
 * <p>
 * 이 클래스는 `NotificationProducerController`가 발행한 알림 메시지를
 * 구독하여 처리하는 여러 'Subscriber'들의 역할을 시뮬레이션합니다.
 * <p>
 * ---- 핵심 아키텍처 ----
 * 이 Consumer들은 메시지가 어떤 헤더를 가졌기 때문에 자신들의 큐로
 * 라우팅되었는지 전혀 알 필요가 없습니다. 그저 자신의 책임(Queue)에 할당된
 * 메시지를 묵묵히 처리할 뿐입니다.
 * <p>
 * 라우팅의 모든 복잡성은 RabbitMQ 설정(`HeadersNotificationConfig`)에
 * 위임되어 있으며, 이로 인해 각 Consumer 서비스는 매우 단순하고 자신의
 * 비즈니스 로직에만 집중할 수 있게 됩니다.
 */


@Slf4j
@Component
public class NotificationSubscribers {
  
  /**
   * [SMS 발송 서비스]의 구독자입니다.
   * 이 큐는 `HeadersNotificationConfig`에서 'any' (OR) 조건으로 바인딩되었습니다.
   * 따라서 아래 조건 중 하나라도 만족하는 메시지를 수신합니다:
   * 1. `channel` 헤더가 `sms`인 경우
   * 2. `priority` 헤더가 `high`인 경우
   *
   * @param notificationDto 수신한 알림 메시지*
   */
  @RabbitListener(queues = HeaderNotificationConfig.SMS_NOTIFICATION_QUEUE)
  public void consumeNotification(NotificationDto notificationDto) {
    log.info(
        "[SMS Subscriber] Received notification to send. Dispatching SMMS: {}",
        notificationDto
    );
    
    
  }
  
  /**
   * [카카오 알림톡 발송 서비스] 의 구독자입니다.
   * 이 큐는 `HeadersNotificationConfig`에서 'all' (AND) 조건으로 바인딩되었습니다.
   * 따라서 아래 조건을 '모두' 만족하는 메시지만을 수신합니다:
   * 1. `channel` 헤더가 `kakao`인 경우
   * 2. `beta-tester` 헤더가 `true`인 경우
   */
  
  @RabbitListener(queues = HeaderNotificationConfig.KAKAO_NOTIFICATION_QUEUE)
  public void consumeKakaoNotification(NotificationDto notificationDto) {
    log.info(
        "[Kakao Subscriber] Received notification for beta tester. Sending Alimtalk: {}",
        notificationDto
    );
    
  }
}
