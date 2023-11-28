package com.mallang.statistics.query.support;

import static com.mallang.statistics.query.support.PeriodType.DAY;
import static com.mallang.statistics.query.support.PeriodType.MONTH;
import static com.mallang.statistics.query.support.PeriodType.WEEK;
import static com.mallang.statistics.query.support.PeriodType.YEAR;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.statistics.query.StatisticCondition;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("날짜 변환기(StatisticConditionConverter) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StatisticConditionConverterTest {

    @Test
    void 조회타입이_일간인_경우_조회_마지막_날은_입력_그대로이다() {
        // given
        LocalDate 기준_2023년_11월_28일 = LocalDate.of(2023, 11, 28);

        // when
        StatisticCondition result = StatisticConditionConverter.convert(DAY, 기준_2023년_11월_28일, 100);

        // then
        assertThat(result.lastDayInclude()).isEqualTo(기준_2023년_11월_28일);
    }

    @Test
    void 조회타입이_일간인_경우_조회_시작일은_입력된_마지막일에서_조회_개수만큼의_요일을_뺀_요일이다() {
        // given
        LocalDate 기준_2023년_11월_28일 = LocalDate.of(2023, 11, 28);
        int 기준일로부터_1일_전 = 1;
        int 기준일로부터_10일_전 = 10;
        int 기준일로부터_30일_전 = 30;

        // when
        LocalDate 결과_2023년_11월_27일 = StatisticConditionConverter.convert(DAY, 기준_2023년_11월_28일, 기준일로부터_1일_전)
                .startDayInclude();
        LocalDate 결과_2023년_11월_18일 = StatisticConditionConverter.convert(DAY, 기준_2023년_11월_28일, 기준일로부터_10일_전)
                .startDayInclude();
        LocalDate 결과_2023년_10월_29일 = StatisticConditionConverter.convert(DAY, 기준_2023년_11월_28일, 기준일로부터_30일_전)
                .startDayInclude();

        // then
        LocalDate 예상_2023년_11월_27일 = LocalDate.of(2023, 11, 27);
        LocalDate 예상_2023년_11월_18일 = LocalDate.of(2023, 11, 18);
        LocalDate 예상_2023년_10월_29일 = LocalDate.of(2023, 10, 29);
        assertThat(결과_2023년_11월_27일).isEqualTo(예상_2023년_11월_27일);
        assertThat(결과_2023년_11월_18일).isEqualTo(예상_2023년_11월_18일);
        assertThat(결과_2023년_10월_29일).isEqualTo(예상_2023년_10월_29일);
    }

    @Test
    void 조회타입이_주간인_경우_조회_마지막_날은_이번주의_마지막_일요일이다() {
        // given
        LocalDate 기준_2023년_11월_27일_월 = LocalDate.of(2023, 11, 27);
        LocalDate 기준_2023년_11월_28일_화 = LocalDate.of(2023, 11, 28);
        LocalDate 기준_2023년_12월_2일_토 = LocalDate.of(2023, 12, 2);
        LocalDate 기준_2023년_12월_3일_일 = LocalDate.of(2023, 12, 3);

        // when
        StatisticCondition result1 = StatisticConditionConverter.convert(WEEK, 기준_2023년_11월_27일_월, 100);
        StatisticCondition result2 = StatisticConditionConverter.convert(WEEK, 기준_2023년_11월_28일_화, 100);
        StatisticCondition result3 = StatisticConditionConverter.convert(WEEK, 기준_2023년_12월_2일_토, 100);
        StatisticCondition result4 = StatisticConditionConverter.convert(WEEK, 기준_2023년_12월_3일_일, 100);

        // then
        assertThat(result1.lastDayInclude())
                .isEqualTo(result2.lastDayInclude())
                .isEqualTo(result3.lastDayInclude())
                .isEqualTo(result4.lastDayInclude())
                .isEqualTo(기준_2023년_12월_3일_일);
    }

    @Test
    void 조회타입이_주간인_경우_조회_시작일은_입력된_마지막일을_포함안_주의_시작일에서_조회_개수만큼의_전_주의_시작일이다() {
        // given
        LocalDate 기준_2023년_11월_26일_일 = LocalDate.of(2023, 11, 26);
        LocalDate 기준_2023년_11월_27일_월 = LocalDate.of(2023, 11, 27);
        LocalDate 기준_2023년_12월_1일_토 = LocalDate.of(2023, 12, 1);
        LocalDate 기준_2023년_12월_2일_일 = LocalDate.of(2023, 12, 2);

        int 기준일로부터_1주_전 = 1;
        int 기준일로부터_5주_전 = 5;

        // when
        LocalDate 결과_2023년_11월_13일_월 = StatisticConditionConverter.convert(WEEK, 기준_2023년_11월_26일_일, 기준일로부터_1주_전)
                .startDayInclude();
        LocalDate 결과_2023년_10월_16일_월 = StatisticConditionConverter.convert(WEEK, 기준_2023년_11월_26일_일, 기준일로부터_5주_전)
                .startDayInclude();

        LocalDate 결과_2023년_11월_20일_월1 = StatisticConditionConverter.convert(WEEK, 기준_2023년_11월_27일_월, 기준일로부터_1주_전)
                .startDayInclude();
        LocalDate 결과_2023년_10월_23일_월1 = StatisticConditionConverter.convert(WEEK, 기준_2023년_11월_27일_월, 기준일로부터_5주_전)
                .startDayInclude();

        LocalDate 결과_2023년_11월_20일_월2 = StatisticConditionConverter.convert(WEEK, 기준_2023년_12월_1일_토, 기준일로부터_1주_전)
                .startDayInclude();
        LocalDate 결과_2023년_10월_23일_월2 = StatisticConditionConverter.convert(WEEK, 기준_2023년_12월_1일_토, 기준일로부터_5주_전)
                .startDayInclude();

        LocalDate 결과_2023년_11월_20일_월3 = StatisticConditionConverter.convert(WEEK, 기준_2023년_12월_2일_일, 기준일로부터_1주_전)
                .startDayInclude();
        LocalDate 결과_2023년_10월_23일_월3 = StatisticConditionConverter.convert(WEEK, 기준_2023년_12월_2일_일, 기준일로부터_5주_전)
                .startDayInclude();

        // then
        LocalDate 예상_2023년_11월_13일 = LocalDate.of(2023, 11, 13);
        LocalDate 예상_2023년_10월_16일 = LocalDate.of(2023, 10, 16);

        LocalDate 예상_2023년_11월_20일 = LocalDate.of(2023, 11, 20);
        LocalDate 예상_2023년_10월_23일 = LocalDate.of(2023, 10, 23);
        assertThat(결과_2023년_11월_13일_월).isEqualTo(예상_2023년_11월_13일);
        assertThat(결과_2023년_10월_16일_월).isEqualTo(예상_2023년_10월_16일);
        assertThat(결과_2023년_11월_20일_월1)
                .isEqualTo(결과_2023년_11월_20일_월2)
                .isEqualTo(결과_2023년_11월_20일_월3)
                .isEqualTo(예상_2023년_11월_20일);
        assertThat(결과_2023년_10월_23일_월1)
                .isEqualTo(결과_2023년_10월_23일_월2)
                .isEqualTo(결과_2023년_10월_23일_월3)
                .isEqualTo(예상_2023년_10월_23일);
    }

    @Test
    void 조회타입이_월간인_경우_조회_마지막_날은_이번달의_마지막_요일이다() {
        // given
        LocalDate 기준_2023년_11월_1일 = LocalDate.of(2023, 11, 1);
        LocalDate 기준_2023년_11월_28일 = LocalDate.of(2023, 11, 28);
        LocalDate 기준_2023년_11월_30일 = LocalDate.of(2023, 11, 30);

        // when
        StatisticCondition result1 = StatisticConditionConverter.convert(MONTH, 기준_2023년_11월_1일, 100);
        StatisticCondition result2 = StatisticConditionConverter.convert(MONTH, 기준_2023년_11월_28일, 100);
        StatisticCondition result3 = StatisticConditionConverter.convert(MONTH, 기준_2023년_11월_30일, 100);

        // then
        assertThat(result1.lastDayInclude())
                .isEqualTo(result2.lastDayInclude())
                .isEqualTo(result3.lastDayInclude())
                .isEqualTo(기준_2023년_11월_30일);
    }

    @Test
    void 조회타입이_월간인_경우_조회_시작일은_입력된_마지막일을_포함한_달의_시작일에서_조회_개수만큼의_전_달의_시작일이다() {
        // given
        LocalDate 기준_2023년_10월_31일 = LocalDate.of(2023, 10, 31);
        LocalDate 기준_2023년_11월_26일 = LocalDate.of(2023, 11, 26);

        int 기준일로부터_1달_전 = 1;
        int 기준일로부터_4달_전 = 4;

        // when
        LocalDate 결과_2023년_9월_1일 = StatisticConditionConverter.convert(MONTH, 기준_2023년_10월_31일, 기준일로부터_1달_전)
                .startDayInclude();
        LocalDate 결과_2023년_6월_1일 = StatisticConditionConverter.convert(MONTH, 기준_2023년_10월_31일, 기준일로부터_4달_전)
                .startDayInclude();

        LocalDate 결과_2023년_10월_1일 = StatisticConditionConverter.convert(MONTH, 기준_2023년_11월_26일, 기준일로부터_1달_전)
                .startDayInclude();
        LocalDate 결과_2023년_7월_1일 = StatisticConditionConverter.convert(MONTH, 기준_2023년_11월_26일, 기준일로부터_4달_전)
                .startDayInclude();

        // then
        LocalDate 예상_2023년_6월_1일 = LocalDate.of(2023, 6, 1);
        LocalDate 예상_2023년_7월_1일 = LocalDate.of(2023, 7, 1);
        LocalDate 예상_2023년_9월_1일 = LocalDate.of(2023, 9, 1);
        LocalDate 예상_2023년_10월_1일 = LocalDate.of(2023, 10, 1);

        assertThat(결과_2023년_9월_1일).isEqualTo(예상_2023년_9월_1일);
        assertThat(결과_2023년_6월_1일).isEqualTo(예상_2023년_6월_1일);
        assertThat(결과_2023년_7월_1일).isEqualTo(예상_2023년_7월_1일);
        assertThat(결과_2023년_10월_1일).isEqualTo(예상_2023년_10월_1일);
    }

    @Test
    void 조회타입이_연간인_경우_조회_마지막_날은_이번년의_마지막_요일이다() {
        // given
        LocalDate 기준_2023년_1월_1일 = LocalDate.of(2023, 1, 1);
        LocalDate 기준_2023년_11월_28일 = LocalDate.of(2023, 11, 28);
        LocalDate 기준_2023년_12월_31일 = LocalDate.of(2023, 12, 31);

        // when
        StatisticCondition result1 = StatisticConditionConverter.convert(YEAR, 기준_2023년_1월_1일, 100);
        StatisticCondition result2 = StatisticConditionConverter.convert(YEAR, 기준_2023년_11월_28일, 100);
        StatisticCondition result3 = StatisticConditionConverter.convert(YEAR, 기준_2023년_12월_31일, 100);

        // then
        assertThat(result1.lastDayInclude())
                .isEqualTo(result2.lastDayInclude())
                .isEqualTo(result3.lastDayInclude())
                .isEqualTo(기준_2023년_12월_31일);
    }

    @Test
    void 조회타입이_연간인_경우_조회_시작일은_입력된_마지막일을_포함한_년의_시작일에서_조회_개수만큼의_전_년의_시작일이다() {
        // given
        LocalDate 기준_2023년_10월_31일 = LocalDate.of(2023, 10, 31);
        LocalDate 기준_2024년_12월_31일 = LocalDate.of(2024, 12, 31);

        int 기준일로부터_1년_전 = 1;
        int 기준일로부터_5년_전 = 5;

        // when
        LocalDate 결과_2022년_1월_1일 = StatisticConditionConverter.convert(YEAR, 기준_2023년_10월_31일, 기준일로부터_1년_전)
                .startDayInclude();
        LocalDate 결과_2018년_1월_1일 = StatisticConditionConverter.convert(YEAR, 기준_2023년_10월_31일, 기준일로부터_5년_전)
                .startDayInclude();

        LocalDate 결과_2023년_1월_1일 = StatisticConditionConverter.convert(YEAR, 기준_2024년_12월_31일, 기준일로부터_1년_전)
                .startDayInclude();
        LocalDate 결과_2019년_1월_1일 = StatisticConditionConverter.convert(YEAR, 기준_2024년_12월_31일, 기준일로부터_5년_전)
                .startDayInclude();

        // then
        LocalDate 예상_2022년_1월_1일 = LocalDate.of(2022, 1, 1);
        LocalDate 예상_2018년_1월_1일 = LocalDate.of(2018, 1, 1);
        LocalDate 예상_2023년_1월_1일 = LocalDate.of(2023, 1, 1);
        LocalDate 예상_2019년_1월_1일 = LocalDate.of(2019, 1, 1);

        assertThat(결과_2022년_1월_1일).isEqualTo(예상_2022년_1월_1일);
        assertThat(결과_2018년_1월_1일).isEqualTo(예상_2018년_1월_1일);
        assertThat(결과_2023년_1월_1일).isEqualTo(예상_2023년_1월_1일);
        assertThat(결과_2019년_1월_1일).isEqualTo(예상_2019년_1월_1일);
    }
}
