package com.study;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringTest {

    @Test
    void substring() {
        // given
        String str = "1".repeat(200);

        // when
        String result = str.substring(0, 150);

        // then
        assertThat(result.length()).isEqualTo(150);
    }
}
