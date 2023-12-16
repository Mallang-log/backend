package com.mallang.post.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.auth.OauthMemberFixture.깃허브_회원;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static com.mallang.post.PostFixture.privatePost;
import static com.mallang.post.PostFixture.publicPost;
import static com.mallang.post.StarGroupFixture.starGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.category.CategoryHierarchyViolationException;
import com.mallang.category.ChildCategoryExistException;
import com.mallang.category.DuplicateCategoryNameException;
import com.mallang.post.application.command.CreateStarGroupCommand;
import com.mallang.post.application.command.DeleteStarGroupCommand;
import com.mallang.post.application.command.UpdateStarGroupHierarchyCommand;
import com.mallang.post.application.command.UpdateStarGroupNameCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.star.PostStar;
import com.mallang.post.domain.star.PostStarRepository;
import com.mallang.post.domain.star.StarGroup;
import com.mallang.post.domain.star.StarGroupRepository;
import com.mallang.post.domain.star.StarGroupValidator;
import com.mallang.post.exception.NoAuthorityStarGroupException;
import com.mallang.post.exception.NotFoundPostCategoryException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("즐겨찾기 그룹 서비스 (StarGroupService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class StarGroupServiceTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final PostStarRepository postStarRepository = mock(PostStarRepository.class);
    private final StarGroupRepository starGroupRepository = mock(StarGroupRepository.class);
    private final StarGroupValidator starGroupValidator = mock(StarGroupValidator.class);
    private final StarGroupService starGroupService = new StarGroupService(
            memberRepository,
            postStarRepository,
            starGroupRepository,
            starGroupValidator
    );

    private final Member mallang = 깃허브_말랑(1L);
    private final Member other = 깃허브_회원(2L, "other");

    @BeforeEach
    void setUp() {
        given(memberRepository.getById(mallang.getId())).willReturn(mallang);
        given(memberRepository.getById(other.getId())).willReturn(other);
        given(starGroupRepository.getByIdIfIdNotNull(null)).willReturn(null);
        given(starGroupRepository.save(any())).willReturn(mock(StarGroup.class));
    }

    @Nested
    class 생성_시 {

        @Test
        void 그룹을_저장한다() {
            // given
            var command = new CreateStarGroupCommand(
                    mallang.getId(),
                    "최상위 그룹",
                    null,
                    null,
                    null
            );

            // when
            Long id = starGroupService.create(command);

            // then
            then(starGroupRepository)
                    .should(times(1))
                    .save(any());
        }

        @Test
        void 이미_그룹이_존재하는데_이와의_관계를_명시하지_않으면_예외() {
            // given
            willThrow(CategoryHierarchyViolationException.class)
                    .given(starGroupValidator)
                    .validateNoCategories(any());
            var command = new CreateStarGroupCommand(
                    mallang.getId(),
                    "최상위 카테고리",
                    null,
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() -> {
                starGroupService.create(command);
            }).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 계층형으로_저장할_수_있다() {
            // given
            var parent = starGroup(1L, "root", mallang);
            var prev = starGroup(2L, "prev", mallang);
            var next = starGroup(3L, "next", mallang);
            prev.updateHierarchy(parent, null, null);
            next.updateHierarchy(parent, prev, null);
            given(starGroupRepository.getByIdIfIdNotNull(parent.getId())).willReturn(parent);
            given(starGroupRepository.getByIdIfIdNotNull(prev.getId())).willReturn(prev);
            given(starGroupRepository.getByIdIfIdNotNull(next.getId())).willReturn(next);
            var command = new CreateStarGroupCommand(
                    mallang.getId(),
                    "prev 와 next 차이 parent 자식",
                    parent.getId(),
                    prev.getId(),
                    next.getId()
            );

            // when
            Long id = starGroupService.create(command);

            // then
            then(starGroupRepository)
                    .should(times(1))
                    .save(any());
            assertThat(parent.getChildren()).hasSize(3);
            assertThat(prev.getNextSibling()).isNotEqualTo(next);
            assertThat(next.getPreviousSibling()).isNotEqualTo(prev);
            var saved = prev.getNextSibling();
            assertThat(saved).isEqualTo(next.getPreviousSibling());
            assertThat(saved.getParent()).isEqualTo(parent);
        }

        @Test
        void 없는_부모나_형제_그룹_ID를_설정한_경우_예외() {
            // given
            given(starGroupRepository.getByIdIfIdNotNull(100L))
                    .willThrow(NotFoundPostCategoryException.class);
            var command1 = new CreateStarGroupCommand(
                    mallang.getId(),
                    "name",
                    100L,
                    null,
                    null
            );
            var command2 = new CreateStarGroupCommand(
                    mallang.getId(),
                    "name",
                    100L,
                    null,
                    null
            );
            var command3 = new CreateStarGroupCommand(
                    mallang.getId(),
                    "name",
                    100L,
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.create(command1)
            ).isInstanceOf(NotFoundPostCategoryException.class);
            assertThatThrownBy(() ->
                    starGroupService.create(command2)
            ).isInstanceOf(NotFoundPostCategoryException.class);
            assertThatThrownBy(() ->
                    starGroupService.create(command3)
            ).isInstanceOf(NotFoundPostCategoryException.class);
        }

        @Test
        void 다른_사람의_하위_그룹으로_생성되려는_경우_예외() {
            // given
            var parent = starGroup(1L, "root", other);
            given(starGroupRepository.getByIdIfIdNotNull(parent.getId())).willReturn(parent);
            var command = new CreateStarGroupCommand(
                    mallang.getId(),
                    "name",
                    parent.getId(),
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.create(command)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }

        @Test
        void 다른_사람의_형제_그룹으로_생성되려는_경우_예외() {
            // given
            var prev = starGroup(1L, "prev", other);
            given(starGroupRepository.getByIdIfIdNotNull(prev.getId())).willReturn(prev);
            var command1 = new CreateStarGroupCommand(
                    mallang.getId(),
                    "name",
                    null,
                    prev.getId(),
                    null
            );
            var command2 = new CreateStarGroupCommand(
                    mallang.getId(),
                    "name",
                    null,
                    null,
                    prev.getId()
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.create(command1)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
            assertThatThrownBy(() ->
                    starGroupService.create(command2)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }

        @Test
        void 형제끼리는_이름이_같을_수_없다() {
            // given
            var parent = starGroup(1L, "same name", mallang);
            given(starGroupRepository.getByIdIfIdNotNull(parent.getId())).willReturn(parent);
            var command1 = new CreateStarGroupCommand(
                    mallang.getId(),
                    "same name",
                    null,
                    parent.getId(),
                    null
            );
            var command2 = new CreateStarGroupCommand(
                    mallang.getId(),
                    "same name",
                    null,
                    null,
                    parent.getId()
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.create(command1)
            ).isInstanceOf(DuplicateCategoryNameException.class);
            assertThatThrownBy(() ->
                    starGroupService.create(command2)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 이름_수정_시 {

        private final StarGroup starGroup = starGroup(1L, "spring", mallang);

        @BeforeEach
        void setUp() {
            given(starGroupRepository.getById(starGroup.getId())).willReturn(starGroup);
        }

        @Test
        void 다른_사람의_카테고리는_수정할_수_없다() {
            // given
            var command = new UpdateStarGroupNameCommand(
                    starGroup.getId(),
                    other.getId(),
                    "수정"
            );

            // when & then
            assertThatThrownBy(() -> {
                starGroupService.updateName(command);
            }).isInstanceOf(NoAuthorityStarGroupException.class);
        }

        @Test
        void 자신의_카테고리라면_수정_가능() {
            // given
            var command = new UpdateStarGroupNameCommand(
                    starGroup.getId(),
                    mallang.getId(),
                    "수정"
            );

            // when
            starGroupService.updateName(command);

            // then
            assertThat(starGroup.getName()).isEqualTo("수정");
        }

        @Test
        void 형제끼리는_이름이_같을_수_없다() {
            // given
            StarGroup next = new StarGroup("next", mallang);
            next.updateHierarchy(null, starGroup, null);
            var command = new UpdateStarGroupNameCommand(
                    starGroup.getId(),
                    mallang.getId(),
                    "next"
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateName(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 계층구조_수정_시 {

        private final StarGroup parent = starGroup(1L, "parent", mallang);
        private final StarGroup prev = starGroup(2L, "prev", mallang);
        private final StarGroup next = starGroup(3L, "next", mallang);

        @BeforeEach
        void setUp() {
            prev.updateHierarchy(parent, null, null);
            next.updateHierarchy(parent, prev, null);
            given(starGroupRepository.getById(parent.getId())).willReturn(parent);
            given(starGroupRepository.getById(prev.getId())).willReturn(prev);
            given(starGroupRepository.getById(next.getId())).willReturn(next);
            given(starGroupRepository.getByIdIfIdNotNull(parent.getId())).willReturn(parent);
            given(starGroupRepository.getByIdIfIdNotNull(prev.getId())).willReturn(prev);
            given(starGroupRepository.getByIdIfIdNotNull(next.getId())).willReturn(next);
            given(starGroupRepository.getByIdIfIdNotNull(null)).willReturn(null);
        }

        @Test
        void 자신의_카테고리라면_수정_가능() {
            // given
            var command = new UpdateStarGroupHierarchyCommand(
                    prev.getId(),
                    mallang.getId(),
                    next.getId(),
                    null,
                    null
            );

            // when
            starGroupService.updateHierarchy(command);

            // then
            assertThat(prev.getParent()).isEqualTo(next);
            assertThat(prev.getNextSibling()).isNull();
            assertThat(next.getPreviousSibling()).isNull();
            assertThat(next.getChildren())
                    .containsExactly(prev);
        }

        @Test
        void 자신의_카테고리가_아니면_예외() {
            // given
            var command = new UpdateStarGroupHierarchyCommand(
                    prev.getId(),
                    other.getId(),
                    next.getId(),
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }

        @Test
        void 자신_혹은_자신의_하위_카테고리를_자신의_부모로_만드려는_경우_예외() {
            // given
            var command = new UpdateStarGroupHierarchyCommand(
                    parent.getId(),
                    mallang.getId(),
                    next.getId(),
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(command)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 다른_사람의_카테고리의_하위_카테고리로_변경하려는_경우_예외() {
            // given
            StarGroup otherGroup = starGroup(4L, "other", other);
            given(starGroupRepository.getByIdIfIdNotNull(otherGroup.getId())).willReturn(otherGroup);
            var command = new UpdateStarGroupHierarchyCommand(
                    prev.getId(),
                    other.getId(),
                    otherGroup.getId(),
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }

        @Test
        void 다른_사람의_카테고리의_형제_카테고리로_변경하려는_경우_예외() {
            // given
            StarGroup otherGroup = starGroup(4L, "other", other);
            given(starGroupRepository.getByIdIfIdNotNull(otherGroup.getId())).willReturn(otherGroup);
            var command = new UpdateStarGroupHierarchyCommand(
                    prev.getId(),
                    other.getId(),
                    null,
                    null,
                    otherGroup.getId()
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(command)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }

        @Test
        void 변경_시_이름이_겹치는_형제가_있으면_예외() {
            // given
            StarGroup last = starGroup(5L, "parent", mallang);
            last.updateHierarchy(parent, next, null);
            given(starGroupRepository.getById(last.getId())).willReturn(last);
            var command = new UpdateStarGroupHierarchyCommand(
                    last.getId(),
                    mallang.getId(),
                    null,
                    parent.getId(),
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.updateHierarchy(command)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 제거_시 {

        private final StarGroup parent = starGroup(1L, "parent", mallang);
        private final StarGroup prev = starGroup(2L, "prev", mallang);
        private final StarGroup next = starGroup(3L, "next", mallang);

        @BeforeEach
        void setUp() {
            prev.updateHierarchy(parent, null, null);
            next.updateHierarchy(parent, prev, null);
            given(starGroupRepository.getById(parent.getId())).willReturn(parent);
            given(starGroupRepository.getById(prev.getId())).willReturn(prev);
            given(starGroupRepository.getById(next.getId())).willReturn(next);
            given(starGroupRepository.getByIdIfIdNotNull(parent.getId())).willReturn(parent);
            given(starGroupRepository.getByIdIfIdNotNull(prev.getId())).willReturn(prev);
            given(starGroupRepository.getByIdIfIdNotNull(next.getId())).willReturn(next);
            given(starGroupRepository.getByIdIfIdNotNull(null)).willReturn(null);
        }

        @Test
        void 하위_카테고리가_있다면_예외() {
            // given
            var command = new DeleteStarGroupCommand(
                    mallang.getId(),
                    parent.getId()
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.delete(command)
            ).isInstanceOf(ChildCategoryExistException.class);
        }

        @Test
        void 자신의_카테고리가_아니라면_예외() {
            // given
            var command = new DeleteStarGroupCommand(
                    other.getId(),
                    prev.getId()
            );

            // when & then
            assertThatThrownBy(() ->
                    starGroupService.delete(command)
            ).isInstanceOf(NoAuthorityStarGroupException.class);
        }

        @Test
        void 부모_카테고리의_자식에서_제거된다() {
            // given
            var command = new DeleteStarGroupCommand(
                    mallang.getId(),
                    prev.getId()
            );

            // when
            starGroupService.delete(command);

            // then
            then(starGroupRepository)
                    .should(times(1))
                    .delete(prev);
            assertThat(parent.getChildren()).doesNotContain(prev);
            assertThat(next.getPreviousSibling()).isNull();
        }

        @Test
        void 이전_카테고리와_다음_카테고리는_이어진다() {
            // given
            StarGroup last = starGroup(5L, "last", mallang);
            last.updateHierarchy(parent, next, null);
            var command = new DeleteStarGroupCommand(
                    mallang.getId(),
                    next.getId()
            );

            // when
            starGroupService.delete(command);

            // then
            then(starGroupRepository)
                    .should(times(1))
                    .delete(next);
            assertThat(prev.getNextSibling()).isEqualTo(last);
            assertThat(last.getPreviousSibling()).isEqualTo(prev);
            assertThat(next.getNextSibling()).isNull();
            assertThat(next.getPreviousSibling()).isNull();
            assertThat(parent.getChildren()).doesNotContain(next);
        }

        @Test
        void 해당_그룹에_속한_즐겨찾기된_포스트들을_그룹_없음으로_만든다() {
            // given
            Blog blog = mallangBlog(1L, mallang);
            Post post1 = publicPost(1L, blog);
            Post post2 = privatePost(2L, blog);
            PostStar postStar1 = new PostStar(post1, mallang);
            PostStar postStar2 = new PostStar(post2, mallang);
            postStar1.updateGroup(prev);
            postStar2.updateGroup(prev);
            given(postStarRepository.findAllByStarGroup(prev))
                    .willReturn(List.of(postStar1, postStar2));
            var command = new DeleteStarGroupCommand(
                    mallang.getId(),
                    prev.getId()
            );

            // when
            starGroupService.delete(command);

            // then
            then(starGroupRepository)
                    .should(times(1))
                    .delete(prev);
            assertThat(postStar1.getStarGroup()).isNull();
            assertThat(postStar1.getStarGroup()).isNull();
        }
    }
}
