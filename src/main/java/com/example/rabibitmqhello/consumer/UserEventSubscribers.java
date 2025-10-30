package com.example.rabibitmqhello.consumer;


import com.example.rabibitmqhello.config.FanoutEventConfig;
import com.example.rabibitmqhello.dto.UserEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 예시: 발행/구독(Pub/Sub) 패턴의 Subscriber(구독자) 그룹
 * 이 클래스는 `UserController`(발행자)가 발행한 이벤트를 구독하여
 * 처리하는 여러 'Subscriber'들의 역할을 시뮬레이션합니다.
 */

@Slf4j
@Component
public class UserEventSubscribers {
  
  @RabbitListener(queues = FanoutEventConfig.EMAIL_QUEUE_NAME)
  public void handleUserDeactivationForEmail(UserEventDto event) {
    log.info(
        "[Subscriber: Email Service ] Event received. Sending " +
            "deactivation email to user '{}'. Event Details: {}",
        event.getUserId(), event
    );
  }
  
  @RabbitListener(queues = FanoutEventConfig.AUTH_QUEUE_NAME)
  public void handleUserDeactivationForAuth(UserEventDto event) {
    log.info(
        "[Subscriber: Auth Service] Event received. Expiring " +
            "all sessions for user '{}'. Event Details: {}",
        event.getUserId(), event
    );
  }
  
  @RabbitListener(queues = FanoutEventConfig.FEED_QUEUE_NAME)
  public void handleUserDeactivationForFeed(UserEventDto event) {
    log.info(
        "[Subscriber: Feed Service] Event received. Archiving " +
            "all posts by user '{}'. Event Details: {}",
        event.getUserId(), event
    );
  }

}
