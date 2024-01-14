package com.study;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("LocalDateTime 학습 테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LocalDateTimeStudyTest {

    @Test
    void 초단위를_무시하고_분까지만_가져오는_법() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.of(2000, 10, 4, 10, 20, 3, 321)
                .withNano(0)
                .withSecond(0);

        // when
        assertThat(currentDateTime.toString()).isEqualTo("2000-10-04T10:20");
    }

    @Test
    void 분_초_나노초를_제거한다() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.of(2000, 10, 4, 10, 20, 3, 321);

        // when
        LocalDateTime result = currentDateTime.truncatedTo(ChronoUnit.HOURS);

        // then
        assertThat(result).isEqualTo(LocalDateTime.of(2000, 10, 4, 10, 0));
    }
}
