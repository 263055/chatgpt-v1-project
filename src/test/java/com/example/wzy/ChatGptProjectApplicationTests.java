package com.example.wzy;

import cn.hutool.extra.mail.MailUtil;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.listener.ConsoleStreamListener;
import com.plexpt.chatgpt.listener.SseStreamListener;
import com.plexpt.chatgpt.util.Proxys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.net.Proxy;
import java.util.Arrays;

import static com.example.wzy.utils.ValidateCodeUtils.generateValidateCode;

@SpringBootTest
class ChatGptProjectApplicationTests {

    @Test
    void contextLoads() {
        String code = generateValidateCode(6) + "";
        MailUtil.send("2630559606@qq.com", "欢迎注册", "你的验证码是" + code + "有效期是五分钟", false);
    }

    @Test
    void context() {
    }

    @Test
    public void test() {
        Proxy proxy = Proxys.http("127.0.0.1", 7890);

        ChatGPTStream chatGPTStream = ChatGPTStream.builder()
                .timeout(900)
                .apiKey("sk-hIiEJtqsCZxfjXRpwA8YSfJdnWdIj0BXLUJ1i1KlsZVVqAIn")
                .proxy(proxy)
                .apiHost("https://api.ohmygpt.com/")
                .build()
                .init();


        ConsoleStreamListener listener = new ConsoleStreamListener();
        Message message = Message.of("你好");
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(Arrays.asList(message))
                .build();
        System.out.println(100);
        chatGPTStream.streamChatCompletion(chatCompletion, listener);
        System.out.println(11100);
    }

    @Test
    public void sseEmitter() {
        //国内需要代理 国外不需要
        Proxy proxy = Proxys.http("127.0.0.1", 7890);

        ChatGPTStream chatGPTStream = ChatGPTStream.builder()
                .timeout(600)
                .apiKey("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")
                .proxy(proxy)
                .apiHost("https://api.openai.com/")
                .build()
                .init();

        SseEmitter sseEmitter = new SseEmitter(-1L);

        SseStreamListener listener = new SseStreamListener(sseEmitter);
        Message message = Message.of("你好");

        listener.setOnComplate(msg -> {
            //回答完成，可以做一些事情
            System.out.println("00");
        });
        chatGPTStream.streamChatCompletion(Arrays.asList(message), listener);

        System.out.println(sseEmitter);
    }
}
