package com.example.rabibitmqhello.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Serializable 인터페이스를 구현하여 객체가 직렬화 가능함을 명시
 * 이는 객체를 네트워크로 전송하거나 파일에 저장할 때 필요
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto implements Serializable {
  
  private String orderId;
  
  private String productName;
  
  private BigDecimal price;
  
  private int quantity;
  
  private LocalDateTime orderDate;
  
}
