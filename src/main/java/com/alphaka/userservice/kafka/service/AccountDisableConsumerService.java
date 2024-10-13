package com.alphaka.userservice.kafka.service;

import com.alphaka.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountDisableConsumerService {

    private final UserService userService;

    @KafkaListener(topics = "account-disable", groupId = "account-disable-group")
    public void consumeMessage(String email) {
        userService.disableUser(email);
    }

}
