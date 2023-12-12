package com.mallang.category;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.auth.domain.Member;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public abstract class TieredCategoryTestTemplate<T extends TieredCategory<T>> {

    protected final Member member = 깃허브_말랑(1L);
    protected final Member otherMember = 깃허브_동훈(2L);

    protected abstract T createRoot(String name, Member owner);

    protected abstract T createChild(String name, Member owner, T parent);

    protected abstract T createChild(String name, Member owner, T parent, T prev, T next);

    protected abstract Class<?> 권한_없음_예외();

    @Nested
    protected class 생성_시 {

        @Test
        void 생성한다() {
            // when & then
            assertDoesNotThrow(() -> {
                createRoot("최상위", member);
            });
        }
    }

    @Nested
    class 이름_수정_시 {

        private final T root = createRoot("루트", member);

        @Test
        void 형제가_없다면_이름을_변경할_수_있다() {
            // when
            root.updateName("말랑");

            // then
            assertThat(root.getName()).isEqualTo("말랑");
        }

        @Test
        void 형제_중_이름이_같은게_없다면_이름을_변경할_수_있다() {
            // given
            T postCategory = createRoot("형제", member);
            postCategory.updateHierarchy(null, root, null);

            // when
            root.updateName("이름 다름");

            // then
            assertThat(root.getName()).isEqualTo("이름 다름");
        }

        @Test
        void 형제_중_이름이_같은게_있다면_예외() {
            // given
            T postCategory = createRoot("형제", member);
            postCategory.updateHierarchy(null, root, null);

            // when & then
            assertThatThrownBy(() -> {
                root.updateName("형제");
            }).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 카테고리_게층_구조_변경_시 {

        @Test
        void 계층구조를_변경한다() {
            // given
            T root1 = createRoot("root1", member);
            T root2 = createRoot("root2", member);
            T root1First = createRoot("first", member);
            T root1Second = createRoot("second", member);
            T root1Third = createRoot("third", member);
            T root1Forth = createRoot("forth", member);
            root2.updateHierarchy(null, root1, null);
            root1First.updateHierarchy(root1, null, null);
            root1Second.updateHierarchy(root1, root1First, null);
            root1Third.updateHierarchy(root1, root1Second, null);
            root1Forth.updateHierarchy(root1, root1Third, null);

            // when
            root2.updateHierarchy(root1, root1Second, root1Third);

            // then
            assertThat(root1.getChildren()).containsExactlyInAnyOrder(
                    root1First, root1Second, root2, root1Third, root1Forth
            );
            assertThat(root1First.getPreviousSibling()).isNull();
            assertThat(root1First.getNextSibling()).isEqualTo(root1Second);

            assertThat(root1Second.getPreviousSibling()).isEqualTo(root1First);
            assertThat(root1Second.getNextSibling()).isEqualTo(root2);

            assertThat(root2.getPreviousSibling()).isEqualTo(root1Second);
            assertThat(root2.getNextSibling()).isEqualTo(root1Third);

            assertThat(root1Third.getPreviousSibling()).isEqualTo(root2);
            assertThat(root1Third.getNextSibling()).isEqualTo(root1Forth);

            assertThat(root1Forth.getPreviousSibling()).isEqualTo(root1Third);
            assertThat(root1Forth.getNextSibling()).isNull();
        }

        @Test
        void 무한_Depth_가_가능하다() {
            // given
            T root = createRoot("root", member);
            T child = createRoot("child", member);
            T childChild = createRoot("childChild", member);
            T childChildChild = createRoot("childChildChild", member);

            // when
            child.updateHierarchy(root, null, null);
            childChild.updateHierarchy(child, null, null);
            childChildChild.updateHierarchy(childChild, null, null);

            // then
            assertThat(child.getDescendantsExceptSelf()).containsExactly(childChild, childChildChild);
            assertThat(childChild.getDescendantsExceptSelf()).containsExactly(childChildChild);
        }

        @Test
        void 변경_이후에도_카테고리의_자식들은_동일하다() {
            // given
            T root = createRoot("Spring", member);

            T firstChild = createRoot("First", member);
            firstChild.updateHierarchy(root, null, null);

            T firstFirstChild = createRoot("FirstFirst", member);
            firstFirstChild.updateHierarchy(firstChild, null, null);

            T secondChild = createRoot("Second", member);
            secondChild.updateHierarchy(root, firstChild, null);

            firstChild.updateHierarchy(null, root, null);

            // then
            assertThat(root.getDescendantsExceptSelf()).containsExactly(secondChild);
            assertThat(firstChild.getParent()).isNull();
            assertThat(firstChild.getDescendantsExceptSelf()).containsExactly(firstFirstChild);
        }

        @Nested
        class 주인_일치여부를_검증하는데 {

            @Test
            void 부모의_주인이_다른_경우_예외() {
                // given
                T parent = createRoot("root", otherMember);
                T target = createRoot("target", member);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(parent, null, null);
                }).isInstanceOf(권한_없음_예외());
            }

            @Test
            void 이전_형제의_주인이_다른_경우_예외() {
                // given
                T prev = createRoot("prev", otherMember);
                T target = createRoot("target", member);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(null, prev, null);
                }).isInstanceOf(권한_없음_예외());
            }

            @Test
            void 이후_형제의_주인이_다른_경우_예외() {
                // given
                T next = createRoot("next", otherMember);
                T target = createRoot("target", member);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(null, null, next);
                }).isInstanceOf(권한_없음_예외());
            }
        }

        @Nested
        class 나_혹은_내_자손들_중_하나를_부모나_형제로_설정하는_경우 {

            @Test
            void 나를_부모로_설정하는_경우_예외() {
                // given
                T root = createRoot("root", member);

                // when & then
                assertThatThrownBy(() -> {
                    root.updateHierarchy(root, null, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 내_자식을_부모로_설정하는_경우_예외() {
                // given
                T root = createRoot("root", member);
                T child = createRoot("child", member);
                child.updateHierarchy(root, null, null);

                // when & then
                assertThatThrownBy(() -> {
                    root.updateHierarchy(child, null, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 내_자손을_부모로_설정하는_경우_예외() {
                // given
                T root = createRoot("root", member);
                T child = createRoot("child", member);
                T descendant = createRoot("descendant", member);
                child.updateHierarchy(root, null, null);
                descendant.updateHierarchy(child, null, null);

                // when & then
                assertThatThrownBy(() -> {
                    root.updateHierarchy(descendant, null, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 나를_이전_형제로_설정하는_경우_예외() {
                // given
                T root = createRoot("root", member);

                // when & then
                assertThatThrownBy(() -> {
                    root.updateHierarchy(null, root, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 내_자식을_이전_형제로_설정하는_경우_예외() {
                // given
                T root = createRoot("root", member);
                T child = createRoot("child", member);
                child.updateHierarchy(root, null, null);

                // when & then
                assertThatThrownBy(() -> {
                    root.updateHierarchy(null, child, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 내_자손을_이전_형제로_설정하는_경우_예외() {
                // given
                T root = createRoot("root", member);
                T child = createRoot("child", member);
                T descendant = createRoot("descendant", member);
                child.updateHierarchy(root, null, null);
                descendant.updateHierarchy(child, null, null);

                // when & then
                assertThatThrownBy(() -> {
                    root.updateHierarchy(null, descendant, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 나를_다음_형제로_설정하는_경우_예외() {
                // given
                T root = createRoot("root", member);

                // when & then
                assertThatThrownBy(() -> {
                    root.updateHierarchy(null, null, root);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 내_자식을_다음_형제로_설정하는_경우_예외() {
                // given
                T root = createRoot("root", member);
                T child = createRoot("child", member);
                child.updateHierarchy(root, null, null);

                // when & then
                assertThatThrownBy(() -> {
                    root.updateHierarchy(null, null, child);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 내_자손을_다음_형제로_설정하는_경우_예외() {
                // given
                T root = createRoot("root", member);
                T child = createRoot("child", member);
                T descendant = createRoot("descendant", member);
                child.updateHierarchy(root, null, null);
                descendant.updateHierarchy(child, null, null);

                // when & then
                assertThatThrownBy(() -> {
                    root.updateHierarchy(null, null, descendant);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }
        }

        @Nested
        class 직전_형제와_다음_형제가_주어졌을_때 {

            @Test
            void 직전_형제와_다음_형제_사이_다른_형제가_있는_경우_예외() {
                // given
                T first = createRoot("first", member);
                T second = createRoot("second", member);
                T third = createRoot("third", member);
                T target = createRoot("target", member);

                second.updateHierarchy(null, first, null);
                third.updateHierarchy(null, second, null);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(null, first, third);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 직전_형제와_다음_형제의_순서가_바뀐_경우_예외() {
                // given
                T first = createRoot("first", member);
                T second = createRoot("second", member);
                T target = createRoot("target", member);

                second.updateHierarchy(null, first, null);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(null, second, first);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 직전_형제의_직후_형제가_존재하나_명시되지_않은_경우_예외() {
                // given
                T first = createRoot("first", member);
                T second = createRoot("second", member);
                T target = createRoot("target", member);

                second.updateHierarchy(null, first, null);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(null, first, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 직후_형제의_직전_형제가_존재하나_명시되지_않은_경우_예외() {
                // given
                T first = createRoot("first", member);
                T second = createRoot("second", member);
                T target = createRoot("target", member);

                second.updateHierarchy(null, first, null);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(null, null, second);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 주어진_부모와_형제들의_부모가_다른_경우_예외() {
                // given
                T otherParent = createRoot("otherParent", member);
                T parent = createRoot("parent", member);
                T first = createRoot("first", member);
                T second = createRoot("second", member);
                T target = createRoot("target", member);

                first.updateHierarchy(parent, null, null);
                second.updateHierarchy(parent, first, null);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(otherParent, first, second);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("주어진 형제와 부모의 관계가 올바르지 않습니다.");
            }
        }

        @Nested
        class 형제들이_주어지지_않았을_때 {

            @Test
            void 부모가_주어지지_않았으며_루트의_형제가_하나라도_존재한다면_예외() {
                // given
                T target = createRoot("target", member);
                T prev = createRoot("prev", member);
                prev.updateHierarchy(null, null, target);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(null, null, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("존재하는 다른 최상위 카테고리와의 관계가 명시되지 않았습니다.");
                assertThatThrownBy(() -> {
                    prev.updateHierarchy(null, null, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("존재하는 다른 최상위 카테고리와의 관계가 명시되지 않았습니다.");
            }

            @Test
            void 부모가_주어지지_않았으며_루트의_형제가_존재하지_않을_때_내가_루트라면_업데이트() {
                // given
                T target = createRoot("target", member);

                // when & then
                assertDoesNotThrow(() -> {
                    target.updateHierarchy(null, null, null);
                });
            }

            @Test
            void 부모가_주어지지_않았으며_루트의_형제가_존재하지_않을_때_내가_루트가_아니라면_예외() {
                // given
                T root = createRoot("root", member);
                T child = createRoot("child", member);
                child.updateHierarchy(root, null, null);

                // when & then
                assertThatThrownBy(() -> {
                    child.updateHierarchy(null, null, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("존재하는 다른 최상위 카테고리와의 관계가 명시되지 않았습니다.");
            }

            @Test
            void 부모가_주어지지_않았으며_내가_루트가_아닌_경우_예외() {
                // given
                T root = createRoot("root", member);
                T child = createRoot("target", member);
                child.updateHierarchy(root, null, null);
                T descendant = createRoot("descendant", member);
                descendant.updateHierarchy(child, null, null);

                // when
                assertThatThrownBy(() -> {
                    child.updateHierarchy(null, null, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("존재하는 다른 최상위 카테고리와의 관계가 명시되지 않았습니다.");
            }

            @Test
            void 부모가_주어지지_않았으며_내가_루트이나_내_형제가_존재하면_예외() {
                // given
                T root = createRoot("root", member);
                T next = createRoot("next", member);
                next.updateHierarchy(null, root, null);

                // when
                assertThatThrownBy(() -> {
                    root.updateHierarchy(null, null, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("존재하는 다른 최상위 카테고리와의 관계가 명시되지 않았습니다.");
            }

            @Test
            void 부모가_주어지고_해당_부모의_자식이_존재하는_경우_예외() {
                // given
                T parent = createRoot("parent", member);
                T child = createRoot("child", member);
                child.updateHierarchy(parent, null, null);
                T target = createRoot("target", member);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(parent, null, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("주어진 부모의 자식 카테고리와의 관계가 명시되지 않았습니다.");
            }
        }

        @Nested
        class 이전_형제가_주어진_경우 {

            @Test
            void 부모는_주어지지_않았는데_이전_형제의_부모가_존재하는_경우_예외() {
                // given
                T parent = createRoot("parent", member);
                T prev = createRoot("prev", member);
                prev.updateHierarchy(parent, null, null);
                T target = createRoot("target", member);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(null, prev, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("주어진 형제와 부모의 관계가 올바르지 않습니다.");
            }

            @Test
            void 부모가_주어졌을_때_이전_형제의_부모와_주어진_부모와_다른_경우_예외() {
                // given
                T otherParent = createRoot("otherParent", member);

                T parent = createRoot("parent", member);
                T prev = createRoot("prev", member);
                prev.updateHierarchy(parent, null, null);

                T target = createRoot("target", member);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(otherParent, prev, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("주어진 형제와 부모의 관계가 올바르지 않습니다.");
            }
        }

        @Nested
        class 다음_형제가_주어진_경우 {

            @Test
            void 부모는_주어지지_않았는데_다음_형제의_부모가_존재하는_경우_예외() {
                // given
                T parent = createRoot("parent", member);
                T prev = createRoot("prev", member);
                prev.updateHierarchy(parent, null, null);
                T target = createRoot("target", member);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(null, prev, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("주어진 형제와 부모의 관계가 올바르지 않습니다.");
            }

            @Test
            void 부모가_주어졌을_때_다음_형제의_부모와_주어진_부모와_다른_경우_예외() {
                // given
                T parent = createRoot("parent", member);
                T next = createRoot("next", member);
                next.updateHierarchy(parent, null, null);

                T target = createRoot("target", member);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(next, null, next);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("주어진 형제와 부모의 관계가 올바르지 않습니다.");
            }
        }

        @Nested
        class 계층_참여_시_중복_이름이_존재하게_되는_경우 {

            @Test
            void 이전_형제와_이름이_같으면_예외() {
                // given
                T parent = createRoot("parent", member);
                T prev = createRoot("prev", member);
                T next = createRoot("next", member);
                prev.updateHierarchy(parent, null, null);
                next.updateHierarchy(parent, prev, null);

                T target = createRoot("prev", member);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(parent, prev, next);
                }).isInstanceOf(DuplicateCategoryNameException.class)
                        .hasMessage("직전 형제 카테고리와 이름이 겹칩니다.");
            }

            @Test
            void 다음_형제와_이름이_같으면_예외() {
                // given
                T prev = createRoot("prev", member);
                T next = createRoot("next", member);
                prev.updateHierarchy(null, null, null);
                next.updateHierarchy(null, prev, null);

                T target = createRoot("next", member);

                // when & then
                assertThatThrownBy(() -> {
                    target.updateHierarchy(null, prev, next);
                }).isInstanceOf(DuplicateCategoryNameException.class)
                        .hasMessage("다음 형제 카테고리와 이름이 겹칩니다.");
            }

            @Test
            void 부모와는_이름이_같아도_된다() {
                // given
                T parent = createRoot("parent", member);
                T prev = createRoot("prev", member);
                T next = createRoot("next", member);
                prev.updateHierarchy(parent, null, null);
                next.updateHierarchy(parent, prev, null);

                T target = createRoot("parent", member);

                // when & then
                assertDoesNotThrow(() -> {
                    target.updateHierarchy(parent, prev, next);
                });
            }
        }
    }

    @Nested
    class 제거_시 {

        protected final T root = createRoot("루트", member);
        protected final T prev = createChild("하위 이전", member, root, null, null);
        protected final T child = createChild("하위", member, root, prev, null);
        protected final T next = createChild("하위 이후", member, root, child, null);

        @Test
        void 하위_카테고리가_존재하면_제거할_수_없다() {
            // when & then
            assertThatThrownBy(() ->
                    root.delete()
            ).isInstanceOf(ChildCategoryExistException.class);
        }

        @Test
        void 부모_카테고리의_하위_카테고리에서도_제거된다() {
            // when
            child.delete();

            // then
            assertThat(root.getSortedChildren())
                    .doesNotContain(child)
                    .hasSize(2);
        }

        @Test
        void 이전과_이후_카테고리가_존재하면_이들을_이어준다() {
            // when
            child.delete();

            // then
            assertThat(prev.getNextSibling()).isEqualTo(next);
            assertThat(next.getPreviousSibling()).isEqualTo(prev);
        }
    }

    @Test
    void 주인을_검증한다() {
        // given
        T 최상위 = createRoot("최상위", member);

        // when & then
        assertDoesNotThrow(() -> {
            최상위.validateOwner(member);

        });
        assertThatThrownBy(() -> {
            최상위.validateOwner(otherMember);
        }).isInstanceOf(권한_없음_예외());
    }

    @Test
    void 모든_자손을_반환한다() {
        // given
        T 최상위 = createRoot("최상위", member);
        T 하위 = createChild("하위", member, 최상위);
        T 더하위1 = createChild("더하위1", member, 하위);
        T 더하위2 = createChild("더하위2", member, 하위, 더하위1, null);
        T 더더하위1 = createChild("더더하위1", member, 더하위1);

        // when
        List<T> 최상위_descendants = 최상위.getDescendantsExceptSelf();
        List<T> 하위_descendants = 하위.getDescendantsExceptSelf();

        // then
        assertThat(최상위_descendants)
                .containsExactlyInAnyOrder(하위, 더하위1, 더하위2, 더더하위1);
        assertThat(하위_descendants)
                .containsExactlyInAnyOrder(더하위1, 더하위2, 더더하위1);
    }
}
