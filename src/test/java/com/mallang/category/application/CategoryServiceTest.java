package com.mallang.category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.category.application.command.DeleteCategoryCommand;
import com.mallang.category.application.command.UpdateCategoryCommand;
import com.mallang.category.domain.Category;
import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.ChildCategoryExistException;
import com.mallang.category.exception.DuplicateCategoryNameException;
import com.mallang.category.exception.NotFoundCategoryException;
import com.mallang.common.EventsTestUtils;
import com.mallang.common.ServiceTest;
import com.mallang.common.domain.CommonDomainModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("카테고리 서비스(CategoryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CategoryServiceTest extends ServiceTest {

    private Long mallangId;
    private Long mallangBlogId;
    private Long otherMemberId;
    private Long otherBlogId;

    @BeforeEach
    void setUp() {
        mallangId = 회원을_저장한다("mallang");
        mallangBlogId = 블로그_개설(mallangId, "mallang-log");
        otherMemberId = 회원을_저장한다("동훈");
        otherBlogId = 블로그_개설(otherMemberId, "donghun");
    }

    @Nested
    class 저장_시 {

        @Test
        void 최상위_카테고리로_저장할_수_있다() {
            // given
            CreateCategoryCommand command = CreateCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogId(mallangBlogId)
                    .name("최상위 카테고리")
                    .parentCategoryId(null)
                    .build();

            // when
            Long 최상위_카테고리 = categoryService.create(command);

            // then
            Category category = categoryRepository.getById(최상위_카테고리);
            assertThat(category.getParent()).isNull();
            assertThat(category.getName()).isEqualTo("최상위 카테고리");
        }

        @Test
        void 계층형으로_저장할_수_있다() {
            // given
            Long 최상위 = categoryService.create(new CreateCategoryCommand(mallangId, mallangBlogId, "최상위", null));
            CreateCategoryCommand command = CreateCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogId(mallangBlogId)
                    .name("하위 카테고리")
                    .parentCategoryId(최상위)
                    .build();

            // when
            Long id = categoryService.create(command);

            // then
            Category category = categoryRepository.getById(id);
            assertThat(category.getParent().getId()).isEqualTo(최상위);
            assertThat(category.getName()).isEqualTo("하위 카테고리");
        }

        @Test
        void 없는_부모_카테고리_ID를_설정한_경우_예외() {
            // given
            CreateCategoryCommand command = CreateCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogId(mallangBlogId)
                    .name("하위 카테고리")
                    .parentCategoryId(100L)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.create(command)
            ).isInstanceOf(NotFoundCategoryException.class);
        }

        @Test
        void 하위_카테고리를_생성하려는_회원가_상위_카테고리를_생성한_회원이_동일하지_않으면_예외() {
            // given
            Long 최상위 = categoryService.create(new CreateCategoryCommand(mallangId, mallangBlogId, "최상위", null));
            CreateCategoryCommand command = CreateCategoryCommand.builder()
                    .memberId(otherMemberId)
                    .blogId(otherBlogId)
                    .name("하위 카테고리")
                    .parentCategoryId(최상위)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.create(command)
            ).isInstanceOf(NotFoundCategoryException.class);
        }

        @Test
        void 루트끼리는_이름이_같을_수_없다() {
            // given
            categoryService.create(new CreateCategoryCommand(mallangId, mallangBlogId, "최상위", null));
            CreateCategoryCommand command = CreateCategoryCommand.builder()
                    .memberId(mallangId)
                    .blogId(mallangBlogId)
                    .name("최상위")
                    .parentCategoryId(null)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.create(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 자신의_카테고리라면_수정_가능() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위", null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "하위", categoryId
            ));
            UpdateCategoryCommand command = UpdateCategoryCommand.builder()
                    .categoryId(childCategoryId)
                    .memberId(mallangId)
                    .name("수정")
                    .parentCategoryId(categoryId)
                    .build();

            // when
            categoryService.update(command);

            // then
            Category category = categoryRepository.getById(childCategoryId);
            assertThat(category.getName()).isEqualTo("수정");
            assertThat(category.getParent().getId()).isEqualTo(categoryId);
        }

        @Test
        void 부모_카테고리를_제거함으로써_최상위_카테고리로_만들_수_있다() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위", null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "하위", categoryId
            ));
            UpdateCategoryCommand command = UpdateCategoryCommand.builder()
                    .categoryId(childCategoryId)
                    .memberId(mallangId)
                    .name("수정")
                    .parentCategoryId(null)
                    .build();

            // when
            categoryService.update(command);

            // then
            Category category = categoryRepository.getById(childCategoryId);
            assertThat(category.getName()).isEqualTo("수정");
            assertThat(category.getParent()).isNull();
        }

        @Test
        void 부모_카테고리를_변경할_수_있다() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위", null
            ));
            Long otherRootCategory = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위2", null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "하위", categoryId
            ));
            Long childChildCategoryId1 = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "더하위1", childCategoryId
            ));
            Long childChildCategoryId2 = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "더하위2", childCategoryId
            ));
            UpdateCategoryCommand command = UpdateCategoryCommand.builder()
                    .categoryId(childCategoryId)
                    .memberId(mallangId)
                    .name("수정")
                    .parentCategoryId(otherRootCategory)
                    .build();

            // when
            categoryService.update(command);

            // then
            transactionHelper.doAssert(() -> {
                Category category = categoryRepository.getById(childCategoryId);
                assertThat(category.getName()).isEqualTo("수정");
                assertThat(category.getParent().getId()).isEqualTo(otherRootCategory);
                assertThat(category.getChildren())
                        .extracting(CommonDomainModel::getId)
                        .containsExactly(childChildCategoryId1, childChildCategoryId2);
            });
        }

        @Test
        void 자신의_하위_카테고리를_부모로_만들_수_없다() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위", null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "하위", categoryId
            ));
            Long childChildCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "더하위1", childCategoryId
            ));
            UpdateCategoryCommand command1 = UpdateCategoryCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .name("수정")
                    .parentCategoryId(childCategoryId)
                    .build();
            UpdateCategoryCommand command2 = UpdateCategoryCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .name("수정")
                    .parentCategoryId(childChildCategoryId)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.update(command1)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
            assertThatThrownBy(() ->
                    categoryService.update(command2)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 자신의_카테고리가_아니면_예외() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위", null
            ));
            UpdateCategoryCommand command = UpdateCategoryCommand.builder()
                    .categoryId(categoryId)
                    .memberId(otherMemberId)
                    .name("수정")
                    .parentCategoryId(null)
                    .build();

            // when
            assertThatThrownBy(() ->
                    categoryService.update(command)
            ).isInstanceOf(NotFoundCategoryException.class);

            // then
            Category category = categoryRepository.getById(categoryId);
            assertThat(category.getName()).isEqualTo("최상위");
            assertThat(category.getParent()).isNull();
        }

        @Test
        void 다른_사람의_카테고리의_하위_카테고리로_변경할_수_없다() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위", null
            ));
            Long otherCategory = categoryService.create(new CreateCategoryCommand(
                    otherMemberId, otherBlogId, "최상위", null
            ));
            UpdateCategoryCommand command = UpdateCategoryCommand.builder()
                    .categoryId(categoryId)
                    .memberId(mallangId)
                    .name("수정")
                    .parentCategoryId(otherCategory)
                    .build();

            // when
            assertThatThrownBy(() ->
                    categoryService.update(command)
            ).isInstanceOf(NotFoundCategoryException.class);

            // then
            transactionHelper.doAssert(() -> {
                Category category = categoryRepository.getById(categoryId);
                assertThat(category.getName()).isEqualTo("최상위");
                assertThat(category.getParent()).isNull();
            });
        }

        @Test
        void 같은_부모를_가진_직계_자식끼리는_이름이_겹쳐서는_안된다() {
            // given
            Long 최상위 = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위", null
            ));
            Long 자식1 = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "하위1", 최상위
            ));
            Long 자식2 = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "하위2", 최상위
            ));
            UpdateCategoryCommand command = UpdateCategoryCommand.builder()
                    .categoryId(자식2)
                    .memberId(mallangId)
                    .name("하위1")
                    .parentCategoryId(최상위)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.update(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 루트끼리는_이름이_같을_수_없다() {
            // given
            Long 최상위 = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위", null
            ));
            Long 자식 = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "하위", 최상위
            ));
            UpdateCategoryCommand command = UpdateCategoryCommand.builder()
                    .categoryId(자식)
                    .memberId(mallangId)
                    .name("최상위")
                    .parentCategoryId(null)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    categoryService.update(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 제거_시 {

        @Test
        void 하위_카테고리가_있다면_오류() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위", null
            ));
            categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "하위", categoryId
            ));
            DeleteCategoryCommand command = DeleteCategoryCommand.builder()
                    .memberId(mallangId)
                    .categoryId(categoryId)
                    .build();

            // when
            assertThatThrownBy(() ->
                    categoryService.delete(command)
            ).isInstanceOf(ChildCategoryExistException.class);

            // then
            assertThat(categoryRepository.getById(categoryId)).isNotNull();
        }

        @Test
        void 자신의_카테고리가_아니라면_예외() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위", null
            ));
            categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "하위", categoryId
            ));
            DeleteCategoryCommand command = DeleteCategoryCommand.builder()
                    .memberId(otherMemberId)
                    .categoryId(categoryId)
                    .build();
            // when
            assertThatThrownBy(() ->
                    categoryService.delete(command)
            ).isInstanceOf(NotFoundCategoryException.class);

            // then
            assertThat(categoryRepository.getById(categoryId)).isNotNull();
        }

        @Test
        void 부모_카테고리의_자식에서_제거된다() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위", null
            ));
            Long childCategoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "하위", categoryId
            ));
            DeleteCategoryCommand command = DeleteCategoryCommand.builder()
                    .memberId(mallangId)
                    .categoryId(childCategoryId)
                    .build();
            // when
            categoryService.delete(command);

            // then
            assertThatThrownBy(() ->
                    categoryRepository.getById(childCategoryId)
            ).isInstanceOf(NotFoundCategoryException.class);
            transactionHelper.doAssert(() ->
                    assertThat(categoryRepository.getById(categoryId).getChildren()).isEmpty()
            );
        }

        @Test
        void 카테고리_제거_이벤트가_발행된다() {
            // given
            Long categoryId = categoryService.create(new CreateCategoryCommand(
                    mallangId, mallangBlogId, "최상위", null
            ));
            DeleteCategoryCommand command = DeleteCategoryCommand.builder()
                    .memberId(mallangId)
                    .categoryId(categoryId)
                    .build();

            // when
            categoryService.delete(command);

            // then
            int count = EventsTestUtils.count(events, CategoryDeletedEvent.class);
            assertThat(count).isEqualTo(1);
        }
    }
}
