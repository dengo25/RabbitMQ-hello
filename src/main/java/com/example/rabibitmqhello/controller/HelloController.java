package com.example.rabibitmqhello.controller;

import com.example.rabibitmqhello.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {
  private final RabbitTemplate rabbitTemplate;
  
  @GetMapping("/send")
  public String sendMessage(@RequestParam String message) {
    //메세지를 hello.exchange로 hello.key.1 이라는 라우팅 키로 발행
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.EXCHANGE_NAME,
        "hello.key.1",
        message);
    return "Message sent: " + message;
    
  }
}
