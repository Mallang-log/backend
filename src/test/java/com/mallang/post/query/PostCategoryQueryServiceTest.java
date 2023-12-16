package com.mallang.post.query;

import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static com.mallang.post.PostCategoryFixture.postCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.PostCategory;
import com.mallang.post.query.repository.PostCategoryQueryRepository;
import com.mallang.post.query.response.PostCategoryResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 카테고리 조회 서비스 (PostCategoryQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostCategoryQueryServiceTest {

    private final PostCategoryQueryRepository postCategoryQueryRepository = mock(PostCategoryQueryRepository.class);
    private final PostCategoryQueryService postCategoryQueryService = new PostCategoryQueryService(
            postCategoryQueryRepository
    );

    @Test
    void 카테고리_목록이_없는_경우_빈_리스트_반환() {
        // given
        given(postCategoryQueryRepository.findAllByBlogName("blog-name"))
                .willReturn(List.of());

        // when
        List<PostCategoryResponse> result = postCategoryQueryService.findAllByBlogName("blog-name");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 특정_블로그의_카테고리를_순서대로_전체_조회한다() {
        // given
        Member member = 깃허브_말랑(1L);
        Blog blog = mallangBlog(1L, member);
        Long springId = 2L;
        Long jpaId = 4L;
        Long n1Id = 1L;
        Long securityId = 3L;
        Long oAuthId = 7L;
        Long csrfId = 8L;
        Long algorithmId = 10L;
        Long dfsId = 5L;
        PostCategory spring = postCategory(springId, "Spring", blog);
        PostCategory jpa = postCategory(jpaId, "JPA", blog);
        PostCategory n1 = postCategory(n1Id, "N + 1", blog);
        PostCategory security = postCategory(securityId, "Security", blog);
        PostCategory oauth = postCategory(oAuthId, "OAuth", blog);
        PostCategory csrf = postCategory(csrfId, "CSRF", blog);
        PostCategory algorithm = postCategory(algorithmId, "Algorithm", blog);
        PostCategory dfs = postCategory(dfsId, "DFS", blog);
        dfs.updateHierarchy(algorithm, null, null);
        spring.updateHierarchy(null, algorithm, null);
        n1.updateHierarchy(jpa, null, null);
        csrf.updateHierarchy(security, null, null);
        oauth.updateHierarchy(security, csrf, null);
        jpa.updateHierarchy(spring, null, null);
        security.updateHierarchy(spring, jpa, null);
        given(postCategoryQueryRepository.findAllByBlogName(blog.getName()))
                .willReturn(List.of(
                        spring,
                        jpa,
                        n1,
                        security,
                        oauth,
                        csrf,
                        algorithm,
                        dfs
                ));

        // when
        List<PostCategoryResponse> allByMemberId = postCategoryQueryService.findAllByBlogName(blog.getName());

        // then
        List<PostCategoryResponse> expected = List.of(
                new PostCategoryResponse(
                        algorithmId,
                        "Algorithm",
                        null,
                        null,
                        springId,
                        List.of(
                                new PostCategoryResponse(
                                        dfsId,
                                        "DFS",
                                        algorithmId,
                                        null,
                                        null,
                                        List.of()
                                )
                        )),
                new PostCategoryResponse(
                        springId,
                        "Spring",
                        null,
                        algorithmId,
                        null,
                        List.of(
                                new PostCategoryResponse(
                                        jpaId,
                                        "JPA",
                                        springId,
                                        null,
                                        securityId,
                                        List.of(
                                                new PostCategoryResponse(
                                                        n1Id,
                                                        "N + 1",
                                                        jpaId,
                                                        null,
                                                        null,
                                                        List.of()
                                                )
                                        )),
                                new PostCategoryResponse(
                                        securityId,
                                        "Security",
                                        springId,
                                        jpaId,
                                        null,
                                        List.of(
                                                new PostCategoryResponse(
                                                        csrfId,
                                                        "CSRF",
                                                        securityId,
                                                        null,
                                                        oAuthId,
                                                        List.of()
                                                ),
                                                new PostCategoryResponse(
                                                        oAuthId,
                                                        "OAuth",
                                                        securityId,
                                                        csrfId,
                                                        null,
                                                        List.of()
                                                )
                                        ))
                        ))
        );

        assertThat(allByMemberId).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
