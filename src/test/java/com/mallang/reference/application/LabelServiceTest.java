package com.mallang.reference.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.category.CategoryHierarchyViolationException;
import com.mallang.category.DuplicateCategoryNameException;
import com.mallang.reference.application.command.CreateLabelCommand;
import com.mallang.reference.application.command.DeleteLabelCommand;
import com.mallang.reference.application.command.UpdateLabelAttributeCommand;
import com.mallang.reference.application.command.UpdateLabelHierarchyCommand;
import com.mallang.reference.domain.Label;
import com.mallang.reference.domain.LabelRepository;
import com.mallang.reference.domain.LabelValidator;
import com.mallang.reference.exception.InvalidLabelColorException;
import com.mallang.reference.exception.NoAuthorityLabelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("라벨 서비스 (LabelService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LabelServiceTest {

    private final LabelRepository labelRepository = mock(LabelRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final LabelValidator labelValidator = new LabelValidator(labelRepository);
    private final LabelService labelService = new LabelService(labelRepository, memberRepository, labelValidator);

    private final Member mallang = 깃허브_말랑(1L);
    private final Member donghun = 깃허브_동훈(2L);

    @BeforeEach
    void setUp() {
        given(memberRepository.getById(mallang.getId()))
                .willReturn(mallang);
        given(memberRepository.getById(donghun.getId()))
                .willReturn(donghun);
    }

    @Nested
    class 라벨_생성_시 {

        @Test
        void 형제_라벨을_지정하지_않았을_때_해당_회원이_아직_라벨이_없는_상태면_생성된다() {
            // given
            var labelId = 1L;
            given(labelRepository.existsByOwner(mallang))
                    .willReturn(false);
            Label label = new Label("빨간색", mallang, "#ff0000");
            ReflectionTestUtils.setField(label, "id", labelId);
            given(labelRepository.save(any()))
                    .willReturn(label);
            var command = new CreateLabelCommand(
                    mallang.getId(),
                    "빨간색",
                    "#ff0000",
                    null,
                    null
            );

            // when
            Long id = labelService.create(command);

            // then
            assertThat(id).isEqualTo(labelId);
        }

        @Test
        void 형제_라벨을_지정하지_않았을_때_해당_회원이_가진_라벨이_존재하면_예외() {
            // given
            given(labelRepository.existsByOwner(mallang))
                    .willReturn(true);
            var command = new CreateLabelCommand(
                    mallang.getId(),
                    "빨간색",
                    "#ff0000",
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    labelService.create(command)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 형제가_주어졌을_때_이름이_겹치는_라벨이_존재하면_예외() {
            // given
            Label prev = new Label("빨간색", mallang, "#000000");
            ReflectionTestUtils.setField(prev, "id", 1L);
            given(labelRepository.existsByOwnerAndName(mallang, "빨간색"))
                    .willReturn(true);
            given(labelRepository.getByIdIfIdNotNull(prev.getId()))
                    .willReturn(prev);
            var command = new CreateLabelCommand(
                    mallang.getId(),
                    "빨간색",
                    "#ff0000",
                    prev.getId(),
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    labelService.create(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 형제가_주어지고_이름이_겹치는_라벨이_없으면_계층구조를_설정하여_생성된다() {
            // given
            Label prev = new Label("prev", mallang, "#000000");
            ReflectionTestUtils.setField(prev, "id", 1L);
            given(labelRepository.existsByOwnerAndName(mallang, "빨간색"))
                    .willReturn(false);
            given(labelRepository.getByIdIfIdNotNull(prev.getId()))
                    .willReturn(prev);

            Label label = new Label("빨간색", mallang, "#ff0000");
            ReflectionTestUtils.setField(label, "id", 2L);
            given(labelRepository.save(any()))
                    .willReturn(label);
            var command = new CreateLabelCommand(
                    mallang.getId(),
                    "빨간색",
                    "#ff0000",
                    prev.getId(),
                    null
            );

            // when & then
            assertDoesNotThrow(() -> {
                labelService.create(command);
            });
        }
    }

    @Nested
    class 라벨_계층구조_업데이트_시 {

        @Test
        void 자신의_라벨이_아니면_예외() {
            // given
            var label = new Label("target", mallang, "#000000");
            ReflectionTestUtils.setField(label, "id", 1L);
            given(labelRepository.getById(label.getId()))
                    .willReturn(label);
            var command = new UpdateLabelHierarchyCommand(
                    donghun.getId(),
                    label.getId(),
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() -> {
                labelService.updateHierarchy(command);
            }).isInstanceOf(NoAuthorityLabelException.class);
        }

        @Test
        void 계층구조를_변경한다() {
            // given
            var label = new Label("target", mallang, "#000000");
            var prev = new Label("prev", mallang, "#000000");
            var next = new Label("next", mallang, "#000000");
            label.updateHierarchy(prev, null);
            next.updateHierarchy(label, null);
            ReflectionTestUtils.setField(label, "id", 1L);
            ReflectionTestUtils.setField(prev, "id", 2L);
            ReflectionTestUtils.setField(next, "id", 3L);
            given(labelRepository.getById(label.getId()))
                    .willReturn(label);
            given(labelRepository.getByIdIfIdNotNull(prev.getId()))
                    .willReturn(prev);
            given(labelRepository.getByIdIfIdNotNull(next.getId()))
                    .willReturn(next);

            var command = new UpdateLabelHierarchyCommand(
                    mallang.getId(),
                    label.getId(),
                    next.getId(),
                    null
            );

            // when
            labelService.updateHierarchy(command);

            // then
            assertThat(prev.getPreviousSibling()).isNull();
            assertThat(prev.getNextSibling()).isEqualTo(next);
            assertThat(next.getPreviousSibling()).isEqualTo(prev);
            assertThat(next.getNextSibling()).isEqualTo(label);
            assertThat(label.getPreviousSibling()).isEqualTo(next);
            assertThat(label.getNextSibling()).isNull();
        }

        @Nested
        class 형제들의_주인_일치여부를_검증하는데 {

            @Test
            void 이전_형제의_주인이_다른_경우_예외() {
                // given
                var label = new Label("target", mallang, "#000000");
                var prev = new Label("prev", donghun, "#000000");
                ReflectionTestUtils.setField(label, "id", 1L);
                ReflectionTestUtils.setField(prev, "id", 2L);
                given(labelRepository.getById(label.getId()))
                        .willReturn(label);
                given(labelRepository.getByIdIfIdNotNull(prev.getId()))
                        .willReturn(prev);

                var command = new UpdateLabelHierarchyCommand(
                        mallang.getId(),
                        label.getId(),
                        prev.getId(),
                        null
                );

                // when
                assertThatThrownBy(() -> {
                    labelService.updateHierarchy(command);
                }).isInstanceOf(NoAuthorityLabelException.class);
            }

            @Test
            void 이후_형제의_주인이_다른_경우_예외() {
                // given
                var label = new Label("target", mallang, "#000000");
                var prev = new Label("prev", donghun, "#000000");
                ReflectionTestUtils.setField(label, "id", 1L);
                ReflectionTestUtils.setField(prev, "id", 2L);
                given(labelRepository.getById(label.getId()))
                        .willReturn(label);
                given(labelRepository.getByIdIfIdNotNull(prev.getId()))
                        .willReturn(prev);

                var command = new UpdateLabelHierarchyCommand(
                        mallang.getId(),
                        label.getId(),
                        null,
                        prev.getId()
                );

                // when
                assertThatThrownBy(() -> {
                    labelService.updateHierarchy(command);
                }).isInstanceOf(NoAuthorityLabelException.class);
            }
        }

        @Nested
        class 나를_형제로_설정하는_경우 {

            @Test
            void 나를_이전_형제로_설정하는_경우_예외() {
                // given
                var label = new Label("target", mallang, "#000000");
                ReflectionTestUtils.setField(label, "id", 1L);
                given(labelRepository.getById(label.getId()))
                        .willReturn(label);
                given(labelRepository.getByIdIfIdNotNull(label.getId()))
                        .willReturn(label);

                var command = new UpdateLabelHierarchyCommand(
                        mallang.getId(),
                        label.getId(),
                        label.getId(),
                        null
                );

                // when & then
                assertThatThrownBy(() -> {
                    labelService.updateHierarchy(command);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신을 형제로 지정할 수 없습니다.");
            }

            @Test
            void 나를_다음_형제로_설정하는_경우_예외() {
                // given
                var label = new Label("target", mallang, "#000000");
                ReflectionTestUtils.setField(label, "id", 1L);
                given(labelRepository.getById(label.getId()))
                        .willReturn(label);
                given(labelRepository.getByIdIfIdNotNull(label.getId()))
                        .willReturn(label);

                var command = new UpdateLabelHierarchyCommand(
                        mallang.getId(),
                        label.getId(),
                        null,
                        label.getId()
                );

                // when & then
                assertThatThrownBy(() -> {
                    labelService.updateHierarchy(command);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신을 형제로 지정할 수 없습니다.");
            }
        }

        @Nested
        class 직전_형제와_다음_형제가_주어졌을_때 {

            @Test
            void 직전_형제와_다음_형제_사이_다른_형제가_있는_경우_예외() {
                // given
                var first = new Label("first", mallang, "#000000");
                var second = new Label("second", mallang, "#000000");
                var third = new Label("third", mallang, "#000000");
                var forth = new Label("forth", mallang, "#000000");
                second.updateHierarchy(first, null);
                third.updateHierarchy(second, null);
                forth.updateHierarchy(third, null);
                ReflectionTestUtils.setField(first, "id", 1L);
                ReflectionTestUtils.setField(second, "id", 2L);
                ReflectionTestUtils.setField(third, "id", 3L);
                ReflectionTestUtils.setField(forth, "id", 4L);
                given(labelRepository.getByIdIfIdNotNull(first.getId()))
                        .willReturn(first);
                given(labelRepository.getByIdIfIdNotNull(third.getId()))
                        .willReturn(third);
                given(labelRepository.getById(forth.getId()))
                        .willReturn(forth);

                var command = new UpdateLabelHierarchyCommand(
                        mallang.getId(),
                        forth.getId(),
                        first.getId(),
                        third.getId()
                );

                // when
                assertThatThrownBy(() -> {
                    labelService.updateHierarchy(command);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 직전_형제와_다음_형제의_순서가_바뀐_경우_예외() {
                // given
                var first = new Label("first", mallang, "#000000");
                var second = new Label("second", mallang, "#000000");
                var third = new Label("third", mallang, "#000000");
                second.updateHierarchy(first, null);
                third.updateHierarchy(second, null);
                ReflectionTestUtils.setField(first, "id", 1L);
                ReflectionTestUtils.setField(second, "id", 2L);
                ReflectionTestUtils.setField(third, "id", 3L);
                given(labelRepository.getByIdIfIdNotNull(first.getId()))
                        .willReturn(first);
                given(labelRepository.getByIdIfIdNotNull(second.getId()))
                        .willReturn(second);
                given(labelRepository.getById(third.getId()))
                        .willReturn(third);

                var command = new UpdateLabelHierarchyCommand(
                        mallang.getId(),
                        third.getId(),
                        second.getId(),
                        first.getId()
                );

                // when
                assertThatThrownBy(() -> {
                    labelService.updateHierarchy(command);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 직전_형제의_직후_형제가_존재하나_명시되지_않은_경우_예외() {
                // given
                var first = new Label("first", mallang, "#000000");
                var second = new Label("second", mallang, "#000000");
                var third = new Label("third", mallang, "#000000");
                second.updateHierarchy(first, null);
                third.updateHierarchy(second, null);
                ReflectionTestUtils.setField(first, "id", 1L);
                ReflectionTestUtils.setField(second, "id", 2L);
                ReflectionTestUtils.setField(third, "id", 3L);

                given(labelRepository.getByIdIfIdNotNull(first.getId()))
                        .willReturn(first);
                given(labelRepository.getById(third.getId()))
                        .willReturn(third);

                var command = new UpdateLabelHierarchyCommand(
                        mallang.getId(),
                        third.getId(),
                        first.getId(),
                        null
                );

                // when
                assertThatThrownBy(() -> {
                    labelService.updateHierarchy(command);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 직후_형제의_직전_형제가_존재하나_명시되지_않은_경우_예외() {
                // given
                var first = new Label("first", mallang, "#000000");
                var second = new Label("second", mallang, "#000000");
                var third = new Label("third", mallang, "#000000");
                second.updateHierarchy(first, null);
                third.updateHierarchy(second, null);
                ReflectionTestUtils.setField(first, "id", 1L);
                ReflectionTestUtils.setField(second, "id", 2L);
                ReflectionTestUtils.setField(third, "id", 3L);
                given(labelRepository.getById(first.getId()))
                        .willReturn(first);
                given(labelRepository.getByIdIfIdNotNull(third.getId()))
                        .willReturn(third);

                var command = new UpdateLabelHierarchyCommand(
                        mallang.getId(),
                        first.getId(),
                        null,
                        third.getId()
                );

                // when
                assertThatThrownBy(() -> {
                    labelService.updateHierarchy(command);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }
        }

        @Test
        void 형제들이_주어지지_않으면_예외() {
            // given
            var first = new Label("first", mallang, "#000000");
            ReflectionTestUtils.setField(first, "id", 1L);
            given(labelRepository.getById(first.getId()))
                    .willReturn(first);

            var command = new UpdateLabelHierarchyCommand(
                    mallang.getId(),
                    first.getId(),
                    null,
                    null
            );

            // when
            assertThatThrownBy(() -> {
                labelService.updateHierarchy(command);
            }).isInstanceOf(CategoryHierarchyViolationException.class)
                    .hasMessage("형제들이 제대로 명시되지 않았습니다.");
        }
    }

    @Nested
    class 라벨_속성_업데이트_시 {

        @Test
        void 자신의_라벨이_아니면_예외() {
            // given
            var label = new Label("target", mallang, "#000000");
            ReflectionTestUtils.setField(label, "id", 1L);
            given(labelRepository.getById(label.getId()))
                    .willReturn(label);
            var command = new UpdateLabelHierarchyCommand(
                    donghun.getId(),
                    label.getId(),
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() -> {
                labelService.updateHierarchy(command);
            }).isInstanceOf(NoAuthorityLabelException.class);
        }

        @Test
        void 이름과_색상을_변경한다() {
            // given
            var label = new Label("target", mallang, "#000000");
            ReflectionTestUtils.setField(label, "id", 1L);
            given(labelRepository.getById(label.getId()))
                    .willReturn(label);
            var command = new UpdateLabelAttributeCommand(
                    mallang.getId(),
                    label.getId(),
                    "change",
                    "#ffffff"
            );

            // when
            labelService.updateAttribute(command);

            // then
            assertThat(label.getName()).isEqualTo("change");
            assertThat(label.getColor()).isEqualTo("#ffffff");
        }

        @Test
        void 형제들_중_중복되는_이름이_존재하는_경우_예외() {
            // given
            var label = new Label("target", mallang, "#000000");
            ReflectionTestUtils.setField(label, "id", 1L);
            given(labelRepository.getById(label.getId()))
                    .willReturn(label);
            var prev = new Label("prev", mallang, "#000000");
            prev.updateHierarchy(null, label);
            ReflectionTestUtils.setField(prev, "id", 2L);
            given(labelRepository.getById(prev.getId()))
                    .willReturn(prev);

            var command = new UpdateLabelAttributeCommand(
                    mallang.getId(),
                    label.getId(),
                    "prev",
                    "#ffffff"
            );

            // when & then
            assertThatThrownBy(() -> {
                labelService.updateAttribute(command);
            }).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 색상코드가_잘못된경우_예외() {
            // given
            var label = new Label("target", mallang, "#000000");
            ReflectionTestUtils.setField(label, "id", 1L);
            given(labelRepository.getById(label.getId()))
                    .willReturn(label);

            var command = new UpdateLabelAttributeCommand(
                    mallang.getId(),
                    label.getId(),
                    "change",
                    "ffffff"
            );

            // when & then
            assertThatThrownBy(() -> {
                labelService.updateAttribute(command);
            }).isInstanceOf(InvalidLabelColorException.class);
        }
    }

    @Nested
    class 라벨_제거_시 {

        @Test
        void 자신의_라벨이_아니면_예외() {
            // given
            var label = new Label("target", mallang, "#000000");
            ReflectionTestUtils.setField(label, "id", 1L);
            given(labelRepository.getById(label.getId()))
                    .willReturn(label);
            var command = new DeleteLabelCommand(
                    donghun.getId(),
                    label.getId()
            );

            // when & then
            assertThatThrownBy(() -> {
                labelService.delete(command);
            }).isInstanceOf(NoAuthorityLabelException.class);
        }

        @Test
        void 자신은_제거되고_이전_형제와_다음_형제는_이어진다() {
            // given
            var prev = new Label("prev", mallang, "#000000");
            var label = new Label("target", mallang, "#000000");
            var next = new Label("next", mallang, "#000000");
            label.updateHierarchy(prev, null);
            next.updateHierarchy(label, null);
            ReflectionTestUtils.setField(prev, "id", 1L);
            ReflectionTestUtils.setField(label, "id", 2L);
            ReflectionTestUtils.setField(next, "id", 3L);
            given(labelRepository.getById(prev.getId()))
                    .willReturn(prev);
            given(labelRepository.getById(label.getId()))
                    .willReturn(label);
            given(labelRepository.getById(next.getId()))
                    .willReturn(next);
            var command = new DeleteLabelCommand(
                    mallang.getId(),
                    label.getId()
            );

            // when
            labelService.delete(command);

            // then
            verify(labelRepository, times(1)).delete(label);
            assertThat(prev.getNextSibling()).isEqualTo(next);
            assertThat(next.getPreviousSibling()).isEqualTo(prev);
        }

        // TODO 라벨에 속한 링크들 라벨 없음으로 바꾸기
    }
}
