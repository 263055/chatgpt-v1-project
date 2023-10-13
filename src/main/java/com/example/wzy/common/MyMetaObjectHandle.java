package com.example.wzy.common;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.wzy.utils.RedisIdWorker;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 自定义元数据对象处理器
 */
@Component
public class MyMetaObjectHandle implements MetaObjectHandler {
    /**
     * 插入的时候自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("deadline", LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }

//    /**
//     * 更新的时候自动填充
//     */
//    @Override
//    public void updateFill(MetaObject metaObject) {
//        metaObject.setValue("updateTime", LocalDateTime.now());
//        String tokenValue = StpUtil.getTokenValue();
//        String userMail = (String) StpUtil.getLoginIdByToken(tokenValue);
//        long commentId = RedisIdWorker.nextId(userMail);
//        metaObject.setValue("updateUser", commentId);
//    }
}
