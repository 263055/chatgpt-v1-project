package com.example.wzy;

import cn.dev33.satoken.SaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationPropertiesScan
@EnableConfigurationProperties
@SpringBootApplication
public class ChatGptProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatGptProjectApplication.class, args);
        System.out.println("启动成功：Sa-Token配置如下：" + SaManager.getConfig());
    }
}
