package com.example.wzy.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plexpt.chatgpt.entity.chat.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
