package com.mallang.post.query;

import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.StarPostCommand;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.query.response.StaredPostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

@DisplayName("포스트 즐겨찾기 조회 서비스(PostStarQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostStarQueryServiceTest extends ServiceTest {

    private Long memberId;
    private Long otherMemberId;
    private String blogName;
    private Long post1Id;
    private Long post2Id;
    private Long post3Id;

    @BeforeEach
    void setUp() {
        memberId = 회원을_저장한다("말랑");
        otherMemberId = 회원을_저장한다("other");
        blogName = 블로그_개설(memberId, "mallang").getName();
        post1Id = 포스트를_저장한다(
                memberId,
                blogName,
                "포스트1",
                "내용1",
                new PostVisibilityPolicy(PUBLIC, null));
        post2Id = 포스트를_저장한다(
                memberId,
                blogName,
                "포스트2",
                "내용2",
                new PostVisibilityPolicy(PUBLIC, null));
        post3Id = 포스트를_저장한다(
                memberId,
                blogName,
                "포스트3",
                "내용3",
                new PostVisibilityPolicy(PUBLIC, null));
    }

    @Nested
    class 특정_회원의_즐겨찾기_목록_조회_시 {

        @Test
        void 누구나_볼_수_있다() {
            // given
            postStarService.star(new StarPostCommand(post1Id, otherMemberId, null));
            postStarService.star(new StarPostCommand(post2Id, otherMemberId, null));
            postStarService.star(new StarPostCommand(post3Id, otherMemberId, null));

            // when
            Page<StaredPostResponse> result = postStarQueryService.findAllByMemberId(otherMemberId, null, pageable);

            // then
            assertThat(result)
                    .extracting(StaredPostResponse::title)
                    .containsExactly("포스트3", "포스트2", "포스트1");
        }

        @Test
        void 보호_글은_보호되어_조회된다() {
            // given
            postStarService.star(new StarPostCommand(post1Id, otherMemberId, null));
            postStarService.star(new StarPostCommand(post2Id, otherMemberId, null));
            postStarService.star(new StarPostCommand(post3Id, otherMemberId, null));

            포스트_공개여부를_업데이트한다(memberId, post1Id, PROTECTED, "1234");

            // when
            Page<StaredPostResponse> result = postStarQueryService.findAllByMemberId(otherMemberId, otherMemberId,
                    pageable);

            // then
            assertThat(result.getContent())
                    .extracting(StaredPostResponse::content)
                    .containsExactly("내용3", "내용2", "보호되어 있는 글입니다.");
        }

        @Test
        void 글_작성자가_다른_회원의_즐겨찾기_목록_조회_시_글_작성자의_보호글이_즐겨찾이_되어있다면_볼_수_있다() {
            // given
            postStarService.star(new StarPostCommand(post1Id, otherMemberId, null));
            postStarService.star(new StarPostCommand(post2Id, otherMemberId, null));
            postStarService.star(new StarPostCommand(post3Id, otherMemberId, null));

            포스트_공개여부를_업데이트한다(memberId, post1Id, PROTECTED, "1234");

            // when
            Page<StaredPostResponse> result = postStarQueryService.findAllByMemberId(otherMemberId, memberId, pageable);

            // then
            assertThat(result.getContent())
                    .extracting(StaredPostResponse::content)
                    .containsExactly("내용3", "내용2", "내용1");
        }

        @Test
        void 비공개_글은_누가_조회하든_조회되지_않는다() {
            // given
            postStarService.star(new StarPostCommand(post1Id, otherMemberId, null));
            postStarService.star(new StarPostCommand(post2Id, otherMemberId, null));
            postStarService.star(new StarPostCommand(post3Id, otherMemberId, null));

            포스트_공개여부를_업데이트한다(memberId, post1Id, PRIVATE, null);

            // when
            Page<StaredPostResponse> result = postStarQueryService.findAllByMemberId(otherMemberId, memberId, pageable);

            // then
            assertThat(result.getContent())
                    .extracting(StaredPostResponse::title)
                    .containsExactly("포스트3", "포스트2");
        }
    }
}
