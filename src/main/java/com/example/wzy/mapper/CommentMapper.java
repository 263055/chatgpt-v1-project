package com.example.wzy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wzy.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
