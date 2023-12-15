package com.mallang.blog.query;

import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.auth.OauthMemberFixture.깃허브_회원;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.subscribe.BlogSubscribe;
import com.mallang.blog.query.repository.BlogSubscribeQueryRepository;
import com.mallang.blog.query.response.SubscriberResponse;
import com.mallang.blog.query.response.SubscribingBlogResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("블로그 구독 조회 서비스 (BlogSubscribeQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogSubscribeQueryServiceTest {

    private final BlogSubscribeQueryRepository blogSubscribeQueryRepository = mock(BlogSubscribeQueryRepository.class);
    private final BlogSubscribeQueryService blogSubscribeQueryService = new BlogSubscribeQueryService(
            blogSubscribeQueryRepository
    );

    private final Pageable pageable = PageRequest.of(0, 10);
    private final Member mallang = 깃허브_말랑();
    private final Blog blog = mallangBlog(mallang);

    @Test
    void 특정_회원을_구독중인_구독자_모두_조회() {
        // given
        Member 구독자2 = 깃허브_회원("구독자2");
        Member 구독자1 = 깃허브_회원("구독자1");
        PageImpl<BlogSubscribe> result = new PageImpl<>(
                List.of(
                        new BlogSubscribe(구독자2, blog),
                        new BlogSubscribe(구독자1, blog)
                ),
                pageable,
                2
        );
        given(blogSubscribeQueryRepository.findSubscribers("mallang-blog", pageable))
                .willReturn(result);

        // when
        List<SubscriberResponse> responses = blogSubscribeQueryService.findSubscribers("mallang-blog", pageable)
                .getContent();

        // then
        assertThat(responses)
                .hasSize(2)
                .extracting(SubscriberResponse::subscriberNickname)
                .containsExactly("구독자2", "구독자1");
    }

    @Test
    void 특정_회원이_구독중인_블로그_모두_조회() {
        // given
        Member other1 = 깃허브_회원("other1");
        Member other2 = 깃허브_회원("other2");
        Blog blog1 = new Blog("other1-blog", other1);
        Blog blog2 = new Blog("other2-blog", other2);
        PageImpl<BlogSubscribe> result = new PageImpl<>(
                List.of(
                        new BlogSubscribe(mallang, blog2),
                        new BlogSubscribe(mallang, blog1)
                ),
                pageable,
                2
        );
        given(blogSubscribeQueryRepository.findSubscribingBlogs(mallang.getId(), pageable))
                .willReturn(result);

        // when
        List<SubscribingBlogResponse> responses =
                blogSubscribeQueryService.findSubscribingBlogs(mallang.getId(), pageable).getContent();

        // then
        assertThat(responses)
                .hasSize(2)
                .extracting(SubscribingBlogResponse::blogName)
                .containsExactly("other2-blog", "other1-blog");
    }
}
