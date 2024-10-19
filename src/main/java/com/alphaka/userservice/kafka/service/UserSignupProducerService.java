package com.alphaka.userservice.kafka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSignupProducerService {

    private static final String USER_SIGNUP_TOPIC = "user-signup";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(Long id) {
        kafkaTemplate.send(USER_SIGNUP_TOPIC, String.valueOf(id));
    }
}
