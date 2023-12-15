package com.mallang.post.domain.star;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostId;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.exception.NoAuthorityStarGroupException;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("포스트 즐겨찾기 (PostStar) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostStarTest {

    private final Member mallang = 깃허브_말랑(1L);
    private final Blog mallangBlog = mallangBlog(1L, mallang);
    private final Member donghun = 깃허브_동훈(2L);
    private final Post mallangPublicPost = new Post(
            new PostId(1L, mallangBlog.getId()),
            mallangBlog,
            PUBLIC,
            null,
            "제목",
            "intro",
            "content",
            null,
            null,
            Collections.emptyList(),
            mallang
    );
    private final Post mallangProtectedPost = new Post(
            new PostId(1L, mallangBlog.getId()),
            mallangBlog,
            PROTECTED,
            "1234",
            "제목",
            "intro",
            "content",
            null,
            null,
            Collections.emptyList(),
            mallang
    );
    private final Post mallangPrivatePost = new Post(
            new PostId(1L, mallangBlog.getId()),
            mallangBlog,
            PRIVATE,
            null,
            "제목",
            "intro",
            "content",
            null,
            null,
            Collections.emptyList(),
            mallang
    );

    @Nested
    class 즐겨찾기_시 {

        @ParameterizedTest
        @CsvSource(
                value = {
                        "PUBLIC, null",
                        "PROTECTED, 1234",
                        "PRIVATE, null",
                },
                delimiterString = ", ",
                nullValues = {"null"}
        )
        void 내_글은_공개여부에_관계없이_즐겨찾기_된다(Visibility visibility, String password) {
            // given
            Post post = new Post(
                    new PostId(1L, mallangBlog.getId()),
                    mallangBlog,
                    visibility,
                    password,
                    "제목",
                    "intro",
                    "content",
                    null,
                    null,
                    Collections.emptyList(),
                    mallang
            );
            PostStar postStar = new PostStar(post, mallang);

            // when & then
            assertDoesNotThrow(() -> {
                postStar.star(null);
            });
        }

        @Test
        void 타인의_보호_글_즐겨찾기_시_비밀번호가_일치하면_할_수_있다() {
            // given
            PostStar postStar = new PostStar(mallangProtectedPost, donghun);

            // when & then
            assertDoesNotThrow(() -> {
                postStar.star("1234");
            });
        }

        @Test
        void 타인의_보호_글_즐겨찾기_시_글의_비밀번호가_일치하지_않으면_예외() {
            // given
            PostStar postStar = new PostStar(mallangProtectedPost, donghun);

            // when & then
            assertThatThrownBy(() -> {
                postStar.star("wrong");
            }).isInstanceOf(NoAuthorityPostException.class);
        }

        @Test
        void 타인의_비밀_글은_즐겨찾기_할_수_없다() {
            // given
            PostStar postStar = new PostStar(mallangPrivatePost, donghun);

            // when & then
            assertThatThrownBy(() -> {
                postStar.star(null);
            }).isInstanceOf(NoAuthorityPostException.class);
        }
    }

    @Nested
    class 즐겨찾기_그룹_변경_시 {

        @Test
        void 그룹을_변경한다() {
            // given
            StarGroup mallangStarGroup = new StarGroup("Spring", mallang);
            PostStar postStar = new PostStar(mallangPublicPost, mallang);

            // when
            postStar.updateGroup(mallangStarGroup);

            // then
            assertThat(postStar.getStarGroup()).isEqualTo(mallangStarGroup);
        }

        @Test
        void 그룹을_지정하지_않으면_그룹_없음_상태가_된다() {
            // given
            StarGroup mallangStarGroup = new StarGroup("Spring", mallang);
            PostStar postStar = new PostStar(mallangPublicPost, mallang);
            postStar.updateGroup(mallangStarGroup);

            // when
            postStar.updateGroup(null);

            // then
            assertThat(postStar.getStarGroup()).isNull();
        }

        @Test
        void 타인의_그룹_지정_시_예외() {
            // given
            StarGroup donghunStarGroup = new StarGroup("Spring", donghun);
            PostStar postStar = new PostStar(mallangPublicPost, mallang);

            // when & then
            assertThatThrownBy(() -> {
                postStar.updateGroup(donghunStarGroup);
            }).isInstanceOf(NoAuthorityStarGroupException.class);
        }
    }
}
