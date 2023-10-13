package com.example.wzy.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
  private String id; // 唯一id
  private String mail; //  邮箱
  private String name; //  邮箱
  private String type; // 问答类型
}
