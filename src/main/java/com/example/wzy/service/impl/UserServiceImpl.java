package com.example.wzy.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wzy.common.BaseContext;
import com.example.wzy.common.R;
import com.example.wzy.entity.User;
import com.example.wzy.mapper.UserMapper;
import com.example.wzy.service.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    // 账号注册 2630559606@qq.com
    public R<String> registerUser(String account, String password, String mailCode) {
        String curCode = stringRedisTemplate.opsForValue().get(account);
        // 1. 判断 curCode是否过期
        if (curCode == null) {
            return R.error("账号错误或验证码过期,请重新发送验证码");
        }
        // 2. 判断验证码是否错误
        if (!curCode.equals(mailCode)) {
            return R.error("验证码错误！");
        }
        // 3. 判断账号是否存在以及密码是否合适
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User getUser = userService.getOne(queryWrapper);
        if (getUser != null && getUser.getAccount().equals(account)) {
            return R.error("账号已存在,注册失败");
        }
        // 5. 注册账号
        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        user.setChargedAmount(0L);
        user.setGrade(0L);
        user.setTimes(5L);
        userService.save(user);
        // 6. 销毁验证码
        stringRedisTemplate.delete(account);
        return R.success("注册成功");
    }

    @Override
    public R<String> resetPasswordJudeCode(String account, String code) {
        String curCode = stringRedisTemplate.opsForValue().get(account);
        // 1. 判断 curCode是否过期
        if (curCode == null) {
            return R.error("验证码过期或为空,请重新发送验证码");
        }
        // 2. 判断验证码是否错误
        if (!curCode.equals(code)) {
            return R.error("验证码错误！");
        }
        return R.success("验证成功！！");
    }

    // 用户登录
    public SaResult login(String account, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User getUser = userService.getOne(queryWrapper);
        // 1. 账号不存在
        if (getUser == null) {
            return SaResult.error("账号不存在,请跳转注册界面");
        }
        // 2. 密码错误
        if (!getUser.getPassword().equals(password)) {
            return SaResult.error("密码错误!!!");
        }
        // 3. 登录成功
        // 为这个账号创建了一个Token凭证，且通过 Cookie 上下文返回给了前端
        // Cookie 可以从后端控制往浏览器中写入 Token 值。
        // Cookie 会在前端每次发起请求时自动提交 Token 值
        StpUtil.login(account);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        System.out.println(tokenInfo);
        return SaResult.data(tokenInfo);
    }

    @Override
    public R<String> resetPassword(String account, String password) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", account).set("password", password);
        userService.update(null, updateWrapper);
        return R.success("密码重置成功");
    }
}