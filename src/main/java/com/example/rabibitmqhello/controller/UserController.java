package com.example.rabibitmqhello.controller;

import com.example.rabibitmqhello.config.FanoutEventConfig;
import com.example.rabibitmqhello.dto.UserEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 예시: 사용자 관련 이벤트를 발행(Publish)하는 Producer 컨트롤러
 * <p>
 * 이 컨트롤러는 사용자 서비스의 핵심 API 엔드포인트 역할을 하면서,
 * 동시에 중요한 비즈니스 이벤트가 발생했을 때 이를 시스템 전체에 알리는
 * '이벤트 발행자(Event Producer)'의 역할을 수행합니다.
 * <p>
 * "사용자가 비활성화되었다"는 '사실(Fact)'이 발생하면, 이 컨트롤러는
 * 해당 사실을 담은 이벤트 메시지를 생성하여 Fanout Exchange로 전송합니다.
 * <p>
 * 이 컨트롤러는 이 이벤트를 누가, 왜 받아서 처리하는지에 대해서는
 * 전혀 알 필요가 없습니다. 오직 "이런 일이 일어났다" 고 방송할 책임만 가집니다.
 * 이것이 바로 이벤트 기반 아키텍처의 핵심인 '느슨한 결합'입니다.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
  private final RabbitTemplate rabbitTemplate;
  
  @PostMapping("/users/{userId}/deactivate")
  public String deactivateUser(@PathVariable String userId) {
    log.info("Request received to deactivate user: {}", userId);
    
    UserEventDto event = new UserEventDto(
        userId,
        "USER_DEACTIVATED",
        LocalDateTime.now()
    );
    
    rabbitTemplate.convertAndSend(
        FanoutEventConfig.FANOUT_EXCHANGE_NAME,
        "",
        event
    );
    
    return "User" + userId + " deactivated. Event publication initiated.";
  }
}
