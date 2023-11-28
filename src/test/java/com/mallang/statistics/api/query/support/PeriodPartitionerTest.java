package com.mallang.statistics.api.query.support;

import static com.mallang.common.LocalDateFixture.날짜_2020_12_31;
import static com.mallang.common.LocalDateFixture.날짜_2020_1_1;
import static com.mallang.common.LocalDateFixture.날짜_2021_12_31;
import static com.mallang.common.LocalDateFixture.날짜_2021_1_1;
import static com.mallang.common.LocalDateFixture.날짜_2022_12_31;
import static com.mallang.common.LocalDateFixture.날짜_2022_1_1;
import static com.mallang.common.LocalDateFixture.날짜_2023_10_1;
import static com.mallang.common.LocalDateFixture.날짜_2023_10_20;
import static com.mallang.common.LocalDateFixture.날짜_2023_10_31;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_12_일;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_13_월;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_19_일;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_1_수;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_20_월;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_24_금;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_26_일;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_2_목;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_3_금;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_6_월;
import static com.mallang.common.LocalDateFixture.날짜_2023_12_31;
import static com.mallang.common.LocalDateFixture.날짜_2023_1_1;
import static com.mallang.common.LocalDateFixture.날짜_2023_9_1;
import static com.mallang.common.LocalDateFixture.날짜_2023_9_30;
import static com.mallang.statistics.api.query.PeriodType.DAY;
import static com.mallang.statistics.api.query.PeriodType.MONTH;
import static com.mallang.statistics.api.query.PeriodType.WEEK;
import static com.mallang.statistics.api.query.PeriodType.YEAR;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.statistics.api.query.support.PeriodPartitioner.PeriodPart;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("기간 분할기 (PeriodPartitioner) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PeriodPartitionerTest {

    @Test
    void 주어진_기간을_일_단위로_분리한다() {
        // when
        List<PeriodPart> partition = PeriodPartitioner
                .partition(DAY, 날짜_2023_11_1_수, 날짜_2023_11_3_금);

        // then
        assertThat(partition)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new PeriodPart(날짜_2023_11_1_수, 날짜_2023_11_1_수),
                        new PeriodPart(날짜_2023_11_2_목, 날짜_2023_11_2_목),
                        new PeriodPart(날짜_2023_11_3_금, 날짜_2023_11_3_금)
                ));
    }

    @Test
    void 주어진_기간을_주_단위로_분리한다() {
        // when
        List<PeriodPart> partition = PeriodPartitioner
                .partition(WEEK, 날짜_2023_11_6_월, 날짜_2023_11_24_금);

        // then
        assertThat(partition)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new PeriodPart(날짜_2023_11_6_월, 날짜_2023_11_12_일),
                        new PeriodPart(날짜_2023_11_13_월, 날짜_2023_11_19_일),
                        new PeriodPart(날짜_2023_11_20_월, 날짜_2023_11_26_일)
                ));
    }

    @Test
    void 주어진_기간을_월_단위로_분리한다() {
        // when
        List<PeriodPart> partition = PeriodPartitioner
                .partition(MONTH, 날짜_2023_9_1, 날짜_2023_10_20);

        // then
        assertThat(partition)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new PeriodPart(날짜_2023_9_1, 날짜_2023_9_30),
                        new PeriodPart(날짜_2023_10_1, 날짜_2023_10_31)
                ));
    }

    @Test
    void 주이진_기간을_년_단위로_분리한다() {
        // when
        List<PeriodPart> partition = PeriodPartitioner
                .partition(YEAR, 날짜_2020_1_1, 날짜_2023_10_1);

        // then
        assertThat(partition)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new PeriodPart(날짜_2020_1_1, 날짜_2020_12_31),
                        new PeriodPart(날짜_2021_1_1, 날짜_2021_12_31),
                        new PeriodPart(날짜_2022_1_1, 날짜_2022_12_31),
                        new PeriodPart(날짜_2023_1_1, 날짜_2023_12_31)
                ));
    }
}
