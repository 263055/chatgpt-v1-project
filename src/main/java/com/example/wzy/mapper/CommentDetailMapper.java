package com.example.wzy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.wzy.entity.CommentDetail;
import com.example.wzy.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentDetailMapper extends BaseMapper<CommentDetail> {
}
