package com.mallang.category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.category.application.command.UpdateCategoryCommand;
import com.mallang.category.domain.Category;
import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.NoAuthorityUpdateCategoryException;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import com.mallang.category.exception.NotFoundCategoryException;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.member.MemberServiceTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("카테고리 서비스(CategoryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private CategoryServiceTestHelper categoryServiceTestHelper;

    @Autowired
    private CategoryService categoryService;

    @Nested
    class 저장_시 {

        @Test
        void 최상위_카테고리로_저장할_수_있다() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            CreateCategoryCommand command = new CreateCategoryCommand(말랑_ID, "최상위 카테고리", null);

            // when
            Long 최상위_카테고리 = categoryService.create(command);

            // then
            Category category = categoryServiceTestHelper.카테고리를_조회한다(최상위_카테고리);
            assertThat(category.getParent()).isNull();
            assertThat(category.getName()).isEqualTo("최상위 카테고리");
        }

        @Test
        void 계층형으로_저장할_수_있다() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            Long 최상위 = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "최상위");
            CreateCategoryCommand command = new CreateCategoryCommand(말랑_ID, "하위 카테고리", 최상위);

            // when
            Long id = categoryService.create(command);

            // then
            Category category = categoryServiceTestHelper.카테고리를_조회한다(id);
            assertThat(category.getParent().getId()).isEqualTo(최상위);
            assertThat(category.getName()).isEqualTo("하위 카테고리");
        }

        @Test
        void 없는_부모_카테고리_ID를_설정한_경우_예외() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            CreateCategoryCommand command = new CreateCategoryCommand(말랑_ID, "하위 카테고리", 100L);

            // when & then
            assertThatThrownBy(() ->
                    categoryService.create(command)
            ).isInstanceOf(NotFoundCategoryException.class);
        }

        @Test
        void 하위_카테고리를_생성하려는_회원가_상위_카테고리를_생성한_회원이_동일하지_않으면_예외() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            Long 동훈_ID = memberServiceTestHelper.회원을_저장한다("동훈");
            Long 최상위 = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "최상위");
            CreateCategoryCommand command = new CreateCategoryCommand(동훈_ID, "하위 카테고리", 최상위);

            // when & then
            assertThatThrownBy(() ->
                    categoryService.create(command)
            ).isInstanceOf(NoAuthorityUseCategoryException.class);
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 자신의_카테고리라면_수정_가능() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            Long categoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "최상위");
            Long childCategoryId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, "하위", categoryId);
            UpdateCategoryCommand command = new UpdateCategoryCommand(childCategoryId, 말랑_ID, "수정", categoryId);

            // when
            categoryService.update(command);

            // then
            Category category = categoryServiceTestHelper.카테고리를_조회한다(childCategoryId);
            assertThat(category.getName()).isEqualTo("수정");
            assertThat(category.getParent().getId()).isEqualTo(categoryId);
        }

        @Test
        void 부모_카테고리를_제거함으로써_최상위_카테고리로_만들_수_있다() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            Long categoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "최상위");
            Long childCategoryId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, "하위", categoryId);
            UpdateCategoryCommand command = new UpdateCategoryCommand(childCategoryId, 말랑_ID, "수정", null);

            // when
            categoryService.update(command);

            // then
            Category category = categoryServiceTestHelper.카테고리를_조회한다(childCategoryId);
            assertThat(category.getName()).isEqualTo("수정");
            assertThat(category.getParent()).isNull();
        }

        @Test
        void 부모_카테고리를_변경할_수_있다() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            Long categoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "최상위");
            Long otherRootCategory = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "최상위2");
            Long childCategoryId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, "하위", categoryId);
            Long childChildCategoryId1 = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, "더하위1", childCategoryId);
            Long childChildCategoryId2 = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, "더하위2", childCategoryId);
            UpdateCategoryCommand command = new UpdateCategoryCommand(childCategoryId, 말랑_ID, "수정", otherRootCategory);

            // when
            categoryService.update(command);

            // then
            Category category = categoryServiceTestHelper.카테고리를_조회한다(childCategoryId);
            assertThat(category.getName()).isEqualTo("수정");
            assertThat(category.getParent().getId()).isEqualTo(otherRootCategory);
            assertThat(category.getChildren())
                    .extracting(CommonDomainModel::getId)
                    .containsExactly(childChildCategoryId1, childChildCategoryId2);
        }

        @Test
        void 자신의_하위_카테고리를_부모로_만들_수_없다() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            Long categoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "최상위");
            Long childCategoryId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, "하위", categoryId);
            Long childChildCategoryId = categoryServiceTestHelper.하위_카테고리를_저장한다(말랑_ID, "더하위1", childCategoryId);
            UpdateCategoryCommand command1 = new UpdateCategoryCommand(categoryId, 말랑_ID, "수정", childCategoryId);
            UpdateCategoryCommand command2 = new UpdateCategoryCommand(categoryId, 말랑_ID, "수정", childChildCategoryId);

            // when & then
            assertThatThrownBy(() ->
                    categoryService.update(command1)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
            assertThatThrownBy(() ->
                    categoryService.update(command2)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 자신의_카테고리가_아니면_오류() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            Long otherMemberId = memberServiceTestHelper.회원을_저장한다("동훈");
            Long categoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "최상위");
            UpdateCategoryCommand command = new UpdateCategoryCommand(categoryId, otherMemberId, "수정", null);

            // when
            assertThatThrownBy(() ->
                    categoryService.update(command)
            ).isInstanceOf(NoAuthorityUpdateCategoryException.class);

            // then
            Category category = categoryServiceTestHelper.카테고리를_조회한다(categoryId);
            assertThat(category.getName()).isEqualTo("최상위");
            assertThat(category.getParent()).isNull();
        }

        @Test
        void 다른_사람의_카테고리의_하위_카테고리로_변경할_수_없다() {
            // given
            Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
            Long otherMemberId = memberServiceTestHelper.회원을_저장한다("동훈");
            Long categoryId = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "최상위");
            Long otherCategory = categoryServiceTestHelper.최상위_카테고리를_저장한다(otherMemberId, "최상위");
            UpdateCategoryCommand command = new UpdateCategoryCommand(categoryId, 말랑_ID, "수정", otherCategory);

            // when
            assertThatThrownBy(() ->
                    categoryService.update(command)
            ).isInstanceOf(NoAuthorityUseCategoryException.class);

            // then
            Category category = categoryServiceTestHelper.카테고리를_조회한다(categoryId);
            assertThat(category.getName()).isEqualTo("최상위");
            assertThat(category.getParent()).isNull();
        }
    }
}
