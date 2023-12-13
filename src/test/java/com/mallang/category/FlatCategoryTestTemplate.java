package com.mallang.category;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mallang.auth.domain.Member;
import com.mallang.common.execption.MallangLogException;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public abstract class FlatCategoryTestTemplate<T extends FlatCategory<T>> {

    protected final Member member = 깃허브_말랑(1L);
    protected final Member otherMember = 깃허브_동훈(2L);
    protected final FlatCategoryValidator validator = mock(FlatCategoryValidator.class);

    protected abstract T spyCategory(String name, Member owner);

    protected abstract T create(String name, Member owner);

    protected abstract T create(String name, Member owner, T prev, T next);

    protected abstract Class<?> 권한_없음_예외();

    protected abstract Class<? extends MallangLogException> 회원의_카테고리_없음_검증_실패_시_발생할_예외();

    protected abstract Class<? extends MallangLogException> 중복_이름_존재여부_검증_실패_시_발생할_예외();

    @Nested
    protected class 생성_시 {

        @Test
        void 최초의_카테고리_생성() {
            // given
            T mock = spyCategory("first", member);

            // when & then
            assertDoesNotThrow(() -> {
                mock.create(null, null, validator);
            });
            verify(mock, times(0))
                    .updateHierarchy(any(), any());
        }

        @Test
        void 이미_다른_카테고리가_존재하는_상황에서_형제가_모두_null_이면_예외() {
            // given
            T second = create("second", member);
            willThrow(회원의_카테고리_없음_검증_실패_시_발생할_예외())
                    .given(validator)
                    .validateNoCategories(member);

            // when & then
            assertThatThrownBy(() -> {
                second.create(null, null, validator);
            }).isInstanceOf(회원의_카테고리_없음_검증_실패_시_발생할_예외());
        }

        @Test
        void 중복되는_이름이_형제중에_존재하면_예외() {
            // given
            T first = create("first", member);
            T duplicated = create("first", member);
            willThrow(중복_이름_존재여부_검증_실패_시_발생할_예외())
                    .given(validator)
                    .validateDuplicateName(member, "first");

            // when & then
            assertThatThrownBy(() -> {
                duplicated.create(first, null, validator);
            }).isInstanceOf(중복_이름_존재여부_검증_실패_시_발생할_예외());
            assertThatThrownBy(() -> {
                duplicated.create(null, first, validator);
            }).isInstanceOf(중복_이름_존재여부_검증_실패_시_발생할_예외());
        }

        @Test
        void 형제가_모두_null_이_아니고_이름도_겹치지_않으면_계층_업데이트_메서드를_호출하여_계층구조를_설정한다() {
            // given
            T second = spyCategory("second", member);
            T first = create("first", member);

            // when & then
            second.create(first, null, validator);
            verify(second, times(1))
                    .updateHierarchy(any(), any());
        }
    }

    @Nested
    class 이름_수정_시 {

        private final T first = create("루트", member);

        @Test
        void 형제가_없다면_이름을_변경할_수_있다() {
            // when
            first.updateName("말랑");

            // then
            assertThat(first.getName()).isEqualTo("말랑");
        }

        @Test
        void 형제_중_이름이_같은게_없다면_이름을_변경할_수_있다() {
            // given
            T sibling = create("형제", member);
            sibling.updateHierarchy(null, first);

            // when
            first.updateName("이름 다름");

            // then
            assertThat(first.getName()).isEqualTo("이름 다름");
        }

        @Test
        void 형제_중_이름이_같은게_있다면_예외() {
            // given
            T sibling = create("형제", member);
            sibling.updateHierarchy(null, first);

            // when & then
            assertThatThrownBy(() -> {
                first.updateName("형제");
            }).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 카테고리_게층_구조_변경_시 {

        @Test
        void 계층구조를_변경한다() {
            // given
            T first = create("first", member);
            T second = create("second", member);
            second.updateHierarchy(null, first);

            // when
            first.updateHierarchy(null, second);

            // then
            assertThat(first.getNextSibling()).isEqualTo(second);
            assertThat(first.getPreviousSibling()).isNull();
            assertThat(second.getNextSibling()).isNull();
            assertThat(second.getPreviousSibling()).isEqualTo(first);
        }

        @Nested
        class 주인_일치여부를_검증하는데 {

            @Test
            void 이전_형제의_주인이_다른_경우_예외() {
                // given
                T prev = create("prev", otherMember);
                T target = create("target", member);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(prev, null);
                }).isInstanceOf(권한_없음_예외());
            }

            @Test
            void 이후_형제의_주인이_다른_경우_예외() {
                // given
                T next = create("next", otherMember);
                T target = create("target", member);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(null, next);
                }).isInstanceOf(권한_없음_예외());
            }
        }

        @Nested
        class 나를_형제로_설정하는_경우 {

            @Test
            void 나를_이전_형제로_설정하는_경우_예외() {
                // given
                T first = create("first", member);

                // when & then
                assertThatThrownBy(() -> {
                    first.updateHierarchy(first, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신을 형제로 지정할 수 없습니다.");
            }

            @Test
            void 나를_다음_형제로_설정하는_경우_예외() {
                // given
                T first = create("first", member);

                // when & then
                assertThatThrownBy(() -> {
                    first.updateHierarchy(null, first);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신을 형제로 지정할 수 없습니다.");
            }
        }

        @Nested
        class 직전_형제와_다음_형제가_주어졌을_때 {

            @Test
            void 직전_형제와_다음_형제_사이_다른_형제가_있는_경우_예외() {
                // given
                T first = create("first", member);
                T second = create("second", member);
                T third = create("third", member);
                T target = create("target", member);

                second.updateHierarchy(first, null);
                third.updateHierarchy(second, null);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(first, third);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 직전_형제와_다음_형제의_순서가_바뀐_경우_예외() {
                // given
                T first = create("first", member);
                T second = create("second", member);
                T target = create("target", member);

                second.updateHierarchy(first, null);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(second, first);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 직전_형제의_직후_형제가_존재하나_명시되지_않은_경우_예외() {
                // given
                T first = create("first", member);
                T second = create("second", member);
                T target = create("target", member);

                second.updateHierarchy(first, null);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(first, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 직후_형제의_직전_형제가_존재하나_명시되지_않은_경우_예외() {
                // given
                T first = create("first", member);
                T second = create("second", member);
                T target = create("target", member);

                second.updateHierarchy(first, null);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(null, second);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }
        }


        @Test
        void 형제들이_주어지지_않으면_예외() {
            // given
            T prev = create("first", member);
            T next = create("child", member);
            prev.updateHierarchy(null, next);

            // when & then
            assertThatThrownBy(() -> {
                next.updateHierarchy(null, null);
            }).isInstanceOf(CategoryHierarchyViolationException.class)
                    .hasMessage("형제들이 제대로 명시되지 않았습니다.");
        }
    }

    @Nested
    class 제거_시 {

        protected final T prev = create("하위 이전", member, null, null);
        protected final T me = create("하위", member, prev, null);
        protected final T next = create("하위 이후", member, me, null);

        @Test
        void 이전과_이후_카테고리가_존재하면_이들을_이어준다() {
            // when
            me.delete();

            // then
            assertThat(prev.getNextSibling()).isEqualTo(next);
            assertThat(next.getPreviousSibling()).isEqualTo(prev);
        }
    }

    @Test
    void 주인을_검증한다() {
        // given
        T 최상위 = create("최상위", member);

        // when & then
        assertDoesNotThrow(() -> {
            최상위.validateOwner(member);

        });
        assertThatThrownBy(() -> {
            최상위.validateOwner(otherMember);
        }).isInstanceOf(권한_없음_예외());
    }

    @Test
    void 나를_제외한_내_형제들을_반환한다() {
        // given
        T first = create("최상위", member);
        T second = create("최상위", member, first, null);
        T third = create("최상위", member, second, null);
        T forth = create("최상위", member, third, null);
        T fifth = create("최상위", member, forth, null);

        // when
        List<T> siblingsExceptSelf = third.getSiblingsExceptSelf();

        // then
        assertThat(siblingsExceptSelf)
                .containsExactly(first, second, forth, fifth);
    }
}
