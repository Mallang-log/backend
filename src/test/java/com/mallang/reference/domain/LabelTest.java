package com.mallang.reference.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;

import com.mallang.auth.domain.Member;
import com.mallang.category.CategoryHierarchyViolationException;
import com.mallang.category.DuplicateCategoryNameException;
import com.mallang.category.FlatCategoryTestTemplate;
import com.mallang.common.execption.MallangLogException;
import com.mallang.reference.exception.InvalidLabelColorException;
import com.mallang.reference.exception.NoAuthorityLabelException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("라벨 (Label) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LabelTest extends FlatCategoryTestTemplate<Label> {

    private final String color = "#000000";

    @Override
    protected Label spyCategory(String name, Member owner) {
        Label label = new Label(name, owner, color);
        return spy(label);
    }

    @Override
    protected Label create(String name, Member owner) {
        return new Label(name, owner, color);
    }

    @Override
    protected Label create(String name, Member owner, Label prev, Label next) {
        Label label = new Label(name, owner, color);
        label.create(prev, next, validator);
        return label;
    }

    @Override
    protected Class<?> 권한_없음_예외() {
        return NoAuthorityLabelException.class;
    }

    @Override
    protected Class<? extends MallangLogException> 회원의_카테고리_없음_검증_실패_시_발생할_예외() {
        return CategoryHierarchyViolationException.class;
    }

    @Override
    protected Class<? extends MallangLogException> 중복_이름_존재여부_검증_실패_시_발생할_예외() {
        return DuplicateCategoryNameException.class;
    }

    @Nested
    class 생성_시 extends FlatCategoryTestTemplate<Label>.생성_시 {

        @Test
        void 색상을_가지고_생성된다() {
            // when
            Label label = new Label("Spring", member, "#000000");

            // then
            assertThat(label.getColor()).isEqualTo("#000000");
        }

        @Test
        void 색상_코드_형식이_잘못된_경우_예외() {
            // when & then
            assertThatThrownBy(() -> {
                new Label("Spring", member, "000000");
            }).isInstanceOf(InvalidLabelColorException.class);
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 색상과_이름을_업데이트한다() {
            // given
            Label label = new Label("Spring", member, "#000000");

            // when
            label.update("Node", "#AAAAAA");

            // then
            assertThat(label.getName()).isEqualTo("Node");
            assertThat(label.getColor()).isEqualTo("#AAAAAA");
        }

        @Test
        void 형제_중_중복된_이름을_가진_형제가_있다면_예외() {
            // given
            Label label = new Label("Spring", member, "#000000");
            Label prev = new Label("prev", member, "#000000");
            prev.updateHierarchy(null, label);

            // when & then
            assertThatThrownBy(() -> {
                label.update("prev", "#111111");
            }).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 색상_코드_형식이_잘못된_경우_예외() {
            // given
            Label label = new Label("Spring", member, "#000000");

            // when & then
            assertThatThrownBy(() -> {
                label.update("Node", "AAAAAA");
            }).isInstanceOf(InvalidLabelColorException.class);
        }
    }
}
