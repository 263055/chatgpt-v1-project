package com.example.wzy.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wzy.common.R;
import com.example.wzy.entity.User;

public interface UserService extends IService<User> {
    public R<String> registerUser(String account, String password, String code);

    public R<String> resetPasswordJudeCode(String account, String code);

    public SaResult login(String account, String password);

    public R<String> resetPassword(String account, String password);
}
