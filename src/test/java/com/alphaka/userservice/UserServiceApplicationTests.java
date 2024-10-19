package com.alphaka.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("develop")
@SpringBootTest
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
