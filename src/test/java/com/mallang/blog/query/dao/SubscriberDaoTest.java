package com.mallang.blog.query.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.blog.application.command.BlogSubscribeCommand;
import com.mallang.blog.query.response.SubscriberResponse;
import com.mallang.common.ServiceTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("구독자 조회 DAO (SubscriberDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class SubscriberDaoTest extends ServiceTest {

    @Test
    void 특정_회원을_구독중인_구독자_모두_조회() {
        // given
        Long 주인_ID = 회원을_저장한다("주인");
        String 주인_블로그_이름 = 블로그_개설(주인_ID, "owner-blog");
        Long 구독자1_ID = 회원을_저장한다("구독자1");
        Long 구독자2_ID = 회원을_저장한다("구독자2");
        Long 구독자3_ID = 회원을_저장한다("구독자3");
        Long 다른블로그주인_ID = 회원을_저장한다("다른블로그주인");
        String 다른_블로그_이름 = 블로그_개설(다른블로그주인_ID, "other-blog");
        Long 다른블로그구독자1_ID = 회원을_저장한다("다른블로그구독자1");
        blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자1_ID, 주인_블로그_이름));
        blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자2_ID, 주인_블로그_이름));
        blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자3_ID, 주인_블로그_이름));
        blogSubscribeService.subscribe(new BlogSubscribeCommand(다른블로그구독자1_ID, 다른_블로그_이름));

        // when
        List<SubscriberResponse> result = subscriberDao.findSubscribers(주인_블로그_이름, pageable)
                .getContent();

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(SubscriberResponse::subscriberNickname)
                .containsExactly("구독자3", "구독자2", "구독자1");
    }
}
