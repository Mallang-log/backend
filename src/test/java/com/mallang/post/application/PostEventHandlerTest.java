package com.mallang.post.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.category.application.CategoryServiceTestHelper;
import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.member.MemberServiceTestHelper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("포스트 이벤트 핸들러(PostEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
@SpringBootTest
class PostEventHandlerTest {

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private PostServiceTestHelper postServiceTestHelper;

    @Autowired
    private CategoryServiceTestHelper categoryServiceTestHelper;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private EntityManager em;

    @Nested
    class 카테고리_삭제_이벤트를_받아 {

        @Test
        void 해당_카테고리에_속한_포스트들을_카테고리_없음으로_만든다() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            Long categoryId1 = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "최상위1");
            Long categoryId2 = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "최상위2");
            Long postId1 = postServiceTestHelper.포스트를_저장한다(말랑_ID, "제목1", "내용", categoryId1);
            Long postId2 = postServiceTestHelper.포스트를_저장한다(말랑_ID, "제목2", "내용", categoryId1);
            Long postId3 = postServiceTestHelper.포스트를_저장한다(말랑_ID, "안삭제", "내용", categoryId2);

            // when
            publisher.publishEvent(new CategoryDeletedEvent(categoryId1));
            flushAndClear();

            // then
            assertThat(postServiceTestHelper.포스트를_조회한다(postId1).getCategory()).isNull();
            assertThat(postServiceTestHelper.포스트를_조회한다(postId2).getCategory()).isNull();
            assertThat(postServiceTestHelper.포스트를_조회한다(postId3).getCategory()).isNotNull();
        }
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}