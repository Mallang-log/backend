package com.mallang.post.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.auth.MemberServiceTestHelper;
import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.ClickPostLikeCommand;
import com.mallang.post.domain.like.PostLikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("포스트 좋아요 이벤트 핸들러(PostLikeEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class PostLikeEventHandlerTest {

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private BlogServiceTestHelper blogServiceTestHelper;

    @Autowired
    private PostServiceTestHelper postServiceTestHelper;

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostLikeRepository postLikeRepository;

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
    class 포스트_삭제_시 {

        @Test
        void 좋아요도_삭제되어야_한다() {
            postLikeService.like(new ClickPostLikeCommand(postId, memberId, null));

            // when
            postServiceTestHelper.포스트를_삭제한다(memberId, postId);

            // then
            assertThat(postLikeRepository.findByPostIdAndMemberId(postId, memberId)).isEmpty();
        }
    }
}
