package com.alphaka.userservice.kafka.service;

import com.alphaka.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountDisableConsumerService {

    private final UserService userService;

    @KafkaListener(topics = "account-disable", groupId = "account-disable-group")
    public void consumeMessage(String email) {
        log.info("사용자 비활성화 이벤트 수신");
        userService.disableUser(email);
    }

}
