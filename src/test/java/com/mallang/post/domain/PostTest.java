package com.mallang.post.domain;

import static com.mallang.member.MemberFixture.memberBuilder;
import static com.mallang.member.domain.OauthServerType.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.member.domain.Member;
import com.mallang.member.domain.OauthId;
import com.mallang.post.exception.NoAuthorityUpdatePost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트(Post) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostTest {

    private final Member writer = memberBuilder()
            .id(1L)
            .oauthId(new OauthId("1", GITHUB))
            .nickname("말랑")
            .profileImageUrl("https://mallang.com")
            .build();

    @Nested
    class 수정_시 {

        @Test
        void 수정에_성공한다() {
            // given
            Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .member(writer)
                    .build();

            // when
            post.update(writer.getId(), "수정제목", "수정내용");

            // then
            assertThat(post.getTitle()).isEqualTo("수정제목");
            assertThat(post.getContent()).isEqualTo("수정내용");
        }

        @Test
        void 작성자가_아니면_예외() {
            // given
            Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .member(writer)
                    .build();

            // when
            assertThatThrownBy(() ->
                    post.update(writer.getId() + 1, "수정제목", "수정내용")
            ).isInstanceOf(NoAuthorityUpdatePost.class);

            // then
            assertThat(post.getTitle()).isEqualTo("제목");
            assertThat(post.getContent()).isEqualTo("내용");
        }
    }
}
