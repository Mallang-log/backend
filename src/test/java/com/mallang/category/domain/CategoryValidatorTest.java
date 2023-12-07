package com.mallang.category.domain;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.DuplicateCategoryNameException;
import com.mallang.category.exception.NoAuthorityCategoryException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("카테고리 검증기 (CategoryValidator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
class CategoryValidatorTest {

    private final Member member = 깃허브_말랑(1L);
    private final Member otherMember = 깃허브_동훈(2L);
    private final Blog memberBlog = new Blog("member-log", member);
    private final Blog otherBlog = new Blog("other-log", otherMember);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final CategoryValidator categoryValidator = new CategoryValidator(categoryRepository);

    @Nested
    class 형제들_중_중복되는_이름이_있는지_검증_시 {

        @Nested
        class 루트_카테고리인_경우 {

            @Test
            void 다른_루트_카테고리들_중_주어진_이름을_가진_카테고리가_있으면_예외() {
                // given
                Category root = new Category("root", member, memberBlog);
                Category otherRoot1 = new Category("otherRoot1", member, memberBlog);
                Category otherRoot2 = new Category("otherRoot2", member, memberBlog);
                given(categoryRepository.findAllRootByBlog(memberBlog))
                        .willReturn(List.of(root, otherRoot1, otherRoot2));

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateDuplicateNameInSibling(root, "otherRoot2");
                }).isInstanceOf(DuplicateCategoryNameException.class);
            }

            @Test
            void 다른_루트_카테고리들_중_주어진_이름을_가진_카테고리가_없으면_문제없다() {
                // given
                Category root = new Category("root", member, memberBlog);
                Category otherRoot1 = new Category("otherRoot1", member, memberBlog);
                Category otherRoot2 = new Category("otherRoot2", member, memberBlog);
                given(categoryRepository.findAllRootByBlog(memberBlog))
                        .willReturn(List.of(root, otherRoot1, otherRoot2));

                // when & then
                assertDoesNotThrow(() -> {
                    categoryValidator.validateDuplicateNameInSibling(root, "root3");
                });
            }

            @Test
            void 자신의_이름은_중복되어도_된다() {
                // given
                Category root = new Category("root", member, memberBlog);
                Category otherRoot1 = new Category("otherRoot1", member, memberBlog);
                Category otherRoot2 = new Category("otherRoot2", member, memberBlog);
                given(categoryRepository.findAllRootByBlog(memberBlog))
                        .willReturn(List.of(root, otherRoot1, otherRoot2));

                // when & then
                assertDoesNotThrow(() -> {
                    categoryValidator.validateDuplicateNameInSibling(root, "root");
                });
            }
        }

        @Nested
        class 루트가_아닌_경우 {

            @Test
            void 해당_카테고리의_부모의_자식등_중_주어진_이름을_가진_카테고리가_있으면_예외() {
                // given
                Category root = new Category("root", member, memberBlog);
                Category child1 = new Category("child1", member, memberBlog);
                Category child2 = new Category("child2", member, memberBlog);
                Category child3 = new Category("child3", member, memberBlog);
                child1.updateHierarchy(root, null, null, categoryValidator);
                child2.updateHierarchy(root, child1, null, categoryValidator);
                child3.updateHierarchy(root, child2, null, categoryValidator);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateDuplicateNameInSibling(child1, "child3");
                }).isInstanceOf(DuplicateCategoryNameException.class);
            }

            @Test
            void 해당_카테고리의_부모의_자식등_중_주어진_이름을_가진_카테고리가_없으면_문제없다() {
                // given
                Category root = new Category("root", member, memberBlog);
                Category child1 = new Category("child1", member, memberBlog);
                Category child2 = new Category("child2", member, memberBlog);
                Category child3 = new Category("child3", member, memberBlog);
                child1.updateHierarchy(root, null, null, categoryValidator);
                child2.updateHierarchy(root, child1, null, categoryValidator);
                child3.updateHierarchy(root, child2, null, categoryValidator);

                // when & then
                assertDoesNotThrow(() -> {
                    categoryValidator.validateDuplicateNameInSibling(child1, "child4");
                });
            }

            @Test
            void 자신의_이름은_중복되어도_된다() {
                // given
                Category root = new Category("root", member, memberBlog);
                Category child1 = new Category("child1", member, memberBlog);
                Category child2 = new Category("child2", member, memberBlog);
                Category child3 = new Category("child3", member, memberBlog);
                child1.updateHierarchy(root, null, null, categoryValidator);
                child2.updateHierarchy(root, child1, null, categoryValidator);
                child3.updateHierarchy(root, child2, null, categoryValidator);

                // when & then
                assertDoesNotThrow(() -> {
                    categoryValidator.validateDuplicateNameInSibling(child1, "child1");
                });
            }
        }
    }

    @Nested
    class 계층구조_변경_검증_시 {

        @Nested
        class 나_혹은_내_자손들_중_하나를_부모나_형제로_설정하는_경우 {

            @Test
            void 나를_부모로_설정하는_경우() {
                // given
                Category root = new Category("root", member, memberBlog);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(root, root, null, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 내_자식을_부모로_설정하는_경우() {
                // given
                Category root = new Category("root", member, memberBlog);
                Category child = new Category("child", member, memberBlog);
                child.updateHierarchy(root, null, null, categoryValidator);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(root, child, null, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 내_자손을_부모로_설정하는_경우() {
                // given
                Category root = new Category("root", member, memberBlog);
                Category child = new Category("child", member, memberBlog);
                Category descendant = new Category("descendant", member, memberBlog);
                child.updateHierarchy(root, null, null, categoryValidator);
                descendant.updateHierarchy(child, null, null, categoryValidator);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(root, descendant, null, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 나를_이전_형제로_설정하는_경우() {
                // given
                Category root = new Category("root", member, memberBlog);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(root, null, root, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 내_자식을_이전_형제로_설정하는_경우() {
                // given
                Category root = new Category("root", member, memberBlog);
                Category child = new Category("child", member, memberBlog);
                child.updateHierarchy(root, null, null, categoryValidator);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(root, null, child, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 내_자손을_이전_형제로_설정하는_경우() {
                // given
                Category root = new Category("root", member, memberBlog);
                Category child = new Category("child", member, memberBlog);
                Category descendant = new Category("descendant", member, memberBlog);
                child.updateHierarchy(root, null, null, categoryValidator);
                descendant.updateHierarchy(child, null, null, categoryValidator);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(root, null, descendant, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 나를_다음_형제로_설정하는_경우() {
                // given
                Category root = new Category("root", member, memberBlog);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(root, null, null, root);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 내_자식을_다음_형제로_설정하는_경우() {
                // given
                Category root = new Category("root", member, memberBlog);
                Category child = new Category("child", member, memberBlog);
                child.updateHierarchy(root, null, null, categoryValidator);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(root, null, null, child);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }

            @Test
            void 내_자손을_다음_형제로_설정하는_경우() {
                // given
                Category root = new Category("root", member, memberBlog);
                Category child = new Category("child", member, memberBlog);
                Category descendant = new Category("descendant", member, memberBlog);
                child.updateHierarchy(root, null, null, categoryValidator);
                descendant.updateHierarchy(child, null, null, categoryValidator);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(root, null, null, descendant);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
            }
        }

        @Nested
        class 참여하려는_계층구조의_주인이_다른_경우 {

            @Test
            void 부모의_주인이_다른_경우_예외() {
                // given
                Category parent = new Category("root", otherMember, otherBlog);
                Category target = new Category("target", member, memberBlog);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(target, parent, null, null);
                }).isInstanceOf(NoAuthorityCategoryException.class);
            }

            @Test
            void 이전_형제의_주인이_다른_경우_예외() {
                // given
                Category prev = new Category("prev", otherMember, otherBlog);
                Category target = new Category("target", member, memberBlog);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(target, null, prev, null);
                }).isInstanceOf(NoAuthorityCategoryException.class);
            }

            @Test
            void 이후_형제의_주인이_다른_경우() {
                // given
                Category next = new Category("next", otherMember, otherBlog);
                Category target = new Category("target", member, memberBlog);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(target, null, null, next);
                }).isInstanceOf(NoAuthorityCategoryException.class);
            }
        }

        @Nested
        class 직전_형제와_다음_형제가_연속적이지_않은_경우 {

            @Test
            void 직전_형제와_다음_형제_사이_다른_형제가_있는_경우_예외() {
                // given
                Category first = new Category("first", member, memberBlog);
                Category second = new Category("second", member, memberBlog);
                Category third = new Category("third", member, memberBlog);
                Category target = new Category("target", member, memberBlog);

                second.updateHierarchy(null, first, null, categoryValidator);
                third.updateHierarchy(null, second, null, categoryValidator);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(target, null, first, third);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 직전_형제와_다음_형제의_순서가_바뀐_경우_예외() {
                // given
                Category first = new Category("first", member, memberBlog);
                Category second = new Category("second", member, memberBlog);
                Category target = new Category("target", member, memberBlog);

                second.updateHierarchy(null, first, null, categoryValidator);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(target, null, second, first);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 직전_형제의_직후_형제가_존재하나_명시되지_않은_경우_예외() {
                // given
                Category first = new Category("first", member, memberBlog);
                Category second = new Category("second", member, memberBlog);
                Category target = new Category("target", member, memberBlog);

                second.updateHierarchy(null, first, null, categoryValidator);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(target, null, first, null);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }

            @Test
            void 직후_형제의_직전_형제가_존재하나_명시되지_않은_경우_예외() {
                // given
                Category first = new Category("first", member, memberBlog);
                Category second = new Category("second", member, memberBlog);
                Category target = new Category("target", member, memberBlog);

                second.updateHierarchy(null, first, null, categoryValidator);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(target, null, null, second);
                }).isInstanceOf(CategoryHierarchyViolationException.class)
                        .hasMessage("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.");
            }
        }

        @Nested
        class 부모와_형제의_관계가_올바르지_않은_경우 {

            @Nested
            class 형제들이_모두_주어지지_않은_경우 {

                @Test
                void 부모도_주어지지_않았을_떄_해당_블로그에_다른_카테고리가_존재하면_예외() {
                    // given
                    Category target = new Category("target", member, memberBlog);
                    given(categoryRepository.existsByBlog(memberBlog))
                            .willReturn(true);

                    // when
                    assertThatThrownBy(() -> {
                        categoryValidator.validateUpdateHierarchy(target, null, null, null);
                    }).isInstanceOf(CategoryHierarchyViolationException.class)
                            .hasMessage("블로드에 존재하는 다른 최상위 카테고리와의 관계가 명시되지 않았습니다.");
                }

                @Test
                void 부모가_주어졌을_때_해당_부모의_자식이_존재하는_경우_예외() {
                    // given
                    Category parent = new Category("parent", member, memberBlog);
                    Category next = new Category("next", member, memberBlog);
                    next.updateHierarchy(parent, null, null, categoryValidator);
                    Category target = new Category("target", member, memberBlog);

                    // when & then
                    assertThatThrownBy(() -> {
                        categoryValidator.validateUpdateHierarchy(target, parent, null, null);
                    }).isInstanceOf(CategoryHierarchyViolationException.class)
                            .hasMessage("주어진 부모의 자식 카테고리와의 관계가 명시되지 않았습니다.");
                }
            }

            @Nested
            class 이전_형제가_주어진_경우 {

                @Test
                void 부모는_주어지지_않았는데_이전_형제의_부모가_존재하는_경우_예외() {
                    // given
                    Category parent = new Category("parent", member, memberBlog);
                    Category prev = new Category("prev", member, memberBlog);
                    prev.updateHierarchy(parent, null, null, categoryValidator);
                    Category target = new Category("target", member, memberBlog);

                    // when & then
                    assertThatThrownBy(() -> {
                        categoryValidator.validateUpdateHierarchy(target, null, prev, null);
                    }).isInstanceOf(CategoryHierarchyViolationException.class)
                            .hasMessage("주어진 형제와 부모의 관계가 올바르지 않습니다.");
                }

                @Test
                void 부모가_주어졌을_때_이전_형제의_부모와_주어진_부모와_다른_경우_예외() {
                    // given
                    Category otherParent = new Category("otherParent", member, memberBlog);

                    Category parent = new Category("parent", member, memberBlog);
                    Category prev = new Category("prev", member, memberBlog);
                    prev.updateHierarchy(parent, null, null, categoryValidator);

                    Category target = new Category("target", member, memberBlog);

                    // when & then
                    assertThatThrownBy(() -> {
                        categoryValidator.validateUpdateHierarchy(target, otherParent, prev, null);
                    }).isInstanceOf(CategoryHierarchyViolationException.class)
                            .hasMessage("주어진 형제와 부모의 관계가 올바르지 않습니다.");
                }
            }

            @Nested
            class 다음_형제가_주어진_경우 {

                @Test
                void 부모는_주어지지_않았는데_다음_형제의_부모가_존재하는_경우_예외() {
                    // given
                    Category parent = new Category("parent", member, memberBlog);
                    Category prev = new Category("prev", member, memberBlog);
                    prev.updateHierarchy(parent, null, null, categoryValidator);
                    Category target = new Category("target", member, memberBlog);

                    // when & then
                    assertThatThrownBy(() -> {
                        categoryValidator.validateUpdateHierarchy(target, null, prev, null);
                    }).isInstanceOf(CategoryHierarchyViolationException.class)
                            .hasMessage("주어진 형제와 부모의 관계가 올바르지 않습니다.");
                }

                @Test
                void 부모가_주어졌을_때_다음_형제의_부모와_주어진_부모와_다른_경우_예외() {
                    // given
                    Category parent = new Category("parent", member, memberBlog);
                    Category next = new Category("next", member, memberBlog);
                    next.updateHierarchy(parent, null, null, categoryValidator);

                    Category target = new Category("target", member, memberBlog);

                    // when & then
                    assertThatThrownBy(() -> {
                        categoryValidator.validateUpdateHierarchy(target, next, null, next);
                    }).isInstanceOf(CategoryHierarchyViolationException.class)
                            .hasMessage("주어진 형제와 부모의 관계가 올바르지 않습니다.");
                }
            }
        }

        @Nested
        class 계층구조_참여_시_중복_이름이_존재하게_되는_경우 {

            @Test
            void 이전_형제와_이름이_같으면_예외() {
                // given
                Category parent = new Category("parent", member, memberBlog);
                Category prev = new Category("prev", member, memberBlog);
                Category next = new Category("next", member, memberBlog);
                prev.updateHierarchy(parent, null, null, categoryValidator);
                next.updateHierarchy(parent, prev, null, categoryValidator);

                Category target = new Category("prev", member, memberBlog);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(target, parent, prev, next);
                }).isInstanceOf(DuplicateCategoryNameException.class)
                        .hasMessage("직전 형제 카테고리와 이름이 겹칩니다.");
            }

            @Test
            void 다음_형제와_이름이_같으면_예외() {
                // given
                Category parent = new Category("parent", member, memberBlog);
                Category prev = new Category("prev", member, memberBlog);
                Category next = new Category("next", member, memberBlog);
                prev.updateHierarchy(parent, null, null, categoryValidator);
                next.updateHierarchy(parent, prev, null, categoryValidator);

                Category target = new Category("next", member, memberBlog);

                // when & then
                assertThatThrownBy(() -> {
                    categoryValidator.validateUpdateHierarchy(target, parent, prev, next);
                }).isInstanceOf(DuplicateCategoryNameException.class)
                        .hasMessage("다음 형제 카테고리와 이름이 겹칩니다.");
            }

            @Test
            void 부모와는_이름이_같아도_된다() {
                // given
                Category parent = new Category("parent", member, memberBlog);
                Category prev = new Category("prev", member, memberBlog);
                Category next = new Category("next", member, memberBlog);
                prev.updateHierarchy(parent, null, null, categoryValidator);
                next.updateHierarchy(parent, prev, null, categoryValidator);

                Category target = new Category("parent", member, memberBlog);

                // when & then
                assertDoesNotThrow(() -> {
                    categoryValidator.validateUpdateHierarchy(target, parent, prev, next);
                });
            }
        }
    }
}
