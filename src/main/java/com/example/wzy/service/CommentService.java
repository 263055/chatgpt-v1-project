package com.example.wzy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wzy.common.R;
import com.example.wzy.entity.Comment;
import com.example.wzy.entity.CommentDetail;
import com.example.wzy.entity.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

public interface CommentService extends IService<Comment> {

    public R<String> AddComment(String name, String preinstall);

    public R<String> UpdateCommentPreInstall(String preinstall, String id, String name);

    public R<List<CommentDetail>> GetCommentDetail(String commentId);

    public SseEmitter AddCommentDetail(String userComment, String buttonId, String region);

    public R<String > getComment();

    public R<String> deleteButton(String mail, String id);
}
