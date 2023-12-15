package com.mallang.post.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.auth.OauthMemberFixture.깃허브_회원;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.BlogFixture;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.post.application.command.CreateDraftCommand;
import com.mallang.post.application.command.DeleteDraftCommand;
import com.mallang.post.application.command.UpdateDraftCommand;
import com.mallang.post.domain.PostCategory;
import com.mallang.post.domain.PostCategoryRepository;
import com.mallang.post.domain.draft.Draft;
import com.mallang.post.domain.draft.DraftRepository;
import com.mallang.post.exception.NoAuthorityDraftException;
import com.mallang.post.exception.NoAuthorityPostCategoryException;
import com.mallang.post.exception.NotFoundDraftException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("임시 글 서비스 (DraftService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class DraftServiceTest {

    private final BlogRepository blogRepository = mock(BlogRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final PostCategoryRepository postCategoryRepository = mock(PostCategoryRepository.class);
    private final DraftRepository draftRepository = mock(DraftRepository.class);
    private final DraftService draftService = new DraftService(
            blogRepository,
            memberRepository,
            postCategoryRepository,
            draftRepository
    );

    private final Member mallang = 깃허브_말랑(1L);
    private final Member other = 깃허브_회원(2L, "other");
    private final Blog blog = BlogFixture.mallangBlog(1L, mallang);
    private final Blog otherBlog = BlogFixture.blog(2L, other);
    private final PostCategory category = new PostCategory("Spring", mallang, blog);
    private final PostCategory otherCategory = new PostCategory("Spring", other, otherBlog);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(category, "id", 1L);
        ReflectionTestUtils.setField(otherCategory, "id", 2L);
        given(memberRepository.getById(mallang.getId())).willReturn(mallang);
        given(memberRepository.getById(other.getId())).willReturn(other);
        given(blogRepository.getByName(blog.getName())).willReturn(blog);
        given(blogRepository.getByName(otherBlog.getName())).willReturn(otherBlog);
        given(postCategoryRepository.getByIdIfIdNotNull(category.getId())).willReturn(category);
        given(postCategoryRepository.getByIdIfIdNotNull(otherCategory.getId())).willReturn(otherCategory);
        given(postCategoryRepository.getByIdIfIdNotNull(null)).willReturn(null);
    }

    @Nested
    class 임시_글_저장_시 {

        @Test
        void 저장한다() {
            // given
            given(draftRepository.save(any()))
                    .willReturn(mock(Draft.class));
            var command = new CreateDraftCommand(
                    mallang.getId(),
                    blog.getName(),
                    "임시글",
                    "content",
                    "intro",
                    "image",
                    category.getId(),
                    List.of("tag1", "tag2")
            );

            // when
            Long id = draftService.create(command);

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 카테고리와_태그_썸네일는_없어도_된다() {
            // given
            given(draftRepository.save(any()))
                    .willReturn(mock(Draft.class));
            var command = new CreateDraftCommand(
                    mallang.getId(),
                    blog.getName(),
                    "임시글",
                    "content",
                    "intro",
                    null,
                    null,
                    List.of()
            );

            // when
            Long id = draftService.create(command);

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 다른_사람의_블로그에_대한_임시_글_작성시_예외() {
            // given
            var command = new CreateDraftCommand(
                    mallang.getId(),
                    otherBlog.getName(),
                    "임시글",
                    "content",
                    "intro",
                    null,
                    null,
                    List.of()
            );

            // when & then
            assertThatThrownBy(() -> {
                draftService.create(command);
            }).isInstanceOf(NoAuthorityBlogException.class);
        }

        @Test
        void 다른_사람의_카테고리라면_예외() {
            // given
            // given
            var command = new CreateDraftCommand(
                    mallang.getId(),
                    blog.getName(),
                    "임시글",
                    "content",
                    "intro",
                    null,
                    otherCategory.getId(),
                    List.of()
            );

            // when & then
            assertThatThrownBy(() -> {
                draftService.create(command);
            }).isInstanceOf(NoAuthorityPostCategoryException.class);
        }
    }

    @Nested
    class 임시_글_수정_시 {

        private final Draft draft = new Draft(
                blog,
                "title",
                "intro",
                "text",
                "image",
                category,
                List.of("tag1", "tag2"),
                mallang
        );

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(draft, "id", 1L);
            given(draftRepository.getById(draft.getId())).willReturn(draft);
        }

        @Test
        void 내가_쓴_임시_글을_수정할_수_있다() {
            // given
            var command = new UpdateDraftCommand(
                    mallang.getId(),
                    draft.getId(),
                    "수정제목",
                    "수정인트로",
                    "수정내용",
                    "수정썸네일",
                    null,
                    List.of("태그2")
            );

            // when
            draftService.update(command);

            // then
            assertThat(draft.getTitle()).isEqualTo("수정제목");
            assertThat(draft.getBodyText()).isEqualTo("수정내용");
            assertThat(draft.getPostThumbnailImageName()).isEqualTo("수정썸네일");
            assertThat(draft.getPostIntro()).isEqualTo("수정인트로");
            assertThat(draft.getCategory()).isNull();
            assertThat(draft.getTags()).containsExactly("태그2");
        }

        @Test
        void 다른_사람의_임시_글은_수정할_수_없다() {
            // given
            var command = new UpdateDraftCommand(
                    other.getId(),
                    draft.getId(),
                    "수정제목",
                    "수정인트로",
                    "수정내용",
                    "수정썸네일",
                    null,
                    List.of("태그2")
            );

            // when & then
            assertThatThrownBy(() ->
                    draftService.update(command)
            ).isInstanceOf(NoAuthorityDraftException.class);
        }
    }

    @Nested
    class 임시_글_제거_시 {

        private final Draft draft = new Draft(
                blog,
                "title",
                "intro",
                "text",
                "image",
                category,
                List.of("tag1", "tag2"),
                mallang
        );

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(draft, "id", 1L);
            given(draftRepository.getById(draft.getId())).willReturn(draft);
        }

        @Test
        void 자신이_작성한_글이_아닌_경우_예외() {
            // given
            var command = new DeleteDraftCommand(other.getId(), draft.getId());

            // when & then
            assertThatThrownBy(() -> {
                draftService.delete(command);
            }).isInstanceOf(NoAuthorityDraftException.class);
        }

        @Test
        void 없는_임시_글을_제거하려_한다면_예외() {
            // given
            willThrow(NotFoundDraftException.class)
                    .given(draftRepository)
                    .getById(draft.getId() + 1L);
            var command = new DeleteDraftCommand(mallang.getId(), draft.getId() + 1L);

            // when & then
            assertThatThrownBy(() -> {
                draftService.delete(command);
            }).isInstanceOf(NotFoundDraftException.class);
        }

        @Test
        void 임시_글을_제거한다() {
            // given
            var command = new DeleteDraftCommand(mallang.getId(), draft.getId());

            // when & then
            draftService.delete(command);
        }
    }
}
