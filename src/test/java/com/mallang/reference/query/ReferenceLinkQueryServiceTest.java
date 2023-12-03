package com.mallang.reference.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.common.ServiceTest;
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
    private String blogName;


    @BeforeEach
    void setUp() {
        memberId = 회원을_저장한다("말랑");
        blogName = 블로그_개설(memberId, "mallang-log");
    }

    @Nested
    class 검색_시 {

        private Long 말랑이_블로그_링크_ID;
        private Long Spring_글_참고_링크_ID;

        @BeforeEach
        void setUp() {
            말랑이_블로그_링크_ID = referenceLinkService.save(new SaveReferenceLinkCommand(
                    memberId,
                    blogName,
                    "https://ttl-blog.tistory.com",
                    "말랑이 블로그",
                    "말랑이 블로그 메인 페이지이다."
            ));
            Spring_글_참고_링크_ID = referenceLinkService.save(new SaveReferenceLinkCommand(
                    memberId,
                    blogName,
                    "https://ttl-blog.tistory.com/123",
                    "스프링이란?",
                    "말랑이가 쓴 스프링에 대한 내용."
            ));
        }

        @Test
        void 아무_조건이_없다면_블로그에_등록된_모든_링크를_조회한다() {
            // given
            ReferenceLinkSearchDaoCond emptyCond = new ReferenceLinkSearchDaoCond(null, null, null);

            // when
            List<ReferenceLinkSearchResponse> result = referenceLinkQueryService.search(memberId, blogName, emptyCond);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        void 다른_사람_글은_보이지_않는다() {
            // given
            Long otherMemberId = 회원을_저장한다("other");
            String otherBlogName = 블로그_개설(otherMemberId, "other-log");
            Long 타인의_Spring_글_참고_링크_ID = referenceLinkService.save(new SaveReferenceLinkCommand(
                    otherMemberId,
                    otherBlogName,
                    "https://ttl-blog.tistory.com/123",
                    "스프링이란?",
                    "누군가 쓴 스프링에 대한 내용."
            ));
            ReferenceLinkSearchDaoCond emptyCond = new ReferenceLinkSearchDaoCond(null, null, null);

            // when
            List<ReferenceLinkSearchResponse> result = referenceLinkQueryService.search(memberId, blogName, emptyCond);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        void 블로그의_주인이_아닌_사람이_조회시_예외() {
            // given
            Long otherMemberId = 회원을_저장한다("other");
            ReferenceLinkSearchDaoCond emptyCond = new ReferenceLinkSearchDaoCond(null, null, null);

            // when & then
            assertThatThrownBy(() -> {
                referenceLinkQueryService.search(otherMemberId, blogName, emptyCond);
            }).isInstanceOf(NoAuthorityBlogException.class);
        }

        @Test
        void url_포함조건으로_검색할_수_있다() {
            // given
            ReferenceLinkSearchDaoCond urlCond = new ReferenceLinkSearchDaoCond("12", null, null);

            // when
            List<ReferenceLinkSearchResponse> result = referenceLinkQueryService.search(memberId, blogName, urlCond);

            // then
            assertThat(result)
                    .extracting(ReferenceLinkSearchResponse::referenceLinkId)
                    .containsExactly(Spring_글_참고_링크_ID);
        }

        @Test
        void 제목_포함조건으로_검색할_수_있다() {
            // given
            ReferenceLinkSearchDaoCond titleCond = new ReferenceLinkSearchDaoCond(null, "랑이", null);

            // when
            List<ReferenceLinkSearchResponse> result = referenceLinkQueryService.search(memberId, blogName, titleCond);

            // then
            assertThat(result)
                    .extracting(ReferenceLinkSearchResponse::referenceLinkId)
                    .containsExactly(말랑이_블로그_링크_ID);
        }

        @Test
        void 메모_포함조건으로_검색할_수_있다() {
            // given
            ReferenceLinkSearchDaoCond memoCond = new ReferenceLinkSearchDaoCond(null, null, "스프링에");

            // when
            List<ReferenceLinkSearchResponse> result = referenceLinkQueryService.search(memberId, blogName, memoCond);

            // then
            assertThat(result)
                    .extracting(ReferenceLinkSearchResponse::referenceLinkId)
                    .containsExactly(Spring_글_참고_링크_ID);
        }
    }
}
