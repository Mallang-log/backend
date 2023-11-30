package com.mallang.statistics.api.presentation.support;

import static com.mallang.statistics.api.query.PeriodType.DAY;
import static com.mallang.statistics.api.query.PeriodType.MONTH;
import static com.mallang.statistics.api.query.PeriodType.WEEK;
import static com.mallang.statistics.api.query.PeriodType.YEAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.statistics.api.query.StatisticQueryCondition;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("날짜 변환기 (StatisticQueryConditionConverter) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StatisticQueryConditionConverterTest {

    @Test
    void 개수는_1개_이상이어야_한다() {
        // given
        LocalDate 기준_2023년_11월_28일 = LocalDate.of(2023, 11, 28);

        // when & then
        assertThatThrownBy(() -> {
            StatisticQueryConditionConverter.convert(DAY, 기준_2023년_11월_28일, 0);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 조회타입이_일간인_경우_조회_마지막_날은_입력_그대로이다() {
        // given
        LocalDate 기준_2023년_11월_28일 = LocalDate.of(2023, 11, 28);

        // when
        StatisticQueryCondition result = StatisticQueryConditionConverter.convert(DAY, 기준_2023년_11월_28일, 100);

        // then
        assertThat(result.lastDayInclude()).isEqualTo(기준_2023년_11월_28일);
    }

    @DisplayName("조회타입이 일간인 경우 조회 시작일은 입력된 마지막일에서 (조회개수 - 1) 만큼의 요일을 뺀 요일이다")
    @Test
    void 일간_조회_시작일_검증() {
        // given
        LocalDate 기준_2023년_11월_28일 = LocalDate.of(2023, 11, 28);
        int 데이터_1개 = 1;
        int 데이터_10개 = 10;
        int 데이터_30개 = 30;

        // when
        LocalDate 결과_2023년_11월_27일 =
                StatisticQueryConditionConverter.convert(DAY, 기준_2023년_11월_28일, 데이터_1개).startDayInclude();
        LocalDate 결과_2023년_11월_18일 =
                StatisticQueryConditionConverter.convert(DAY, 기준_2023년_11월_28일, 데이터_10개).startDayInclude();
        LocalDate 결과_2023년_10월_29일 =
                StatisticQueryConditionConverter.convert(DAY, 기준_2023년_11월_28일, 데이터_30개).startDayInclude();

        // then
        LocalDate 예상_2023년_11월_28일 = LocalDate.of(2023, 11, 28);
        LocalDate 예상_2023년_11월_19일 = LocalDate.of(2023, 11, 19);
        LocalDate 예상_2023년_10월_30일 = LocalDate.of(2023, 10, 30);
        assertThat(결과_2023년_11월_27일).isEqualTo(예상_2023년_11월_28일);
        assertThat(결과_2023년_11월_18일).isEqualTo(예상_2023년_11월_19일);
        assertThat(결과_2023년_10월_29일).isEqualTo(예상_2023년_10월_30일);
    }

    @Test
    void 조회타입이_주간인_경우_조회_마지막_날은_이번주의_마지막_일요일이다() {
        // given
        LocalDate 기준_2023년_11월_27일_월 = LocalDate.of(2023, 11, 27);
        LocalDate 기준_2023년_11월_28일_화 = LocalDate.of(2023, 11, 28);
        LocalDate 기준_2023년_12월_2일_토 = LocalDate.of(2023, 12, 2);
        LocalDate 기준_2023년_12월_3일_일 = LocalDate.of(2023, 12, 3);

        // when
        StatisticQueryCondition result1 = StatisticQueryConditionConverter.convert(WEEK, 기준_2023년_11월_27일_월, 100);
        StatisticQueryCondition result2 = StatisticQueryConditionConverter.convert(WEEK, 기준_2023년_11월_28일_화, 100);
        StatisticQueryCondition result3 = StatisticQueryConditionConverter.convert(WEEK, 기준_2023년_12월_2일_토, 100);
        StatisticQueryCondition result4 = StatisticQueryConditionConverter.convert(WEEK, 기준_2023년_12월_3일_일, 100);

        // then
        assertThat(result1.lastDayInclude())
                .isEqualTo(result2.lastDayInclude())
                .isEqualTo(result3.lastDayInclude())
                .isEqualTo(result4.lastDayInclude())
                .isEqualTo(기준_2023년_12월_3일_일);
    }

    @DisplayName("조회타입이 주간인 경우 조회 시작일은 입력된 마지막일을 포함안 주의 시작일에서 (조회 개수 - 1)만큼 전 주의 시작일이다")
    @Test
    void 주간_조회_시작일_검증() {
        // given
        LocalDate 기준_2023년_11월_26일_일 = LocalDate.of(2023, 11, 26);
        LocalDate 기준_2023년_11월_27일_월 = LocalDate.of(2023, 11, 27);
        LocalDate 기준_2023년_12월_1일_토 = LocalDate.of(2023, 12, 1);
        LocalDate 기준_2023년_12월_2일_일 = LocalDate.of(2023, 12, 2);

        int 데이터_1개 = 1;
        int 데이터_5개 = 5;

        // when
        LocalDate 결과_2023년_11월_20일_월 = StatisticQueryConditionConverter.convert(WEEK, 기준_2023년_11월_26일_일, 데이터_1개)
                .startDayInclude();
        LocalDate 결과_2023년_10월_23일_월 = StatisticQueryConditionConverter.convert(WEEK, 기준_2023년_11월_26일_일, 데이터_5개)
                .startDayInclude();

        LocalDate 결과_2023년_11월_27일_월1 = StatisticQueryConditionConverter.convert(WEEK, 기준_2023년_11월_27일_월, 데이터_1개)
                .startDayInclude();
        LocalDate 결과_2023년_10월_30일_월1 = StatisticQueryConditionConverter.convert(WEEK, 기준_2023년_11월_27일_월, 데이터_5개)
                .startDayInclude();

        LocalDate 결과_2023년_11월_27일_월2 = StatisticQueryConditionConverter.convert(WEEK, 기준_2023년_12월_1일_토, 데이터_1개)
                .startDayInclude();
        LocalDate 결과_2023년_10월_30일_월2 = StatisticQueryConditionConverter.convert(WEEK, 기준_2023년_12월_1일_토, 데이터_5개)
                .startDayInclude();

        LocalDate 결과_2023년_11월_27일_월3 = StatisticQueryConditionConverter.convert(WEEK, 기준_2023년_12월_2일_일, 데이터_1개)
                .startDayInclude();
        LocalDate 결과_2023년_10월_30일_월3 = StatisticQueryConditionConverter.convert(WEEK, 기준_2023년_12월_2일_일, 데이터_5개)
                .startDayInclude();

        // then
        LocalDate 예상_2023년_11월_20일 = LocalDate.of(2023, 11, 20);
        LocalDate 예상_2023년_10월_23일 = LocalDate.of(2023, 10, 23);

        LocalDate 예상_2023년_11월_27일 = LocalDate.of(2023, 11, 27);
        LocalDate 예상_2023년_10월_30일 = LocalDate.of(2023, 10, 30);
        assertThat(결과_2023년_11월_20일_월).isEqualTo(예상_2023년_11월_20일);
        assertThat(결과_2023년_10월_23일_월).isEqualTo(예상_2023년_10월_23일);
        assertThat(결과_2023년_11월_27일_월1)
                .isEqualTo(결과_2023년_11월_27일_월2)
                .isEqualTo(결과_2023년_11월_27일_월3)
                .isEqualTo(예상_2023년_11월_27일);
        assertThat(결과_2023년_10월_30일_월1)
                .isEqualTo(결과_2023년_10월_30일_월2)
                .isEqualTo(결과_2023년_10월_30일_월3)
                .isEqualTo(예상_2023년_10월_30일);
    }

    @Test
    void 조회타입이_월간인_경우_조회_마지막_날은_이번달의_마지막_요일이다() {
        // given
        LocalDate 기준_2023년_11월_1일 = LocalDate.of(2023, 11, 1);
        LocalDate 기준_2023년_11월_28일 = LocalDate.of(2023, 11, 28);
        LocalDate 기준_2023년_11월_30일 = LocalDate.of(2023, 11, 30);

        // when
        StatisticQueryCondition result1 = StatisticQueryConditionConverter.convert(MONTH, 기준_2023년_11월_1일, 100);
        StatisticQueryCondition result2 = StatisticQueryConditionConverter.convert(MONTH, 기준_2023년_11월_28일, 100);
        StatisticQueryCondition result3 = StatisticQueryConditionConverter.convert(MONTH, 기준_2023년_11월_30일, 100);

        // then
        assertThat(result1.lastDayInclude())
                .isEqualTo(result2.lastDayInclude())
                .isEqualTo(result3.lastDayInclude())
                .isEqualTo(기준_2023년_11월_30일);
    }

    @DisplayName("조회타입이 월간인 경우 조회 시작일은 입력된 마지막일을 포함한 달의 시작일에서 (조회 개수 - 1)만큼의 전 달의 시작일이다")
    @Test
    void 월간_조회_시작일_검증() {
        // given
        LocalDate 기준_2023년_10월_31일 = LocalDate.of(2023, 10, 31);
        LocalDate 기준_2023년_11월_26일 = LocalDate.of(2023, 11, 26);

        int 데이터_1개 = 1;
        int 데이터_4개 = 4;

        // when
        LocalDate 결과_2023년_10월_1일 = StatisticQueryConditionConverter.convert(MONTH, 기준_2023년_10월_31일, 데이터_1개)
                .startDayInclude();
        LocalDate 결과_2023년_7월_1일 = StatisticQueryConditionConverter.convert(MONTH, 기준_2023년_10월_31일, 데이터_4개)
                .startDayInclude();

        LocalDate 결과_2023년_11월_1일 = StatisticQueryConditionConverter.convert(MONTH, 기준_2023년_11월_26일, 데이터_1개)
                .startDayInclude();
        LocalDate 결과_2023년_8월_1일 = StatisticQueryConditionConverter.convert(MONTH, 기준_2023년_11월_26일, 데이터_4개)
                .startDayInclude();

        // then
        LocalDate 예상_2023년_7월_1일 = LocalDate.of(2023, 7, 1);
        LocalDate 예상_2023년_8월_1일 = LocalDate.of(2023, 8, 1);
        LocalDate 예상_2023년_10월_1일 = LocalDate.of(2023, 10, 1);
        LocalDate 예상_2023년_11월_1일 = LocalDate.of(2023, 11, 1);

        assertThat(결과_2023년_10월_1일).isEqualTo(예상_2023년_10월_1일);
        assertThat(결과_2023년_7월_1일).isEqualTo(예상_2023년_7월_1일);
        assertThat(결과_2023년_11월_1일).isEqualTo(예상_2023년_11월_1일);
        assertThat(결과_2023년_8월_1일).isEqualTo(예상_2023년_8월_1일);
    }

    @Test
    void 조회타입이_연간인_경우_조회_마지막_날은_이번년의_마지막_요일이다() {
        // given
        LocalDate 기준_2023년_1월_1일 = LocalDate.of(2023, 1, 1);
        LocalDate 기준_2023년_11월_28일 = LocalDate.of(2023, 11, 28);
        LocalDate 기준_2023년_12월_31일 = LocalDate.of(2023, 12, 31);

        // when
        StatisticQueryCondition result1 = StatisticQueryConditionConverter.convert(YEAR, 기준_2023년_1월_1일, 100);
        StatisticQueryCondition result2 = StatisticQueryConditionConverter.convert(YEAR, 기준_2023년_11월_28일, 100);
        StatisticQueryCondition result3 = StatisticQueryConditionConverter.convert(YEAR, 기준_2023년_12월_31일, 100);

        // then
        assertThat(result1.lastDayInclude())
                .isEqualTo(result2.lastDayInclude())
                .isEqualTo(result3.lastDayInclude())
                .isEqualTo(기준_2023년_12월_31일);
    }

    @DisplayName("조회타입이 연간인 경우 조회 시작일은 입력된 마지막일을 포함한 년의 시작일에서 (조회 개수 - 1)만큼의 전 년의 시작일이다")
    @Test
    void 연간_조회_시작일_검증() {
        // given
        LocalDate 기준_2023년_10월_31일 = LocalDate.of(2023, 10, 31);
        LocalDate 기준_2024년_12월_31일 = LocalDate.of(2024, 12, 31);

        int 데이터_1개 = 1;
        int 데이터_5개 = 5;

        // when
        LocalDate 결과_2023년_1월_1일 = StatisticQueryConditionConverter.convert(YEAR, 기준_2023년_10월_31일, 데이터_1개)
                .startDayInclude();
        LocalDate 결과_2019년_1월_1일 = StatisticQueryConditionConverter.convert(YEAR, 기준_2023년_10월_31일, 데이터_5개)
                .startDayInclude();

        LocalDate 결과_2024년_1월_1일 = StatisticQueryConditionConverter.convert(YEAR, 기준_2024년_12월_31일, 데이터_1개)
                .startDayInclude();
        LocalDate 결과_2020년_1월_1일 = StatisticQueryConditionConverter.convert(YEAR, 기준_2024년_12월_31일, 데이터_5개)
                .startDayInclude();

        // then
        LocalDate 예상_2023년_1월_1일 = LocalDate.of(2023, 1, 1);
        LocalDate 예상_2019년_1월_1일 = LocalDate.of(2019, 1, 1);
        LocalDate 예상_2024년_1월_1일 = LocalDate.of(2024, 1, 1);
        LocalDate 예상_2020년_1월_1일 = LocalDate.of(2020, 1, 1);

        assertThat(결과_2023년_1월_1일).isEqualTo(예상_2023년_1월_1일);
        assertThat(결과_2019년_1월_1일).isEqualTo(예상_2019년_1월_1일);
        assertThat(결과_2024년_1월_1일).isEqualTo(예상_2024년_1월_1일);
        assertThat(결과_2020년_1월_1일).isEqualTo(예상_2020년_1월_1일);
    }
}
