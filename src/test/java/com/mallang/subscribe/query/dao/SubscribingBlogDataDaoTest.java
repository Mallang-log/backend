package com.mallang.subscribe.query.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.common.ServiceTest;
import com.mallang.member.MemberServiceTestHelper;
import com.mallang.subscribe.application.BlogSubscribeService;
import com.mallang.subscribe.application.command.BlogSubscribeCommand;
import com.mallang.subscribe.query.data.SubscribingBlogData;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("내가 구독중인 블로그 조회 DAO(SubscribingBlogDataDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class SubscribingBlogDataDaoTest {

    @Autowired
    private SubscribingBlogDataDao subscribingBlogDataDao;

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private BlogServiceTestHelper blogServiceTestHelper;

    @Autowired
    private BlogSubscribeService blogSubscribeService;

    @Test
    void 내가_구독중인_블로그_모두_조회() {
        // given
        Long 주인_ID = memberServiceTestHelper.회원을_저장한다("주인");
        Long 주인_블로그_ID = blogServiceTestHelper.블로그_개설(주인_ID, "owner-blog");
        Long 구독자1_ID = memberServiceTestHelper.회원을_저장한다("구독자1");
        Long 다른블로그주인_ID = memberServiceTestHelper.회원을_저장한다("다른블로그주인");
        Long 다른_블로그_ID = blogServiceTestHelper.블로그_개설(다른블로그주인_ID, "other-blog");
        blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자1_ID, 주인_블로그_ID));
        blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자1_ID, 다른_블로그_ID));

        // when
        List<SubscribingBlogData> result = subscribingBlogDataDao.findSubscribingBlogs(구독자1_ID);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(SubscribingBlogData::blogName)
                .containsExactly("owner-blog", "other-blog");
    }
}
