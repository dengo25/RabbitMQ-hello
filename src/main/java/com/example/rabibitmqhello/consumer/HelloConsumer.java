package com.example.rabibitmqhello.consumer;

import com.example.rabibitmqhello.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloConsumer {
  
  @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME) //이 어노테이션으로 지정된 큐를 수신대기하는 메시지 리스너가 된다
  public void receiveMessage(String message) {
    log.info("received message: {}", message);
  }
}
