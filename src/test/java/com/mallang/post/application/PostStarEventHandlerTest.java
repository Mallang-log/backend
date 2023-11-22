package com.mallang.post.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.StarPostCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 즐겨찾기 이벤트 핸들러(PostStarEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostStarEventHandlerTest extends ServiceTest {

    private Long memberId;
    private String blogName;
    private Long postId;

    @BeforeEach
    void setUp() {
        memberId = memberServiceTestHelper.회원을_저장한다("말랑");
        blogName = blogServiceTestHelper.블로그_개설(memberId, "mallang-log").getName();
        postId = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트", "내용", "태그1");
    }

    @Nested
    class 포스트_삭제_이벤트를_받아 {

        @Test
        void 해당_포스트에_눌린_좋아요를_모두_제거한다() {
            postStarService.star(new StarPostCommand(postId, memberId, null));

            // when
            postServiceTestHelper.포스트를_삭제한다(memberId, postId);

            // then
            assertThat(postStarRepository.findByPostIdAndMemberId(postId, memberId)).isEmpty();
        }
    }
}
