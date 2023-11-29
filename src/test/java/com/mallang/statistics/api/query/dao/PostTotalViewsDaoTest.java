package com.mallang.statistics.api.query.dao;

import static com.mallang.common.LocalDateFixture.날짜_2023_11_25_토;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_26_일;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_27_월;
import static com.mallang.common.LocalDateFixture.날짜_2023_11_28_화;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.common.ServiceTest;
import com.mallang.post.domain.PostId;
import com.mallang.post.exception.NotFoundPostException;
import com.mallang.statistics.api.query.response.PostTotalViewsResponse;
import com.mallang.statistics.statistic.PostViewStatistic;
import com.mallang.statistics.statistic.PostViewStatisticRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("포스트 누적 조회수 조회 DAO (PostTotalViewsDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostTotalViewsDaoTest extends ServiceTest {

    private Long memberId;
    private String blogName;
    private PostId postId;

    @Autowired
    private PostViewStatisticRepository postViewStatisticRepository;

    @Autowired
    private PostTotalViewsDao postTotalViewsDao;

    @BeforeEach
    void setUp() {
        memberId = 회원을_저장한다("말랑");
        blogName = 블로그_개설(memberId, "mallang-log");
        postId = 포스트를_저장한다(memberId, blogName, "title", "bodyText");
    }

    @Test
    void 특정_포스트의_누적_조회수를_구한다() {
        // given
        PostViewStatistic 통계_2023_11_25 = new PostViewStatistic(날짜_2023_11_25_토, postId, 10);
        PostViewStatistic 통계_2023_11_26 = new PostViewStatistic(날짜_2023_11_26_일, postId, 5);
        PostViewStatistic 통계_2023_11_27 = new PostViewStatistic(날짜_2023_11_27_월, postId);
        PostViewStatistic 통계_2023_11_28 = new PostViewStatistic(날짜_2023_11_28_화, postId, 100);
        postViewStatisticRepository.saveAll(List.of(통계_2023_11_25, 통계_2023_11_26, 통계_2023_11_27, 통계_2023_11_28));

        // when
        PostTotalViewsResponse response = postTotalViewsDao.find(blogName, postId.getId());

        // then
        assertThat(response.totalViewCount()).isEqualTo(115);
    }

    @Test
    void 없는_포스트라면_예외() {
        // when & then
        assertThatThrownBy(() ->
                postTotalViewsDao.find(blogName, 1000L)
        ).isInstanceOf(NotFoundPostException.class);
    }
}
