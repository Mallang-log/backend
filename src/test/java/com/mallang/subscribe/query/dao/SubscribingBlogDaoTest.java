package com.mallang.subscribe.query.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.auth.MemberServiceTestHelper;
import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.common.ServiceTest;
import com.mallang.subscribe.application.BlogSubscribeService;
import com.mallang.subscribe.application.command.BlogSubscribeCommand;
import com.mallang.subscribe.query.response.SubscribingBlogResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("특정 회원이 구독중인 블로그 조회 DAO(SubscribingBlogDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class SubscribingBlogDaoTest {

    @Autowired
    private SubscribingBlogDao subscribingBlogDataDao;

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private BlogServiceTestHelper blogServiceTestHelper;

    @Autowired
    private BlogSubscribeService blogSubscribeService;

    @Test
    void 특정_회원이_구독중인_블로그_모두_조회() {
        // given
        Long 주인_ID = memberServiceTestHelper.회원을_저장한다("주인");
        String 주인_블로그_이름 = blogServiceTestHelper.블로그_개설(주인_ID, "owner-blog").getName();
        Long 구독자1_ID = memberServiceTestHelper.회원을_저장한다("구독자1");
        Long 다른블로그주인_ID = memberServiceTestHelper.회원을_저장한다("다른블로그주인");
        String 다른_블로그_이름 = blogServiceTestHelper.블로그_개설(다른블로그주인_ID, "other-blog").getName();
        blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자1_ID, 주인_블로그_이름));
        blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자1_ID, 다른_블로그_이름));

        // when
        List<SubscribingBlogResponse> result = subscribingBlogDataDao.findSubscribingBlogs(구독자1_ID);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(SubscribingBlogResponse::blogName)
                .containsExactly("other-blog", "owner-blog");
    }
}