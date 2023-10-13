package com.example.wzy.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private String account;
  private String password;
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime deadline;
  private Long chargedAmount;
  private Long grade;
  private Long times;
}
