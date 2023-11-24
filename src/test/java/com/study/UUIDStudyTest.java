package com.study;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("UUID 학습 테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class UUIDStudyTest {

    @Test
    void UUID_문자열을_UUID_객체로_변환() {
        // given
        UUID uuid = UUID.randomUUID();
        String string = uuid.toString();

        // when
        UUID fromString = UUID.fromString(string);

        // then
        assertThat(fromString).isEqualTo(uuid);
    }
}
