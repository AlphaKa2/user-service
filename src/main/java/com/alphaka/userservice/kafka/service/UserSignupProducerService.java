package com.alphaka.userservice.kafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSignupProducerService {

    private static final String USER_SIGNUP_TOPIC = "user-signup";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(Long id) {
        log.info("사용자 회원가입 이벤트 메시지 전송");
        kafkaTemplate.send(USER_SIGNUP_TOPIC, String.valueOf(id));
    }
}
