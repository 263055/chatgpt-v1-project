package com.example.wzy.utils;

import cn.hutool.extra.mail.MailUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {
    /**
     * 开始时间戳
     */
    private static final long BEGIN_TIMESTAMP = 1640995200L;
    /**
     * 序列号的位数
     */
    private static final int COUNT_BITS = 32;


    private static StringRedisTemplate stringRedisTemplate;

    public RedisIdWorker(StringRedisTemplate stringRedisTemplate) {
        RedisIdWorker.stringRedisTemplate = stringRedisTemplate;
    }

    public static String nextId(String keyPrefix) {
        // 1.生成时间戳
        // 得到现在时间
        LocalDateTime now = LocalDateTime.now();
        // 转化成秒    toEpochSecond就是转换成时间戳的形式  ZoneOffset.UTC是中国区的意思
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        //得到时间戳    当前时间和过去时间的差值
        long timestamp = nowSecond - BEGIN_TIMESTAMP;
        // 2.生成序列号
        // 2.1.获取当前日期，精确到天
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        // 2.2.自增长  keyPrefix 对应的是不同的业务的处理
        // 分级结构是这样的：
        //    icr:
        //		keyprefix:
        //         		yyyy:
        // 					mm:
        //					  dd:
        // 最终会在redis中记录 我们一共生成了多少个id
        Long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);
        if(count == null){
            MailUtil.send("2630559606@qq.com", "redis警告", "redis自增长id返回为空,疑似宕机", false);
            return (timestamp << COUNT_BITS | (int)(Math.random() * 100)) + "";
        }
        // 3.拼接并返回
        // timestamp 向左移动 32位，然后和 count取或，最终得到我们想要的时间戳
        return (timestamp << COUNT_BITS | count) + "";
    }
}
