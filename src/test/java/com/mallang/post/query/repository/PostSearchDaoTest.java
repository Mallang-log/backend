package com.mallang.post.query.repository;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.blog;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.common.RepositoryTest;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostCategory;
import com.mallang.post.domain.PostCategoryRepository;
import com.mallang.post.domain.PostId;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.exception.BadPostSearchCondException;
import com.mallang.post.query.repository.PostSearchDao.PostSearchCond;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("포스트 검색 DAO (PostSearchDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@RepositoryTest
class PostSearchDaoTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCategoryRepository postCategoryRepository;

    @Autowired
    private PostQueryRepository postSearchDao;

    private final Member mallang = 깃허브_말랑(1L);
    private final Member donghun = 깃허브_동훈(2L);
    private final Blog mallangBlog = mallangBlog(1L, mallang);
    private final Blog donghunBlog = blog(2L, "donghun-blog", donghun);
    private final Pageable pageable = PageRequest.of(0, 10);
    private PostCategory 스프링_카테고리;
    private PostCategory JPA_카테고리;
    private Post publicPost;
    private Post protectedPost;
    private Post privatePost;

    @BeforeEach
    void setUp() {
        memberRepository.save(mallang);
        memberRepository.save(donghun);
        blogRepository.save(mallangBlog);
        blogRepository.save(donghunBlog);
        스프링_카테고리 = postCategoryRepository.save(new PostCategory("스프링", mallang, mallangBlog));
        PostCategory jpa = new PostCategory("JPA", mallang, mallangBlog);
        jpa.updateHierarchy(스프링_카테고리, null, null);
        JPA_카테고리 = postCategoryRepository.save(jpa);
        publicPost = new Post(
                new PostId(1L, mallangBlog.getId()),
                mallangBlog,
                PUBLIC,
                null,
                "mallang-public",
                "mallang-public",
                "mallang-public",
                null,
                JPA_카테고리,
                Collections.emptyList(),
                mallang
        );
        protectedPost = new Post(
                new PostId(2L, mallangBlog.getId()),
                mallangBlog,
                PROTECTED,
                "1234",
                "mallang-protected",
                "mallang-protected",
                "mallang-protected",
                null,
                스프링_카테고리,
                Collections.emptyList(),
                mallang
        );
        privatePost = new Post(
                new PostId(3L, mallangBlog.getId()),
                mallangBlog,
                PRIVATE,
                null,
                "mallang-private",
                "mallang-private",
                "mallang-private",
                null,
                null,
                Collections.emptyList(),
                mallang
        );
        postRepository.save(publicPost);
        postRepository.save(protectedPost);
        postRepository.save(privatePost);
    }

    @Test
    void 비공개_포스트의_주인이_아닌_경우_비공개_포스트를_제외하고_전체_조회한다() {
        // given
        PostSearchCond cond = PostSearchCond.builder().build();

        // when
        List<Post> responses = postSearchDao.search(null, cond, pageable).getContent();

        // then
        assertThat(responses).hasSize(2)
                .containsExactly(protectedPost, publicPost);
    }

    @Test
    void 비공개_포스트인_경우_주인에게만_조회된다() {
        // given
        PostSearchCond cond = PostSearchCond.builder().build();

        // when
        List<Post> responses = postSearchDao.search(mallang.getId(), cond, pageable).getContent();

        // then
        assertThat(responses).hasSize(3)
                .containsExactly(privatePost, protectedPost, publicPost);
    }

    @Test
    void 특정_카테고리의_포스트만_조회한다() {
        // given
        PostSearchCond cond = PostSearchCond.builder()
                .categoryId(JPA_카테고리.getId())
                .blogName(mallangBlog.getName())
                .build();

        // when
        List<Post> responses = postSearchDao.search(null, cond, pageable).getContent();

        // then
        assertThat(responses).hasSize(1)
                .containsExactly(publicPost);
    }

    @Test
    void 상위_카테고리로_조회_시_하위_카테고리도_포함되면_조회한다() {
        // given
        PostSearchCond cond = PostSearchCond.builder()
                .categoryId(스프링_카테고리.getId())
                .blogName(mallangBlog.getName())
                .build();

        // when
        List<Post> responses = postSearchDao.search(null, cond, pageable).getContent();

        // then
        assertThat(responses).hasSize(2)
                .containsExactly(protectedPost, publicPost);
        ;
    }

    @Test
    void 특정_태그의_포스트만_조회한다() {
        // given
        Post post = new Post(
                new PostId(100L, mallangBlog.getId()),
                mallangBlog,
                PUBLIC,
                null,
                "title",
                "intro",
                "text",
                null,
                null,
                List.of("tag1", "tag2"),
                mallang
        );
        postRepository.save(post);
        PostSearchCond cond = PostSearchCond.builder()
                .tag("tag2")
                .build();

        // when
        List<Post> responses = postSearchDao.search(null, cond, pageable).getContent();

        // then
        assertThat(responses).hasSize(1)
                .containsExactly(post);
    }

    @Test
    void 특정_작성자의_포스트만_조회한다() {
        // given
        Post post = new Post(
                new PostId(100L, donghunBlog.getId()),
                donghunBlog,
                PUBLIC,
                null,
                "title",
                "intro",
                "text",
                null,
                null,
                List.of("tag1", "tag2"),
                donghun
        );
        postRepository.save(post);
        PostSearchCond cond = PostSearchCond.builder()
                .writerId(mallang.getId())
                .build();

        // when
        List<Post> responses = postSearchDao.search(null, cond, pageable)
                .getContent();

        // then
        assertThat(responses)
                .containsExactly(protectedPost, publicPost);
    }

    @Test
    void 특정_블로그의_포스트만_조회한다() {
        // given
        Post post = new Post(
                new PostId(100L, donghunBlog.getId()),
                donghunBlog,
                PUBLIC,
                null,
                "title",
                "intro",
                "text",
                null,
                null,
                List.of("tag1", "tag2"),
                donghun
        );
        postRepository.save(post);
        PostSearchCond cond = PostSearchCond.builder()
                .blogName(mallangBlog.getName())
                .build();

        // when
        List<Post> responses = postSearchDao.search(mallang.getId(), cond, pageable)
                .getContent();

        // then
        assertThat(responses)
                .containsExactly(privatePost, protectedPost, publicPost);
    }

    @Test
    void 제목으로_조회() {
        // given
        PostSearchCond cond = PostSearchCond.builder()
                .title("public")
                .build();

        // when
        List<Post> responses = postSearchDao.search(null, cond, pageable).getContent();

        // then
        assertThat(responses)
                .containsExactly(publicPost);
    }

    @Test
    void 내용으로_조회() {
        // given
        PostSearchCond cond = PostSearchCond.builder()
                .bodyText("protected")
                .build();

        // when
        List<Post> responses = postSearchDao.search(null, cond, pageable).getContent();

        // then
        assertThat(responses)
                .containsExactly(protectedPost);
    }

    @DisplayName("내용 + 제목으로 조회")
    @Test
    void 내용_and_제목으로_조회() {
        // given
        PostSearchCond cond = PostSearchCond.builder()
                .titleOrBodyText("protected")
                .build();

        // when
        List<Post> responses = postSearchDao.search(null, cond, pageable).getContent();

        // then
        assertThat(responses)
                .containsExactly(protectedPost);
    }

    @DisplayName("제목이나 내용이 있는데 제목 + 내용도 있다면 예외")
    @Test
    void 제목이나_내용이_있는데_제목_and_내용도_있다면_예외() {
        // given
        PostSearchCond cond = PostSearchCond.builder()
                .title("1")
                .titleOrBodyText("안녕")
                .build();

        // when & then
        assertThatThrownBy(() ->
                postSearchDao.search(null, cond, pageable)
        ).isInstanceOf(BadPostSearchCondException.class);
    }
}
