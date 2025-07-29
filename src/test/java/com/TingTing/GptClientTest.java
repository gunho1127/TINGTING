package com.TingTing;

import com.TingTing.gpt.GptClient;
import com.TingTing.gpt.GptMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GptClientTest {

    @Autowired
    private GptClient gptClient;

    @Test
    void testGetFirstMessage() {
        // given
        String prompt = "당신은 25살 여성 디자이너이고, 다정하고 유머러스한 성격으로 소개팅을 시작합니다.";

        // when
        String aiResponse = gptClient.getFirstMessage(prompt);

        // then
        System.out.println("AI 첫 인삿말: " + aiResponse);
    }

    @Test
    void testGetReply() {
        // given
        List<GptMessage> chatHistory = List.of(
                new GptMessage("system", "당신은 25살 여성 디자이너이고, 다정하고 유머러스한 성격으로 소개팅을 시작합니다."),
                new GptMessage("user", "안녕하세요! 저는 27살 개발자입니다. 만나서 반가워요 😊")
        );

        // when
        String reply = gptClient.getReply(chatHistory);

        // then
        System.out.println("GPT 응답: " + reply);
    }
}

