package com.example.wzy.utils;

import com.plexpt.chatgpt.ChatGPT;
import lombok.experimental.UtilityClass;

import java.net.InetSocketAddress;
import java.net.Proxy;

@UtilityClass
public class Proxys {


    public static ChatGPT getCharGPT(){
        //国内需要代理 国外不需要
        Proxy proxy = com.plexpt.chatgpt.util.Proxys.http("127.0.0.1", 7890);

        return ChatGPT.builder()
                .apiKey("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")
                .proxy(proxy)
                .timeout(900)
                .apiHost("https://api.openai.com/") //反向代理地址
                .build()
                .init();
    }

    /**
     * http 代理
     * @param ip
     * @param port
     * @return
     */
    public static Proxy http(String ip, int port) {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
    }

    /**
     * socks5 代理
     * @param ip
     * @param port
     * @return
     */
    public static Proxy socks5(String ip, int port) {
        return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(ip, port));
    }
}