package com.example.rabibitmqhello.controller;

import com.example.rabibitmqhello.config.HeaderNotificationConfig;
import com.example.rabibitmqhello.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 예시: Headers Exchange로 알림 메시지를 발행하는 Producer 컨트롤러
 * <p>
 * ---- 핵심 학습 포인트 ----
 * Headers Exchange를 사용하려면 메시지를 보내기 전에 헤더를 설정해야 합니다.
 * `rabbitTemplate.convertAndSend()` 메서드는 헤더를 직접 추가하는 기능이
 * 제한적이므로, 여기서는 더 세밀한 제어가 가능한 다음 단계를 따릅니다:
 * <p>
 * 1. `MessageProperties` 객체를 생성하여 필요한 헤더를 설정합니다.
 * 2. `MessageConverter`를 사용하여 메시지 본문(Body)과 헤더(Properties)를
 * 하나의 `Message` 객체로 결합합니다.
 * 3. `rabbitTemplate.send()` 메서드를 사용하여 완성된 `Message` 객체를 전송합니다.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {
  
  private final RabbitTemplate rabbitTemplate;
  private final MessageConverter messageConverter;
  
  /**
   * 알림을 보내고, 채널 및 베타 테스터 여부에 따라 헤더를 동적으로 추가합니다.
   *
   * @param notificationDto 알림의 본문 내용 (JSON)
   * @param channel         알림 채널 (예: "sms", "kakao") – 헤더로 사용됨
   * @param isBetaTester    베타 테스터 여부 – 헤더로 사용됨
   * @return 작업 처리 결과 메시지
   */
  @PostMapping("/notifications")
  public String sendNotification(
      @RequestBody NotificationDto notificationDto,
      @RequestParam String channel,
      @RequestParam(required = false) Boolean isBetaTester
  ) {
    log.info("Sending notification via [{}], isBetaTester [{}]: {}",
        channel, isBetaTester, notificationDto);
    
    //1 메세지 메타데이터를 담을 객체를 생성
    MessageProperties messageProperties = new MessageProperties();
    
    //2. 요청 파라미터를 기반으로 헤더를 동적으로 설정한다
    messageProperties.setHeader("channel", channel);
    if (Boolean.TRUE.equals(isBetaTester)) {
      //헤더 값은 보통 문자열로 전달
      messageProperties.setHeader("beta-tester", "true");
    }
    
    //[시나리오2] smsBinding의 or조건을 테스트하기위한 헤더
    if ("high".equalsIgnoreCase(notificationDto.getPriority())) {
      messageProperties.setHeader("priority", "high");
    }
    
    //3.메시지 본문 과 레더를 하나의 AMQP Message 객체로 만든다
    // 이때 MessageConverter가 DTO를 JSON바이트 배열로 변환
    Message message = messageConverter.toMessage(
        notificationDto,
        messageProperties
    );
    
    //4. 완성된 Message 객체를 Headers Exchange로 잔송한다.
    //라우팅 키는 사용하지 않으므로 비 문자열("")을 전달
    rabbitTemplate.send(
        HeaderNotificationConfig.HEADERS_EXCHANGE_NAME,
        "",
        message
    );
    
    return "Notification request sent with custom headers";
  }
}
