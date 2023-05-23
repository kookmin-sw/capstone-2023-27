package com.hany.capstone.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("ChatBotController 테스트")
class ChatBotControllerTest {
    @Autowired
    private ChatBotController chatbot;

    @Test
    void chatBotApi() throws Exception {
        //given
        String question = "삼성전자 현재 주가 알려줘";
        //when
        String answer = chatbot.chatBotApi(question);
        //then
        System.out.println(answer);
    }
}