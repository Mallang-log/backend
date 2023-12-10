package com.mallang.post.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.category.application.command.CreatePostCategoryCommand;
import com.mallang.category.domain.event.PostCategoryDeletedEvent;
import com.mallang.common.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 이벤트 핸들러 (PostEventHandler) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostEventHandlerTest extends ServiceTest {

    @Nested
    class 카테고리_삭제_이벤트를_받아 {

        @Test
        void 해당_카테고리에_속한_포스트들을_카테고리_없음으로_만든다() {
            // given
            Long 말랑_ID = 회원을_저장한다("말랑");
            String blogName = 블로그_개설(말랑_ID, "mallang-log");
            Long categoryId1 = postCategoryService.create(new CreatePostCategoryCommand(
                    말랑_ID,
                    blogName,
                    "최상위1",
                    null,
                    null,
                    null
            ));
            Long categoryId2 = postCategoryService.create(new CreatePostCategoryCommand(
                    말랑_ID,
                    blogName,
                    "최상위2",
                    null,
                    categoryId1,
                    null
            ));
            Long postId1 = 포스트를_저장한다(말랑_ID, blogName, "제목1", "내용", categoryId1).getPostId();
            Long postId2 = 포스트를_저장한다(말랑_ID, blogName, "제목2", "내용", categoryId1).getPostId();
            Long postId3 = 포스트를_저장한다(말랑_ID, blogName, "안삭제", "내용", categoryId2).getPostId();

            // when
            publisher.publishEvent(new PostCategoryDeletedEvent(categoryId1));

            // then
            assertThat(postRepository.getById(postId1, blogName).getCategory()).isNull();
            assertThat(postRepository.getById(postId2, blogName).getCategory()).isNull();
            assertThat(postRepository.getById(postId3, blogName).getCategory()).isNotNull();
        }
    }
}
