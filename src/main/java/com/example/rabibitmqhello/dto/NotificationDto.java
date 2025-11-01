package com.example.rabibitmqhello.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationDto {
  
  private String recipient;
  private String content;
  private String priority; //이게 빠져있는듯
  
}
