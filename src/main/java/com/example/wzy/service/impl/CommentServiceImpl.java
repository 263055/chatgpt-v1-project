package com.example.wzy.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wzy.common.R;
import com.example.wzy.entity.Comment;
import com.example.wzy.entity.CommentDetail;
import com.example.wzy.listener.YSseStreamListener;
import com.example.wzy.listener.YSseStreamListener;
import com.example.wzy.mapper.CommentMapper;
import com.example.wzy.service.CommentDetailService;
import com.example.wzy.service.CommentService;
import com.example.wzy.utils.RedisIdWorker;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.util.Proxys;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CommentDetailService commentDetailService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CommentService commentService;


    @Override
    public R<String> AddComment(String name, String preinstall) {
        // 1. 获取当前会话的token值,并得到对应的账号信息
        String tokenValue = StpUtil.getTokenValue();
        String userMail = (String) StpUtil.getLoginIdByToken(tokenValue);
        String commentId = RedisIdWorker.nextId(userMail);
        // 2. 保险起见，查询数据库判断 Mail 对应的 commentId 是否重复
        //    如果 重复, 返回错误信息
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", commentId);
        queryWrapper.eq("mail", userMail);
        Comment getCommentById = commentService.getOne(queryWrapper);
        if (getCommentById != null) {
            return R.error("添加消息框失败,请重新尝试");
        }
        // 3. 保存到数据库中
        Comment saveComment = new Comment();
        saveComment.setId(commentId);
        saveComment.setMail(userMail);
        // 4. 将 preinstall 保存到数据库中
        saveComment.setType(preinstall);
        saveComment.setName(name);
        commentService.save(saveComment);
        log.info(saveComment);
        return R.success(commentId + "");
    }

    @Override
    public R<String> UpdateCommentPreInstall(String preinstall, String id, String name) {
        // 1. 获取当前会话的token值,并得到对应的账号信息
        String tokenValue = StpUtil.getTokenValue();
        String userMail = (String) StpUtil.getLoginIdByToken(tokenValue);
        // 2. 保险起见，查询数据库判断 这个对话框是否存在
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("mail", userMail);
        Comment getCommentById = commentService.getOne(queryWrapper);
        if (getCommentById == null) {
            return R.error("发送消息失败,请尝试重新开启一个新的对话框");
        }
        // 根据 preinstall 修改对应的 系统预设
        getCommentById.setType(preinstall);
        getCommentById.setName(name);
        commentService.updateById(getCommentById);
        return R.success("系统预设修改成功");
    }

    @Override
    public R<List<CommentDetail>> GetCommentDetail(String commentId) {
        // 从 redis 中查询是否存放了历史记录
        String getPreMessage = stringRedisTemplate.opsForValue().get(commentId);
        // 1.如果能够查询到，则直接返回
        if (!(getPreMessage == null || getPreMessage.equals(""))) {
            JSONArray objects = JSONUtil.parseArray(getPreMessage);
            List<CommentDetail> res = JSONUtil.toList(objects, CommentDetail.class);
            log.info("成功返回了历史对话哦，减少了数据库压力");
            return R.success(res);
        }
        // 2.如果查询不到，则查询数据库，并重新保存到redis中
        QueryWrapper<CommentDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", commentId);
        List<CommentDetail> list = commentDetailService.list(queryWrapper);
        String json = JSONUtil.toJsonStr(list);
        JSONArray objects = JSONUtil.parseArray(json);
        List<CommentDetail> res = JSONUtil.toList(objects, CommentDetail.class);
        stringRedisTemplate.opsForValue().set(commentId, JSONUtil.toJsonStr(res), 60, TimeUnit.MINUTES);
        return R.success(res);
    }

    @Override
    public R<String> getComment() {
        // 获取用户邮箱
        String tokenValue = StpUtil.getTokenValue();
        String mail = (String) StpUtil.getLoginIdByToken(tokenValue);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mail", mail)
                .orderByDesc("id")
                .last("limit 15");
        List<Comment> list = commentService.list(queryWrapper);
        return R.success(JSONUtil.toJsonStr(list));
    }

    @Override
    public R<String> deleteButton(String mail, String id) {
        String tokenValue = StpUtil.getTokenValue();
        String getMail = (String) StpUtil.getLoginIdByToken(tokenValue);
        if (!Objects.equals(getMail, mail)) {
            return R.error("对话框删除失败，请稍后重试");
        }
        boolean b1 = commentDetailService.removeById(id);
        boolean b = commentService.removeById(id);
        return (b && b1) ? R.success("删除成功") : R.error("对话框删除失败，请稍后重试");
    }

    @Override
    public SseEmitter AddCommentDetail(String userComment, String buttonId, String region) {
        //国内需要代理 国外不需要
        Proxy proxy = Proxys.http("127.0.0.1", 7890);
        ChatGPTStream chatGPTStream = ChatGPTStream.builder()
                .timeout(600)
                .apiKey("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")
                .proxy(proxy)
                .apiHost("https://api.openai.com/")
                .build()
                .init();
        // 得到sse对象并进行监听
        SseEmitter sseEmitter = new SseEmitter(-1L);
        YSseStreamListener listener = new YSseStreamListener(sseEmitter);

        Message system = Message.ofSystem(region);
        Message message = Message.of(userComment);

        listener.setOnComplate(msg -> {
            System.out.println("hello");
            //回答完成，可以做一些事情
            // 此时拿到的是json字符串，并转换为list集合
            String getPreMessage = stringRedisTemplate.opsForValue().get(buttonId);
            List<CommentDetail> allMessage = JSONUtil.toList(JSONUtil.parseArray(getPreMessage), CommentDetail.class);

            // 属性赋值
            CommentDetail newCommentDetail = new CommentDetail();
            newCommentDetail.setId(buttonId);
            newCommentDetail.setUsercomment(userComment);
            newCommentDetail.setGptcomment(msg);
            allMessage.add(newCommentDetail);

            // 将新的消息保存到数据库中
            commentDetailService.save(newCommentDetail);

            // 转换为字符串并重新添加到redis中
            String newJsonStr = JSONUtil.toJsonStr(allMessage);
            stringRedisTemplate.opsForValue().set(buttonId, newJsonStr, 60, TimeUnit.MINUTES);
        });
        chatGPTStream.streamChatCompletion(Arrays.asList(system, message), listener);
        return sseEmitter;
    }
}