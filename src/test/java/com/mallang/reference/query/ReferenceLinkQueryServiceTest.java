package com.mallang.reference.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.ServiceTest;
import com.mallang.reference.application.command.CreateLabelCommand;
import com.mallang.reference.application.command.SaveReferenceLinkCommand;
import com.mallang.reference.query.repository.ReferenceLinkSearchDao.ReferenceLinkSearchDaoCond;
import com.mallang.reference.query.response.ReferenceLinkSearchResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("참고 링크 조회 서비스 (ReferenceLinkQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ReferenceLinkQueryServiceTest extends ServiceTest {

    private Long memberId;

    @BeforeEach
    void setUp() {
        memberId = 회원을_저장한다("말랑");
    }

    @Nested
    class 주어진_Url로_등록된_링크가_이미_존재하는지_확인_시 {

        @Test
        void Url이_정확히_일치해야_일치하는_것이다() {
            // given
            referenceLinkService.save(new SaveReferenceLinkCommand(
                    memberId,
                    "https://ttl-blog.tistory.com",
                    "말랑이 블로그",
                    "말랑이 블로그 메인 페이지이다.",
                    null
            ));

            // when
            boolean exactlyMatch = referenceLinkQueryService.existsReferenceLinkByUrl(
                    memberId,
                    "https://ttl-blog.tistory.com"
            );
            boolean notMatch1 = referenceLinkQueryService.existsReferenceLinkByUrl(
                    memberId,
                    "https://ttl-blog.tistory.com/"
            );
            boolean notMatch2 = referenceLinkQueryService.existsReferenceLinkByUrl(
                    memberId,
                    "ttl-blog.tistory.com"
            );

            // then
            assertThat(exactlyMatch).isTrue();
            assertThat(notMatch1).isFalse();
            assertThat(notMatch2).isFalse();
        }

        @Test
        void 다른_회원이_등록한것과는_무관하다() {
            // given
            Long otherMemberId = 회원을_저장한다("other");
            referenceLinkService.save(new SaveReferenceLinkCommand(
                    otherMemberId,
                    "https://ttl-blog.tistory.com/123",
                    "스프링이란?",
                    "누군가 쓴 스프링에 대한 내용.",
                    null
            ));

            // when
            boolean notExist = referenceLinkQueryService.existsReferenceLinkByUrl(
                    memberId,
                    "https://ttl-blog.tistory.com/123"
            );
            boolean exist = referenceLinkQueryService.existsReferenceLinkByUrl(
                    otherMemberId,
                    "https://ttl-blog.tistory.com/123"
            );

            // then
            assertThat(notExist).isFalse();
            assertThat(exist).isTrue();
        }
    }

    @Nested
    class 검색_시 {

        private Long 말랑이_블로그_링크_ID;
        private Long Spring_글_참고_링크_ID;

        @BeforeEach
        void setUp() {
            말랑이_블로그_링크_ID = referenceLinkService.save(new SaveReferenceLinkCommand(
                    memberId,
                    "https://ttl-blog.tistory.com",
                    "말랑이 블로그",
                    "말랑이 블로그 메인 페이지이다.",
                    null
            ));
            Spring_글_참고_링크_ID = referenceLinkService.save(new SaveReferenceLinkCommand(
                    memberId,
                    "https://ttl-blog.tistory.com/123",
                    "스프링이란?",
                    "말랑이가 쓴 스프링에 대한 내용.",
                    null
            ));
        }

        @Test
        void 아무_조건이_없다면_내가_등록한_모든_링크를_조회한다() {
            // given
            ReferenceLinkSearchDaoCond emptyCond = new ReferenceLinkSearchDaoCond(null, null, null, null);

            // when
            List<ReferenceLinkSearchResponse> result = referenceLinkQueryService.search(memberId, emptyCond);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        void 다른_사람_링크는_보이지_않는다() {
            // given
            Long otherMemberId = 회원을_저장한다("other");
            Long 타인의_Spring_글_참고_링크_ID = referenceLinkService.save(new SaveReferenceLinkCommand(
                    otherMemberId,
                    "https://ttl-blog.tistory.com/123",
                    "스프링이란?",
                    "누군가 쓴 스프링에 대한 내용.",
                    null
            ));
            ReferenceLinkSearchDaoCond emptyCond = new ReferenceLinkSearchDaoCond(null, null, null, null);

            // when
            List<ReferenceLinkSearchResponse> result = referenceLinkQueryService.search(memberId, emptyCond);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        void url_포함조건으로_검색할_수_있다() {
            // given
            ReferenceLinkSearchDaoCond urlCond = new ReferenceLinkSearchDaoCond("12", null, null, null);

            // when
            List<ReferenceLinkSearchResponse> result = referenceLinkQueryService.search(memberId, urlCond);

            // then
            assertThat(result)
                    .extracting(ReferenceLinkSearchResponse::referenceLinkId)
                    .containsExactly(Spring_글_참고_링크_ID);
        }

        @Test
        void 제목_포함조건으로_검색할_수_있다() {
            // given
            ReferenceLinkSearchDaoCond titleCond = new ReferenceLinkSearchDaoCond(null, "랑이", null, null);

            // when
            List<ReferenceLinkSearchResponse> result = referenceLinkQueryService.search(memberId, titleCond);

            // then
            assertThat(result)
                    .extracting(ReferenceLinkSearchResponse::referenceLinkId)
                    .containsExactly(말랑이_블로그_링크_ID);
        }

        @Test
        void 메모_포함조건으로_검색할_수_있다() {
            // given
            ReferenceLinkSearchDaoCond memoCond = new ReferenceLinkSearchDaoCond(null, null, "스프링에", null);

            // when
            List<ReferenceLinkSearchResponse> result = referenceLinkQueryService.search(memberId, memoCond);

            // then
            assertThat(result)
                    .extracting(ReferenceLinkSearchResponse::referenceLinkId)
                    .containsExactly(Spring_글_참고_링크_ID);
        }

        @Test
        void 특정_라벨에_속한_링크들을_검색할_수_있다() {
            // given
            var 라벨1_ID = labelService.create(new CreateLabelCommand(memberId, "label", "#000000", null, null));
            var 라벨2_ID = labelService.create(new CreateLabelCommand(memberId, "label2", "#000000", 라벨1_ID, null));
            var 라벨1_붙은_링크 = referenceLinkService.save(new SaveReferenceLinkCommand(
                    memberId,
                    "https://ttl-blog.tistory.com",
                    "말랑이 블로그",
                    "말랑이 블로그 메인 페이지이다.",
                    라벨1_ID
            ));
            var 라벨2_붙은_링크 = referenceLinkService.save(new SaveReferenceLinkCommand(
                    memberId,
                    "https://ttl-blog.tistory.com",
                    "말랑이 블로그",
                    "말랑이 블로그 메인 페이지이다.",
                    라벨2_ID
            ));
            ReferenceLinkSearchDaoCond 라벨1_조회 = new ReferenceLinkSearchDaoCond(null, null, null, 라벨1_ID);
            ReferenceLinkSearchDaoCond 라벨2_조회 = new ReferenceLinkSearchDaoCond(null, null, null, 라벨2_ID);

            // when
            List<ReferenceLinkSearchResponse> result1 = referenceLinkQueryService.search(memberId, 라벨1_조회);
            List<ReferenceLinkSearchResponse> result2 = referenceLinkQueryService.search(memberId, 라벨2_조회);

            // then
            assertThat(result1)
                    .extracting(ReferenceLinkSearchResponse::referenceLinkId)
                    .containsExactly(라벨1_붙은_링크);
            assertThat(result2)
                    .extracting(ReferenceLinkSearchResponse::referenceLinkId)
                    .containsExactly(라벨2_붙은_링크);

        }
    }
}
