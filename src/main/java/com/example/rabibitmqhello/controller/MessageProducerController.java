package com.example.rabibitmqhello.controller;

import com.example.rabibitmqhello.config.RabbitMQConfig;
import com.example.rabibitmqhello.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageProducerController {
  private final RabbitTemplate rabbitTemplate;
  
  @GetMapping("/send/text")
  public String sendTextMessage(@RequestParam String message) {
    //메세지를 hello.exchange로 hello.key.1 이라는 라우팅 키로 발행
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.EXCHANGE_NAME,
        "hello.key.1",
        message);
    return "Text Message sent: " + message;
    
  }
  
  @GetMapping("/send/order")
  public String sendOrderMessage() {
    
    //1. 발생할 비즈니스 이벤트 데이터를 생성
    OrderDto order = new OrderDto(
        UUID.randomUUID().toString(),
        "Spring in Action",
        new BigDecimal("45000"),
        1,
        LocalDateTime.now()
    );
    //2. 객체를 exchange 로 발행
    // 개발자는 json 변환을 신경 안 써도 된다. 앞서 설정한 MesageConverter가 이 객체를 자동으로 직렬화해서 전송
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.EXCHANGE_NAME,
        "hello.key.order",
        order);
    
    return "Order message sent successfully!";
  }
}
