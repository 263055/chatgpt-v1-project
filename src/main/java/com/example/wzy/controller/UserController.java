package com.example.wzy.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.extra.mail.MailUtil;
import com.example.wzy.common.R;
import com.example.wzy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.example.wzy.utils.ValidateCodeUtils.generateValidateCode;
import static com.example.wzy.utils.ValidateCodeUtils.validateMail;

@Slf4j
@RestController
@RequestMapping("/user/")
@CrossOrigin()
public class UserController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    /**
     * 邮箱注册
     * @param request 用户邮箱，状态
     * @return 返回成功状态
     */
    @PostMapping("/sentMailCode")
    public R<String> userSentMailCode(@RequestBody Map<String, String> request) {
        String mail = request.get("email");
        String judge = request.get("judge");
        System.out.println( "获取当前会话是否已经登录" + StpUtil.isLogin());
        if (!validateMail(mail)) {
            return R.error("邮箱格式错误,请重新输入");
        }
        String code = generateValidateCode(6) + "";
        try {
            if (Objects.equals(judge, "0")) MailUtil.send(mail, "欢迎注册", "你的验证码是" + code + "有效期是五分钟", false);
            else if (Objects.equals(judge, "1")) MailUtil.send(mail, "密码找回", "你的验证码是" + code + "有效期是五分钟", false);
        } catch (Exception e) {
            return R.error("邮件发送失败");
        }
        stringRedisTemplate.opsForValue().set(mail, code, 10, TimeUnit.MINUTES);
        log.info(code);
        return R.success("邮件发送成功,请注意查收");
    }

    /**
     * 邮箱注册---验证
     * @param request 请求的参数，包括 账号 验证码
     * @return 返回验证状态
     */
    @PostMapping("/judgeReSetMailCode")
    public R<String> userJudgeReSetMailCode(@RequestBody Map<String, String> request) {
        String account = request.get("email");
        String mailCode = request.get("code");
        R<String> res = userService.resetPasswordJudeCode(account, mailCode);
        if (res.getCode() == 0) return R.error(res.getMsg());
        return R.success(res.getData());
    }


    /**
     * 邮箱注册---验证
     * @param request 请求的参数，包括 账号 密码 验证码
     * @return 返回注册状态
     */
    @PostMapping("/judgeMailCode")
    public R<String> userJudgeMailCode(@RequestBody Map<String, String> request) {
        String account = request.get("account");
        String password = request.get("password");
        String mailCode = request.get("mailCode");
        R<String> res = userService.registerUser(account, password, mailCode);
        if (res.getCode() == 0) return R.error(res.getMsg());
        return R.success("注册成功");
    }

    /**
     * 重置密码
     * @param request 请求的参数，包括 账号 密码
     * @return 返回注册状态
     */
    @PostMapping("/resetPassword")
    public R<String> userResetPassword(@RequestBody Map<String, String> request) {
        String account = request.get("email");
        String password = request.get("password");
        R<String> res = userService.resetPassword(account, password);
        if (res.getCode() == 0) return R.error(res.getData());
        return R.success(res.getData());
    }


    /**
     * 用户登录
     * @param request 请求的参数：邮箱 密码
     */
    @PostMapping("/login")
    public SaResult userLogin(@RequestBody Map<String, String> request) {
        String account = request.get("account");
        String password = request.get("password");
        SaResult tokenInfo = userService.login(account, password);
        if (tokenInfo.getCode() == 500) return SaResult.error(tokenInfo.getMsg());
        return SaResult.data(tokenInfo);
    }

    /**
     * 用户登录
     */
    @GetMapping("/login")
    public R<String> userIsLogin() {
        boolean isLogin = StpUtil.isLogin();
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        System.out.println("/login" + tokenInfo.getLoginId() + " 的登录信息是 " + tokenInfo);
        return isLogin ? R.success(StpUtil.getTokenValue()) : R.error("登录失败");
    }

    /**
     * 用户注销
     */
    @GetMapping("/layout")
    public SaResult userLayout() {
        System.out.println("/layout" + StpUtil.getTokenInfo());
        StpUtil.logout();
        System.out.println(StpUtil.getTokenInfo());
        return SaResult.data("退出成功！！");
    }
}

