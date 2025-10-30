package com.example.rabibitmqhello.controller;

import com.example.rabibitmqhello.config.RabbitMQConfig;
import com.example.rabibitmqhello.dto.ImageTaskDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


/**
 * 비동기 작업 큐를 위한 Producer컨트롤러
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class WorkController {
  private final RabbitTemplate rabbitTemplate;
  
  @PostMapping("/work/request-image-resize")
  public String requestImageResize(@RequestParam String fileName) {
    log.info(
        "Received image resize request for: {}",
        fileName
    );
    
    //시나리오: 하나의 원본 이미지로 3개의 다른 크기의 썸네일 생성
    //이렇게 여러개의 독립적인 작업을 큐에 한 번에 발행가능
    for (int i = 0; i < 3; i++) {
      String taskId = UUID.randomUUID().toString();
      int width = 1920 / (i + 1);
      int height = 1080 / (i + 1);
      
      //1 처리할 작업의 내용을 DTO 객체로 생성
      ImageTaskDto task = new ImageTaskDto(
          taskId,
          fileName,
          width,
          height
      );
      
      /**
       * 생성된 작업을 메시지 큐로 발생
       * 이 컨트롤러의 책임은 여기까지
       * 이후 작업은 Consumer들이 알아서 처리
       */
      rabbitTemplate.convertAndSend(
          RabbitMQConfig.EXCHANGE_NAME,
          RabbitMQConfig.ROUTING_KEY,
          task
      );
      
      log.info("Task published to quere: {}", task);
    }
    //3. 클라이언트에게는 작업이 완료 되었다는 것이아닌, 성공적으로 요쳥 되었다는 사실을 즉시 응답
    return "3 image resize tasks have been successfully requested,";
  }
}
