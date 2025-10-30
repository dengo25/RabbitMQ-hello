package com.example.rabibitmqhello.consumer;

import com.example.rabibitmqhello.config.RabbitMQConfig;
import com.example.rabibitmqhello.dto.ImageTaskDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 예시: 비동기 작업 큐의 Consumer (소비자/작업자)
 * 이 클래스는 RabbitMQ 대기열(Queue)에 쌓인 메시지를 하나씩 가져와서
 * 실제 비즈니스 로직을 처리하는 'Consumer' 또는 'Worker'의 역할을 합니다.
 * 이 Consumer는 Producer(작업 요청자)가 누구인지, 언제 작업을 보냈는지
 * 전혀 알 필요 없이, 오직 큐에 있는 작업에만 집중합니다.
 * 이처럼 Producer와 Consumer가 완전히 분리(Decoupled)되어 있기 때문에,
 * 한쪽에 장애가 발생해도 다른 쪽은 영향을 받지 않고 독립적으로 동작할 수 있습니다.
 * 또한 이 Consumer 애플리케이션의 인스턴스 수를 늘리는 것만으로
 * 전체 시스템의 작업 처리량을 손쉽게 확장(Scale-out)할 수 있습니다.
 */
@Slf4j
@Component
public class WorkConsumer {
  
  /**
   * 지정된 큐(RabbitMQConfig.QUEUE_NAME)를 계속 주시하다가,
   * 메시지가 들어오면 이 메서드를 자동으로 실행합니다.
   * @RabbitListener 어노테이션 덕분에 개발자는 메시지를 가져오는
   * 복잡한 과정을 직접 코딩할 필요가 없습니다.
   * @param task RabbitMQ로부터 받은 메시지.
   *        앞서 설정한 MessageConverter에 의해 JSON 문자열이
   *        자동으로 ImageTaskDto 객체로 변환(역직렬화)되어
   *        파라미터로 주입됩니다.
   */
  @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
  public void processImageTask(ImageTaskDto task) throws InterruptedException {
    log.info("Worker [{}] started processing task: {}",
        Thread.currentThread().getName(),
        task);
    
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      //스레드가 중단될경우 인터럽트 상태를 복원하고 로그를 남김
      Thread.currentThread().interrupt();
      log.error("Task processing was interrupted for task ID: {}",
          task.getTaskId(), e);
      throw e;
    }
    
    log.info("worker [{}] finished processing task: {}",
        Thread.currentThread().getName(),
        task.getTaskId());
    
  }
  
}
