package com.mallang.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.category.CategoryHierarchyViolationException;
import com.mallang.category.ChildCategoryExistException;
import com.mallang.category.DuplicateCategoryNameException;
import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CreateStarGroupCommand;
import com.mallang.post.application.command.DeleteStarGroupCommand;
import com.mallang.post.application.command.UpdateStarGroupHierarchyCommand;
import com.mallang.post.application.command.UpdateStarGroupNameCommand;
import com.mallang.post.domain.star.StarGroup;
import com.mallang.post.exception.NoAuthorityStarGroupException;
import com.mallang.post.exception.NotFoundStarGroupException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("즐겨찾기 그룹 서비스 (StarGroupService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StarGroupServiceTest extends ServiceTest {

    private Long mallangId;
    private Long otherMemberId;

    @BeforeEach
    void setUp() {
        mallangId = 회원을_저장한다("mallang");
        otherMemberId = 회원을_저장한다("동훈");
    }

    @Nested
    class 생성_시 {

        @Test
        void 최상위_그룹으로_생성될_수_있다() {
            // given
            var command = new CreateStarGroupCommand(
                    mallangId,
                    "Spring",
                    null,
                    null,
                    null
            );

            // when
            Long id = starGroupService.create(command);

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 하위_그룹으로_생성될_수_있다() {
            // given
            var createRootCommand = new CreateStarGroupCommand(
                    mallangId,
                    "Spring",
                    null,
                    null,
                    null
            );
            Long rootId = starGroupService.create(createRootCommand);
            var createChildCommand = new CreateStarGroupCommand(
                    mallangId,
                    "JPA",
                    rootId,
                    null,
                    null
            );

            // when
            Long id = starGroupService.create(createChildCommand);

            // then
            StarGroup child = starGroupRepository.getById(id);
            assertThat(child.getParent().getId())
                    .isEqualTo(rootId);
        }

        @Test
        void 이전_형제와_다음_형제들을_지정할_수_있다() {
            // given
            var createRootCommand = new CreateStarGroupCommand(
                    mallangId,
                    "Spring",
                    null,
                    null,
                    null
            );
            Long rootId = starGroupService.create(createRootCommand);
            var createNextRootCommand = new CreateStarGroupCommand(
                    mallangId,
                    "Node",
                    null,
                    rootId,
                    null
            );

            // when
            Long nextId = starGroupService.create(createNextRootCommand);

            // then
            transactionHelper.doAssert(() -> {
                StarGroup next = starGroupRepository.getById(nextId);
                StarGroup prev = starGroupRepository.getById(rootId);
                assertThat(next.getPreviousSibling())
                        .isEqualTo(prev);
                assertThat(prev.getNextSibling())
                        .isEqualTo(next);
            });
        }

        @Test
        void 없는_부모나_형제_그룹으로_생성하려는_경우_예외() {
            // given
            var invalidParentCommand = new CreateStarGroupCommand(
                    mallangId,
                    "Spring",
                    100L,
                    null,
                    null
            );
            var invalidPrevCommand = new CreateStarGroupCommand(
                    mallangId,
                    "Spring",
                    null,
                    100L,
                    null
            );
            var invalidNextCommand = new CreateStarGroupCommand(
                    mallangId,
                    "Spring",
                    null,
                    null,
                    100L
            );

            // when & then
            assertThatThrownBy(() -> {
                starGroupService.create(invalidParentCommand);
            }).isInstanceOf(NotFoundStarGroupException.class);
            assertThatThrownBy(() -> {
                starGroupService.create(invalidPrevCommand);
            }).isInstanceOf(NotFoundStarGroupException.class);
            assertThatThrownBy(() -> {
                starGroupService.create(invalidNextCommand);
            }).isInstanceOf(NotFoundStarGroupException.class);
        }

        @Test
        void 형제_중_중복된_이름이_있으면_예외() {
            // given
            var createFirstCommand = new CreateStarGroupCommand(
                    mallangId,
                    "Spring",
                    null,
                    null,
                    null
            );
            Long firstId = starGroupService.create(createFirstCommand);
            var createNextCommand = new CreateStarGroupCommand(
                    mallangId,
                    "Spring",
                    null,
                    firstId,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.create(createNextCommand)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 다른_사람의_하위_그룹으로_생성되려는_경우_예외() {
            // given
            var createMallangRootCommand = new CreateStarGroupCommand(
                    mallangId,
                    "Spring",
                    null,
                    null,
                    null
            );
            Long mallangRootId = starGroupService.create(createMallangRootCommand);
            var createOtherChildCommand = new CreateStarGroupCommand(
                    otherMemberId,
                    "JPA",
                    mallangRootId,
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.create(createOtherChildCommand)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }

        @Test
        void 다른_사람의_형제_그룹으로_생성되려는_경우_예외() {
            // given
            var createMallangRootCommand = new CreateStarGroupCommand(
                    mallangId,
                    "Spring",
                    null,
                    null,
                    null
            );
            Long mallangRootId = starGroupService.create(createMallangRootCommand);
            var createOtherSiblingCommand = new CreateStarGroupCommand(
                    otherMemberId,
                    "JPA",
                    null,
                    null,
                    mallangRootId
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.create(createOtherSiblingCommand)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }
    }

    @Nested
    class 이름_수정_시 {

        @Test
        void 자신의_그룹이라면_수정_가능() {
            // given
            Long groupId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            var command = new UpdateStarGroupNameCommand(
                    groupId,
                    mallangId,
                    "수정"
            );

            // when
            starGroupService.updateName(command);

            // then
            StarGroup group = starGroupRepository.getById(groupId);
            assertThat(group.getName()).isEqualTo("수정");
        }

        @Test
        void 형제들_중_이름이_중복되면_예외() {
            // given
            Long 최상위 = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long 자식1 = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "하위1",
                    최상위,
                    null,
                    null
            ));
            Long 자식2 = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "하위2",
                    최상위,
                    자식1,
                    null
            ));
            var command = new UpdateStarGroupNameCommand(
                    자식2,
                    mallangId,
                    "하위1"
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateName(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 루트끼리는_이름이_같을_수_없다() {
            // given
            Long 최상위 = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long 최상위2 = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위2",
                    null,
                    최상위,
                    null
            ));
            var command = new UpdateStarGroupNameCommand(
                    최상위2,
                    mallangId,
                    "최상위"
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateName(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 계층구조_수정_시 {

        @Test
        void 자신의_그룹이라면_수정_가능() {
            // given
            Long rootGroupId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childGroupId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "하위",
                    rootGroupId,
                    null,
                    null
            ));
            var command = new UpdateStarGroupHierarchyCommand(
                    childGroupId,
                    mallangId,
                    null,
                    rootGroupId,
                    null
            );

            // when
            starGroupService.updateHierarchy(command);

            // then
            StarGroup group = starGroupRepository.getById(childGroupId);
            assertThat(group.getPreviousSibling().getId()).isEqualTo(rootGroupId);
            assertThat(group.getNextSibling()).isNull();
        }

        @Test
        void 부모_그룹를_제거함으로써_최상위_그룹으로_만들_수_있다() {
            // given
            Long rootGroupId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childGroupId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "하위",
                    rootGroupId,
                    null,
                    null
            ));
            var command = new UpdateStarGroupHierarchyCommand(
                    childGroupId,
                    mallangId,
                    null,
                    rootGroupId,
                    null
            );

            // when
            starGroupService.updateHierarchy(command);

            // then
            StarGroup group = starGroupRepository.getById(childGroupId);
            assertThat(group.getName()).isEqualTo("하위");
            assertThat(group.getParent()).isNull();
        }

        @Test
        void 부모_그룹을_변경할_수_있다() {
            // given
            Long groupId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "하위",
                    groupId,
                    null,
                    null
            ));
            Long descendantId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "더하위",
                    childId,
                    null,
                    null
            ));
            var command = new UpdateStarGroupHierarchyCommand(
                    descendantId,
                    mallangId,
                    groupId,
                    childId,
                    null
            );

            // when
            starGroupService.updateHierarchy(command);

            // then
            transactionHelper.doAssert(() -> {
                StarGroup group = starGroupRepository.getById(groupId);
                assertThat(group.getSortedChildren())
                        .extracting(StarGroup::getName)
                        .containsExactly("하위", "더하위");
            });
        }

        @Test
        void 자신_혹은_자신의_하위_그룹을_자신의_부모로_만드려는_경우_예외() {
            // given
            Long groupId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "하위",
                    groupId,
                    null,
                    null
            ));
            Long descendantId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "더하위",
                    childId,
                    null,
                    null
            ));
            var selfParent = new UpdateStarGroupHierarchyCommand(
                    groupId,
                    mallangId,
                    groupId,
                    null,
                    null
            );
            var childToParent = new UpdateStarGroupHierarchyCommand(
                    groupId,
                    mallangId,
                    childId,
                    null,
                    null
            );
            var descendantToParent = new UpdateStarGroupHierarchyCommand(
                    groupId,
                    mallangId,
                    descendantId,
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(selfParent)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(childToParent)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(descendantToParent)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 자신의_그룹이_아니면_예외() {
            // given
            Long groupId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            var command = new UpdateStarGroupHierarchyCommand(
                    groupId,
                    otherMemberId,
                    null,
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }

        @Test
        void 다른_사람의_그룹의_하위_그룹으로_변경하려는_경우_예외() {
            // given
            Long groupId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long otherGroupId = starGroupService.create(new CreateStarGroupCommand(
                    otherMemberId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            var command = new UpdateStarGroupHierarchyCommand(
                    groupId,
                    mallangId,
                    otherGroupId,
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }

        @Test
        void 다른_사람의_그룹의_형제_그룹으로_변경하려는_경우_예외() {
            // given
            Long groupId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long otherGroupId = starGroupService.create(new CreateStarGroupCommand(
                    otherMemberId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            var command = new UpdateStarGroupHierarchyCommand(
                    groupId,
                    mallangId,
                    null,
                    otherGroupId,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }

        @Test
        void 형제끼리_이름이_겹치면_예외() {
            // given
            Long 최상위 = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long 최상위2 = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "자식1",
                    null,
                    최상위,
                    null
            ));
            Long 자식1 = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "자식1",
                    최상위,
                    null,
                    null
            ));
            var rootToChild = new UpdateStarGroupHierarchyCommand(
                    최상위2,
                    mallangId,
                    최상위,
                    자식1,
                    null
            );
            var childToRoot = new UpdateStarGroupHierarchyCommand(
                    자식1,
                    mallangId,
                    null,
                    최상위,
                    최상위2
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(rootToChild)
            ).isInstanceOf(DuplicateCategoryNameException.class);
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(childToRoot)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 제거_시 {

        @Test
        void 하위_그룹이_있다면_예외() {
            // given
            Long categoryId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            var command = new DeleteStarGroupCommand(
                    mallangId,
                    categoryId
            );

            // when
            assertThatThrownBy(() ->
                    starGroupService.delete(command)
            ).isInstanceOf(ChildCategoryExistException.class);

            // then
            assertThat(starGroupRepository.getById(categoryId)).isNotNull();
        }

        @Test
        void 자신의_그룹이_아니라면_예외() {
            // given
            Long categoryId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            var command = new DeleteStarGroupCommand(otherMemberId, categoryId);

            // when
            assertThatThrownBy(() ->
                    starGroupService.delete(command)
            ).isInstanceOf(NoAuthorityStarGroupException.class);

            // then
            assertThat(starGroupRepository.getById(categoryId)).isNotNull();
        }

        @Test
        void 부모_그룹의_자식에서_제거된다() {
            // given
            Long categoryId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "최상위",
                    null,
                    null,
                    null
            ));
            Long childCategoryId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "하위",
                    categoryId,
                    null,
                    null
            ));
            var command = new DeleteStarGroupCommand(mallangId, childCategoryId);

            // when
            starGroupService.delete(command);

            // then
            assertThatThrownBy(() ->
                    starGroupRepository.getById(childCategoryId)
            ).isInstanceOf(NotFoundStarGroupException.class);
            transactionHelper.doAssert(() ->
                    assertThat(
                            starGroupRepository.getById(categoryId).getSortedChildren()).isEmpty()
            );
        }

        @Test
        void 이전_그룹과_다음_그룹은_이어진다() {
            // given
            Long firstId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "1",
                    null,
                    null,
                    null
            ));
            Long secondId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "2",
                    null,
                    firstId,
                    null
            ));
            Long thirdId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "3",
                    null,
                    secondId,
                    null
            ));
            Long forthId = starGroupService.create(new CreateStarGroupCommand(
                    mallangId,
                    "4",
                    null,
                    thirdId,
                    null
            ));
            var command = new DeleteStarGroupCommand(
                    mallangId,
                    thirdId
            );

            // when
            starGroupService.delete(command);

            // then
            transactionHelper.doAssert(() -> {
                StarGroup second = starGroupRepository.getById(secondId);
                StarGroup forth = starGroupRepository.getById(forthId);
                assertThat(second.getNextSibling()).isEqualTo(forth);
                assertThat(forth.getPreviousSibling()).isEqualTo(second);

            });
        }

        @Test
        void 해당_그룹에_속한_즐겨찾기된_포스트들을_그룹_없음으로_만든다() {
            // TODO
        }
    }
}
