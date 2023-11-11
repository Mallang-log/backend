package com.mallang.subscribe.query.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.auth.MemberServiceTestHelper;
import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.common.ServiceTest;
import com.mallang.subscribe.application.BlogSubscribeService;
import com.mallang.subscribe.application.command.BlogSubscribeCommand;
import com.mallang.subscribe.query.data.SubscriberData;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("구독자 조회 DAO(SubscriberDataDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class SubscriberDataDaoTest {

    @Autowired
    private SubscriberDataDao subscriberDataDao;

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private BlogServiceTestHelper blogServiceTestHelper;

    @Autowired
    private BlogSubscribeService blogSubscribeService;

    @Test
    void 특정_회원을_구독중인_구독자_모두_조회() {
        // given
        Long 주인_ID = memberServiceTestHelper.회원을_저장한다("주인");
        Long 주인_블로그_ID = blogServiceTestHelper.블로그_개설(주인_ID, "owner-blog").getId();
        Long 구독자1_ID = memberServiceTestHelper.회원을_저장한다("구독자1");
        Long 구독자2_ID = memberServiceTestHelper.회원을_저장한다("구독자2");
        Long 구독자3_ID = memberServiceTestHelper.회원을_저장한다("구독자3");
        Long 다른블로그주인_ID = memberServiceTestHelper.회원을_저장한다("다른블로그주인");
        Long 다른_블로그_ID = blogServiceTestHelper.블로그_개설(다른블로그주인_ID, "other-blog").getId();
        Long 다른블로그구독자1_ID = memberServiceTestHelper.회원을_저장한다("다른블로그구독자1");
        blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자1_ID, 주인_블로그_ID));
        blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자2_ID, 주인_블로그_ID));
        blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자3_ID, 주인_블로그_ID));
        blogSubscribeService.subscribe(new BlogSubscribeCommand(다른블로그구독자1_ID, 다른_블로그_ID));

        // when
        List<SubscriberData> result = subscriberDataDao.findSubscribers(주인_블로그_ID);

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(SubscriberData::subscriberNickname)
                .containsExactly("구독자3", "구독자2", "구독자1");
    }
}
