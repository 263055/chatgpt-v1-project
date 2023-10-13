package com.example.wzy.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("commentdetail")
public class CommentDetail {
  private String id;
  private String usercomment;
  private String gptcomment;
}
