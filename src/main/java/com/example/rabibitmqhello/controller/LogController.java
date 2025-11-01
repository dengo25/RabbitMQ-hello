package com.example.rabibitmqhello.controller;


import com.example.rabibitmqhello.config.TopicLogConfig;
import com.example.rabibitmqhello.dto.LogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 예시: Topic Exchange로 로그 메시지를 발행하는 Producer 컨트롤러
 * 예를 들어, "미국에서 발생한 에러 로그"라는 내용이 있다면,
 * 이를 `log.error.usa`와 같은 라우팅 키로 변환하여 발행합니다.
 * 이렇게 함으로써, Consumer들은 `log.error.*`나 `log.#.usa`와 같은
 * 유연한 패턴으로 원하는 로그만 선별하여 구독할 수 있게 됩니다.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class LogController {
  
  private final RabbitTemplate rabbitTemplate;
  
  /**
   * 외부 시스템으로부터 로그 데이터를 받아 Topic Exchange로 발행합니다.
   *
   * @return 작업 처리 결과 메세지
   * @Param logDTO 로그 레벨, 국가, 메세지등을 담고 있는 DTO
   */
  @PostMapping("/logs")
  public String publishing(@RequestBody LogDto logDto) {
    // 1. 수신한 데이터 보강: 서버 시간 기준으로 타임스탬프 설정
    logDto.setTimestamp(LocalDateTime.now());
    
    // 2. 동적 라우팅 키 생성(가장 중요한 부분)
    // 메세지의 내용(level, country)를 조합하여 라우팅 키를 생성
    String routingKey = String.format("log.%s.%s",
        logDto.getLevel().toLowerCase(),
        logDto.getCountry().toLowerCase()
    );
    
    
    
    //3 Topic Exchanhge로 메세지를 발생
    // 이 메세지는 routungKey와 TopicLogConfig에 정의된 바인딩 패턴이 일치하는 모든 큐로 전달
    rabbitTemplate.convertAndSend(
        TopicLogConfig.TOPIC_EXCHANGE_NAME,
        routingKey, //동적으로 생성된 라우팅 키 사용
        logDto
    );
    
    log.info("Published log with routing Key [{}]: {}",routingKey,logDto);
    
    //4. 클라이언트에게 즉시 응답
    return "Log message publushed successFully";
  }
}
