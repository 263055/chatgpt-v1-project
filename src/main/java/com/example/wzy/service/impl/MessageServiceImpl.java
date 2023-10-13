package com.example.wzy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wzy.mapper.MessageMapper;
import com.example.wzy.service.MessageService;
import com.plexpt.chatgpt.entity.chat.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
}
