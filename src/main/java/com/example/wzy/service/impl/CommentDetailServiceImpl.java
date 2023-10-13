package com.example.wzy.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wzy.common.BaseContext;
import com.example.wzy.common.R;
import com.example.wzy.entity.Comment;
import com.example.wzy.entity.CommentDetail;
import com.example.wzy.entity.User;
import com.example.wzy.mapper.CommentDetailMapper;
import com.example.wzy.mapper.CommentMapper;
import com.example.wzy.mapper.UserMapper;
import com.example.wzy.service.CommentDetailService;
import com.example.wzy.service.CommentService;
import com.example.wzy.service.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CommentDetailServiceImpl extends ServiceImpl<CommentDetailMapper, CommentDetail> implements CommentDetailService {

}
