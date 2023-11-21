package com.mallang.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.auth.MemberServiceTestHelper;
import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CancelPostLikeCommand;
import com.mallang.post.application.command.ClickPostLikeCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.like.PostLikeRepository;
import com.mallang.post.exception.AlreadyLikedPostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("포스트 좋아요 서비스(PostLikeService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class PostLikeServiceTest {

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private BlogServiceTestHelper blogServiceTestHelper;

    @Autowired
    private PostServiceTestHelper postServiceTestHelper;

    @Autowired
    private PostRepository postRepository;

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
    class 좋아요_시 {

        @Test
        void 회원이_이미_해당_포스트에_좋아요를_누른_경우_예외() {
            // given
            postLikeService.click(new ClickPostLikeCommand(postId, memberId, null));
            ClickPostLikeCommand command = new ClickPostLikeCommand(postId, memberId, null);

            // when
            assertThatThrownBy(() -> {
                postLikeService.click(command);
            }).isInstanceOf(AlreadyLikedPostException.class);

            // then
            Post post = postRepository.getById(postId);
            assertThat(post.getLikeCount()).isEqualTo(1);
        }

        @Test
        void 해당_포스트에_좋아요를_누른_적이_없으면_좋아요를_누른다() {
            // when
            postLikeService.click(new ClickPostLikeCommand(postId, memberId, null));

            // then
            Post post = postRepository.getById(postId);
            assertThat(post.getLikeCount()).isEqualTo(1);
        }
    }

    @Test
    void 좋아요_취소_시_좋아요가_제거된다() {
        postLikeService.click(new ClickPostLikeCommand(postId, memberId, null));

        // when
        postLikeService.cancel(new CancelPostLikeCommand(postId, memberId, null));
        // then
        Post post = postRepository.getById(postId);
        assertThat(post.getLikeCount()).isZero();
        assertThat(postLikeRepository.findAll()).isEmpty();
    }

    @Nested
    class 포스트_삭제_시 {

        @Test
        void 좋아요도_삭제되어야_한다() {
            postLikeService.click(new ClickPostLikeCommand(postId, memberId, null));

            // when
            postServiceTestHelper.포스트를_삭제한다(memberId, postId);

            // then
            assertThat(postLikeRepository.findByPostIdAndMemberId(postId, memberId)).isEmpty();
        }
    }
}
