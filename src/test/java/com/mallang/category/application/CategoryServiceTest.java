package com.mallang.category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.category.application.exception.InvalidParentCategory;
import com.mallang.category.application.exception.NotFoundCategoryException;
import com.mallang.category.domain.Category;
import com.mallang.member.MemberServiceHelper;
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
    private MemberServiceHelper memberServiceHelper;

    @Autowired
    private CategoryServiceTestHelper categoryServiceTestHelper;

    @Autowired
    private CategoryService categoryService;

    @Nested
    class 저장_시 {

        @Test
        void 최상위_카테고리로_저장할_수_있다() {
            // given
            Long 말랑_ID = memberServiceHelper.회원을_저장한다("말랑");
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
            Long 말랑_ID = memberServiceHelper.회원을_저장한다("말랑");
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
            Long 말랑_ID = memberServiceHelper.회원을_저장한다("말랑");
            CreateCategoryCommand command = new CreateCategoryCommand(말랑_ID, "하위 카테고리", 100L);

            // when & then
            assertThatThrownBy(() ->
                    categoryService.create(command)
            ).isInstanceOf(NotFoundCategoryException.class);
        }

        @Test
        void 하위_카테고리를_생성하려는_회원가_상위_카테고리를_생성한_회원이_동일하지_않으면_예외() {
            // given
            Long 말랑_ID = memberServiceHelper.회원을_저장한다("말랑");
            Long 동훈_ID = memberServiceHelper.회원을_저장한다("동훈");
            Long 최상위 = categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "최상위");
            CreateCategoryCommand command = new CreateCategoryCommand(동훈_ID, "하위 카테고리", 최상위);

            // when & then
            assertThatThrownBy(() ->
                    categoryService.create(command)
            ).isInstanceOf(InvalidParentCategory.class);
        }
    }
}
