package com.mallang;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallangLogApplicationTests {

    @Test
    void contextLoads() {
        if (1 == 1) {
            throw new RuntimeException();
        }
    }
}
