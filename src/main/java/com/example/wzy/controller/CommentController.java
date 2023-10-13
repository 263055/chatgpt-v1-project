package com.example.wzy.controller;

import com.example.wzy.common.R;
import com.example.wzy.entity.Comment;
import com.example.wzy.entity.CommentDetail;
import com.example.wzy.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/comment/")
@CrossOrigin()
public class CommentController {
//    @Resource
//    private StringRedisTemplate stringRedisTemplate;

//    @Resource
//    private CommentDetailMapper commentDetailMapper;


//    @Resource
//    private CommentDetailService commentDetailService;

    @Resource
    private CommentService commentService;

    /**
     * 获取按钮---初始化页面的时候得到按钮信息
     */
    @ResponseBody
    @PostMapping("/getComment")
    public R<String> userGetComment() {
        return commentService.getComment();
    }

    /**
     * 删除按钮---删除某个按钮
     */
    @PostMapping("/deleteComment")
    public R<String> userDeleteComment(@RequestBody Map<String, String> request) {
        String mail = request.get("mail");
        String id = request.get("id");
        return commentService.deleteButton(mail, id);
    }

    /**
     * 添加预设---增加对话框
     * @param request 请求的参数，包括 预设
     * @return 返回状态
     */
    @PostMapping("/addPreinstall")
    public R<String> userAddComment(@RequestBody Map<String, String> request) {
        String preinstall = request.get("preinstall");
        String name = request.get("name");
        return commentService.AddComment(name, preinstall);
    }

    /**
     * 修改预设---用户修改预设
     * @param request 请求的参数，系统预设
     * @return 返回：调用接口返回的结果
     */
    @PostMapping("/updateCommentPreInstall")
    public R<String> userUpdateCommentPreInstall(@RequestBody Map<String, String> request) {
        String preinstall = request.get("preinstall");
        String id = request.get("id");
        String name = request.get("name");
        return commentService.UpdateCommentPreInstall(preinstall, id, name);
    }

    /**
     * 获取消息---获取历史的消息
     * @return 返回：调用接口返回的结果
     */
    @GetMapping("/getCommentDetail")
    public R<List<CommentDetail>> userGetCommentDetail(@RequestParam String id) {
        return R.success(commentService.GetCommentDetail(id).getData());
    }

    /**
     * 添加消息---添加新的消息
     * @return 返回：调用接口返回的结果
     */
    @GetMapping("/addCommentDetail")
    public SseEmitter userAddCommentDetail(@RequestParam String userComment, @RequestParam String buttonId, @RequestParam String region ) {
        return commentService.AddCommentDetail(userComment, buttonId, region);
    }
}