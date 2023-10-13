package com.example.wzy.listener;

import com.alibaba.fastjson.JSON;
import com.plexpt.chatgpt.entity.chat.ChatChoice;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.listener.AbstractStreamListener;
import com.plexpt.chatgpt.util.SseHelper;

import lombok.SneakyThrows;
import okhttp3.sse.EventSource;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * sse
 *
 * @author plexpt
 */
@Slf4j
@RequiredArgsConstructor
public class YSseStreamListener extends AbstractStreamListener {

    final SseEmitter sseEmitter;


    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        if (data.equals("[DONE]")) {
            sseEmitter.send("[DONE]");
            onComplate.accept(lastMessage);
        } else {
            ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
            // 读取Json
            List<ChatChoice> choices = response.getChoices();
            if (choices == null || choices.isEmpty()) {
                return;
            }
            Message delta = choices.get(0).getDelta();
            String text = delta.getContent();
            if (text != null) {
                lastMessage += text;
                onMsg(text);
            }
        }
    }

    @Override
    public void onMsg(String message) {
        String data = message.replaceAll(" ", "&#32;").replaceAll("\\n", "<br>");
        SseHelper.send(sseEmitter, data);
    }


    @Override
    public void onError(Throwable throwable, String response) {
        SseHelper.complete(sseEmitter);
    }

}
