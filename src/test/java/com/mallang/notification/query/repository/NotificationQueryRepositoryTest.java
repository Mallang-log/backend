package com.mallang.notification.query.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.mallang.blog.query.repository.SubscriberDao;
import com.mallang.blog.query.repository.SubscriberDao.SubscriberDaoImpl;
import com.mallang.blog.query.repository.SubscribingBlogDao.SubscribingBlogDaoImpl;
import com.mallang.common.config.JpaConfig;
import com.mallang.common.config.QueryDslConfig;
import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.NotificationRepository;
import com.mallang.notification.domain.type.BlogSubscribedNotification;
import com.mallang.notification.domain.type.CommentWrittenNotification;
import com.mallang.notification.domain.type.CommentWrittenNotification.Type;
import com.mallang.post.domain.PostId;
import com.mallang.post.query.repository.PostManageSearchDao.PostManageSearchDaoImpl;
import com.mallang.post.query.repository.PostSearchDao.PostSearchDaoImpl;
import com.mallang.post.query.repository.StaredPostDao.StaredPostDaoImpl;
import com.mallang.reference.query.repository.ReferenceLinkSearchDao.ReferenceLinkSearchDaoImpl;
import com.mallang.statistics.api.query.repository.BlogVisitStatisticManageDao.BlogVisitStatisticManageDaoImpl;
import com.mallang.statistics.api.query.repository.BlogVisitStatisticSimpleDao.BlogVisitStatisticSimpleDaoImpl;
import com.mallang.statistics.api.query.repository.PostViewStatisticDao.PostViewStatisticDaoImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DisplayName("알림 조회 Repository (NotificationQueryRepository) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DataJpaTest(
        includeFilters = {
                @Filter(type = ASSIGNABLE_TYPE, classes = {
                        // TODO polishing
                        JpaConfig.class,
                        QueryDslConfig.class,
                        JPAQueryFactory.class,
                        SubscriberDao.SubscriberDaoImpl.class,
                        BlogVisitStatisticManageDaoImpl.class,
                        BlogVisitStatisticManageDaoImpl.class,
                        PostSearchDaoImpl.class,
                        BlogVisitStatisticSimpleDaoImpl.class,
                        PostManageSearchDaoImpl.class,
                        PostViewStatisticDaoImpl.class,
                        ReferenceLinkSearchDaoImpl.class,
                        StaredPostDaoImpl.class,
                        SubscriberDaoImpl.class,
                        SubscribingBlogDaoImpl.class
                })
        }
)
class NotificationQueryRepositoryTest {

    @Autowired
    private NotificationQueryRepository notificationQueryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void 특정_회원의_알림들을_조회한다() {
        // given
        notificationRepository.saveAll(List.of(
                new BlogSubscribedNotification(
                        1L,
                        1L,
                        1L
                ),
                new CommentWrittenNotification(
                        1L,
                        Type.COMMENT,
                        new PostId(1L, 1L),
                        1L,
                        1L,
                        "parentWriter",
                        1L,
                        1L,
                        "writer",
                        null,
                        "message"
                ),
                new BlogSubscribedNotification(
                        1L,
                        2L,
                        2L
                ),
                new CommentWrittenNotification(
                        1L,
                        Type.COMMENT_REPLY,
                        new PostId(1L, 1L),
                        1L,
                        1L,
                        "parentWriter",
                        1L,
                        1L,
                        "writer",
                        null,
                        "message"
                )
        ));

        // when
        Page<Notification> response = notificationQueryRepository
                .findAllByTargetMemberId(1L, PageRequest.of(0, 3, Sort.by(DESC, "createdDate")));

        // then
        assertThat(response.getTotalPages()).isEqualTo(2);
        assertThat(response.getTotalElements()).isEqualTo(4);
        assertThat(response.getNumberOfElements()).isEqualTo(3);
        assertThat(response.getNumber()).isZero();
        assertThat(response.getContent())
                .extracting(it -> it.getClass().getSimpleName())
                .containsExactly(
                        CommentWrittenNotification.class.getSimpleName(),
                        BlogSubscribedNotification.class.getSimpleName(),
                        CommentWrittenNotification.class.getSimpleName());
    }
}
