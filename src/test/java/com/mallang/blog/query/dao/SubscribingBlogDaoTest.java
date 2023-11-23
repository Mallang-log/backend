package com.mallang.blog.query.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.blog.application.command.BlogSubscribeCommand;
import com.mallang.blog.query.response.SubscribingBlogResponse;
import com.mallang.common.ServiceTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("특정 회원이 구독중인 블로그 조회 DAO(SubscribingBlogDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class SubscribingBlogDaoTest extends ServiceTest {

    @Test
    void 특정_회원이_구독중인_블로그_모두_조회() {
        // given
        Long 주인_ID = 회원을_저장한다("주인");
        Long 주인_블로그_ID = 블로그_개설(주인_ID, "owner-blog");
        Long 구독자1_ID = 회원을_저장한다("구독자1");
        Long 다른블로그주인_ID = 회원을_저장한다("다른블로그주인");
        Long 다른_블로그_ID = 블로그_개설(다른블로그주인_ID, "other-blog");
        blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자1_ID, 주인_블로그_ID));
        blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자1_ID, 다른_블로그_ID));

        // when
        List<SubscribingBlogResponse> result = subscribingBlogDao.findSubscribingBlogs(구독자1_ID, pageable)
                .getContent();

        // then
        assertThat(result)
                .hasSize(2)
                .extracting(SubscribingBlogResponse::blogName)
                .containsExactly("other-blog", "owner-blog");
    }
}
