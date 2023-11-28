package com.mallang.statistics.statistic.utils;

import static com.mallang.statistics.statistic.utils.LocalDateUtils.isBetween;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("LocalDateUtils 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LocalDateUtilsTest {

    @Test
    void 특정_시간이_어떠한_기간에_포함되는지_판단한다() {
        // given
        LocalDate start = LocalDate.of(2000, 10, 2);
        LocalDate include = LocalDate.of(2000, 10, 4);
        LocalDate end = LocalDate.of(2000, 10, 7);

        LocalDate notInclude1 = LocalDate.of(2000, 10, 1);
        LocalDate notInclude2 = LocalDate.of(2000, 10, 8);

        // when & then
        assertThat(isBetween(start, end, start)).isTrue();
        assertThat(isBetween(start, end, include)).isTrue();
        assertThat(isBetween(start, end, end)).isTrue();
        assertThat(isBetween(start, end, notInclude1)).isFalse();
        assertThat(isBetween(start, end, notInclude2)).isFalse();
    }
}
