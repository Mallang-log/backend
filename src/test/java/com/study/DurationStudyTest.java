package com.study;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("Duration 학습 테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class DurationStudyTest {

    @Test
    void Between_은_인자의_순서에_따라_양수음수가_바뀐다() {
        // given
        LocalDateTime before = LocalDateTime.of(2000, 10, 4, 10, 2);
        LocalDateTime after = LocalDateTime.of(2000, 10, 4, 20, 2);

        // when
        long hours1 = Duration.between(before, after).toHours();
        long hours2 = Duration.between(after, before).toHours();

        // then
        assertThat(hours1).isEqualTo(10);
        assertThat(hours2).isEqualTo(-10);
    }

    @Test
    void toHours_메서드는_분이_포함되면_무시한다() {
        // given
        LocalDateTime before = LocalDateTime.of(2000, 10, 4, 10, 2);
        LocalDateTime after1 = LocalDateTime.of(2000, 10, 4, 10, 12);
        LocalDateTime after2 = LocalDateTime.of(2000, 10, 4, 10, 59);

        // when
        long hours1 = Duration.between(before, after1).toHours();
        long hours2 = Duration.between(before, after2).toHours();

        // then
        assertThat(hours1).isZero();
        assertThat(hours2).isZero();
    }
}
