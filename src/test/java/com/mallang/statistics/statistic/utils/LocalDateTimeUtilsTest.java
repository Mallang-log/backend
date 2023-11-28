package com.mallang.statistics.statistic.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("LocalDateTimeUtils 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LocalDateTimeUtilsTest {

    @Test
    void 현재_시간에서_초와_나노초를_제외한_시간을_반환한다() {
        // given
        LocalDateTime localDateTime = LocalDateTimeUtils.nowWithoutSeconds();

        // when & then
        assertThat(localDateTime.getSecond()).isZero();
        assertThat(localDateTime.getNano()).isZero();
    }
}
