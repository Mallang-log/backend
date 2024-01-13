package com.mallang.post.application;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CreateDraftCommand;
import com.mallang.post.application.command.CreatePostCategoryCommand;
import com.mallang.post.application.command.DeleteDraftCommand;
import com.mallang.post.application.command.UpdateDraftCommand;
import com.mallang.post.domain.draft.Draft;
import com.mallang.post.exception.NoAuthorityDraftException;
import com.mallang.post.exception.NoAuthorityPostCategoryException;
import com.mallang.post.exception.NotFoundDraftException;
import com.mallang.post.exception.NotFoundPostCategoryException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("임시 글 서비스 (DraftService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class DraftServiceTest extends ServiceTest {

    private Long memberId;
    private String blogName;
    private Long categoryId;

    @BeforeEach
    void setUp() {
        memberId = 회원을_저장한다("말랑");
        blogName = 블로그_개설(memberId, "mallang-log");
        categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                memberId,
                blogName,
                "Spring",
                null,
                null,
                null
        ));
    }

    @Nested
    class 임시_글_저장_시 {

        @Test
        void 저장한다() {
            // given
            CreateDraftCommand command = CreateDraftCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("임시_글 1")
                    .bodyText("bodyText")
                    .categoryId(categoryId)
                    .tags(List.of("tag1", "tag2", "tag3"))
                    .build();

            // when
            Long id = draftService.create(command);

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 카테고리와_태그는_없어도_된다() {
            // given
            CreateDraftCommand command = CreateDraftCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("임시_글 1")
                    .bodyText("bodyText")
                    .build();

            // when
            Long id = draftService.create(command);

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 다른_사람의_블로그에_대한_임시_글_작성시_예외() {
            // given
            Long otherMemberId = 회원을_저장한다("other");
            String otherBlogName = 블로그_개설(otherMemberId, "other-log");
            CreateDraftCommand command = CreateDraftCommand.builder()
                    .memberId(memberId)
                    .blogName(otherBlogName)
                    .title("임시_글 1")
                    .bodyText("bodyText")
                    .build();
            CreateDraftCommand command2 = CreateDraftCommand.builder()
                    .memberId(otherMemberId)
                    .blogName(blogName)
                    .title("임시_글 1")
                    .bodyText("bodyText")
                    .build();

            // when & then
            assertThatThrownBy(() -> {
                draftService.create(command);
            }).isInstanceOf(NoAuthorityBlogException.class);
            assertThatThrownBy(() -> {
                draftService.create(command2);
            }).isInstanceOf(NoAuthorityBlogException.class);
        }

        @Test
        void 없는_카테고리면_예외() {
            // given
            CreateDraftCommand command = CreateDraftCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("임시_글 1")
                    .bodyText("bodyText")
                    .categoryId(1000L)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    draftService.create(command)
            ).isInstanceOf(NotFoundPostCategoryException.class);
        }

        @Test
        void 다른_사람의_카테고리라면_예외() {
            // given
            Long otherMemberId = 회원을_저장한다("other");
            String otherBlogName = 블로그_개설(otherMemberId, "other-log");
            Long categoryId = postCategoryService.create(new CreatePostCategoryCommand(
                    otherMemberId,
                    otherBlogName,
                    "Spring",
                    null,
                    null,
                    null
            ));
            CreateDraftCommand command = CreateDraftCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("임시_글 1")
                    .bodyText("bodyText")
                    .categoryId(categoryId)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    draftService.create(command)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);
        }
    }

    @Nested
    class 임시_글_수정_시 {

        private Long 임시_글_ID;

        @BeforeEach
        void setUp() {
            CreateDraftCommand command = CreateDraftCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("임시_글 1")
                    .bodyText("bodyText")
                    .tags(List.of("tag1", "tag2", "tag3"))
                    .categoryId(categoryId)
                    .build();
            임시_글_ID = draftService.create(command);
        }

        @Test
        void 내가_쓴_임시_글을_수정할_수_있다() {
            // given
            UpdateDraftCommand command = new UpdateDraftCommand(
                    memberId,
                    임시_글_ID,
                    "수정제목",
                    "수정내용",
                    null,
                    List.of("태그2")
            );

            // when
            draftService.update(command);

            // then
            transactionHelper.doAssert(() -> {
                Draft draft = draftRepository.getById(임시_글_ID);
                assertThat(draft.getTitle()).isEqualTo("수정제목");
                assertThat(draft.getBodyText()).isEqualTo("수정내용");
                assertThat(draft.getCategory()).isNull();
                assertThat(draft.getTags()).containsExactly("태그2");
            });
        }

        @Test
        void 다른_사람의_임시_글은_수정할_수_없다() {
            // given
            Long otherMemberId = 회원을_저장한다("동훈");
            UpdateDraftCommand command = new UpdateDraftCommand(
                    otherMemberId, 임시_글_ID,
                    "수정제목",
                    "수정내용",
                    null,
                    emptyList()
            );

            // when & then
            assertThatThrownBy(() ->
                    draftService.update(command)
            ).isInstanceOf(NoAuthorityDraftException.class);
        }
    }

    @Nested
    class 임시_글_제거_시 {

        private Long otherMemberId;
        private Long 임시_글_ID;

        @BeforeEach
        void setUp() {
            otherMemberId = 회원을_저장한다("other");
            CreateDraftCommand command = CreateDraftCommand.builder()
                    .memberId(memberId)
                    .blogName(blogName)
                    .title("임시_글 1")
                    .bodyText("bodyText")
                    .tags(List.of("tag1", "tag2", "tag3"))
                    .build();
            임시_글_ID = draftService.create(command);
        }

        @Test
        void 자신이_작성한_글이_아닌_경우_예외() {
            // given
            DeleteDraftCommand command = new DeleteDraftCommand(otherMemberId, 임시_글_ID);

            // when & then
            assertThatThrownBy(() -> {
                draftService.delete(command);
            }).isInstanceOf(NoAuthorityDraftException.class);
        }

        @Test
        void 없는_임시_글을_제거하려_한다면_예외() {
            // given
            DeleteDraftCommand command = new DeleteDraftCommand(otherMemberId, 임시_글_ID + 10L);

            // when & then
            assertThatThrownBy(() -> {
                draftService.delete(command);
            }).isInstanceOf(NotFoundDraftException.class);
        }

        @Test
        void 임시_글을_제거한다() {
            // given
            DeleteDraftCommand command = new DeleteDraftCommand(memberId, 임시_글_ID);

            // when & then
            draftService.delete(command);
        }
    }
}
