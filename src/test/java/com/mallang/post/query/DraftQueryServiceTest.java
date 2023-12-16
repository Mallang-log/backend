package com.mallang.post.query;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.blog.query.repository.BlogQueryRepository;
import com.mallang.post.domain.draft.Draft;
import com.mallang.post.exception.NoAuthorityDraftException;
import com.mallang.post.query.repository.DraftQueryRepository;
import com.mallang.post.query.response.DraftDetailResponse;
import com.mallang.post.query.response.DraftListResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("임시 글 조회 서비스 (DraftQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class DraftQueryServiceTest {

    private final BlogQueryRepository blogQueryRepository = mock(BlogQueryRepository.class);
    private final DraftQueryRepository draftQueryRepository = mock(DraftQueryRepository.class);
    private final MemberQueryRepository memberQueryRepository = mock(MemberQueryRepository.class);
    private final DraftQueryService draftQueryService = new DraftQueryService(
            blogQueryRepository,
            draftQueryRepository,
            memberQueryRepository
    );

    private final Member member = 깃허브_말랑(1L);
    private final Member other = 깃허브_동훈(2L);
    private final Blog blog = mallangBlog(member);

    @BeforeEach
    void setUp() {
        given(memberQueryRepository.getById(member.getId())).willReturn(member);
        given(memberQueryRepository.getById(other.getId())).willReturn(other);
        given(blogQueryRepository.getByName(blog.getName())).willReturn(blog);
    }

    @Nested
    class 임시_글_목록_조회_시 {

        @Test
        void 작성한_임시_글_목록을_조회한다() {
            // given
            Draft draft1 = new Draft(
                    blog,
                    "title1",
                    "intro",
                    "content",
                    null,
                    null,
                    Collections.emptyList(),
                    blog.getOwner()
            );
            Draft draft2 = new Draft(
                    blog,
                    "title2",
                    "intro",
                    "content",
                    null,
                    null,
                    Collections.emptyList(),
                    blog.getOwner()
            );
            given(draftQueryRepository.findAllByBlogOrderByUpdatedDateDesc(blog))
                    .willReturn(List.of(draft2, draft1));

            // when
            List<DraftListResponse> result = draftQueryService.findAllByBlog(member.getId(), blog.getName());

            // then
            assertThat(result)
                    .extracting(DraftListResponse::title)
                    .containsExactly("title2", "title1");
        }

        @Test
        void 블로그_주인이_아니면_예외() {
            // when & then
            assertThatThrownBy(() ->
                    draftQueryService.findAllByBlog(other.getId(), blog.getName())
            ).isInstanceOf(NoAuthorityBlogException.class);
        }
    }

    @Nested
    class 임시_글_단일_조회_시 {

        @Test
        void 단일_조회한다() {
            // given
            Draft draft = new Draft(
                    blog,
                    "title1",
                    "intro",
                    "content",
                    null,
                    null,
                    List.of("1", "2"),
                    blog.getOwner()
            );
            ReflectionTestUtils.setField(draft, "id", 1L);
            given(draftQueryRepository.getById(draft.getId())).willReturn(draft);

            // when
            DraftDetailResponse response = draftQueryService.findById(member.getId(), draft.getId());

            // then
            assertThat(response.draftId()).isEqualTo(1L);
            assertThat(response.title()).isEqualTo("title1");
            assertThat(response.bodyText()).isEqualTo("content");
            assertThat(response.postThumbnailImageName()).isNull();
            assertThat(response.category().categoryId()).isNull();
            assertThat(response.tags().tagContents())
                    .containsExactly("1", "2");
        }

        @Test
        void 작성자가_아니면_예외() {
            // given
            Draft draft = new Draft(
                    blog,
                    "title1",
                    "intro",
                    "content",
                    null,
                    null,
                    List.of("1", "2"),
                    blog.getOwner()
            );
            ReflectionTestUtils.setField(draft, "id", 1L);
            given(draftQueryRepository.getById(draft.getId())).willReturn(draft);

            // when & then
            assertThatThrownBy(() ->
                    draftQueryService.findById(other.getId(), draft.getId())
            ).isInstanceOf(NoAuthorityDraftException.class);
        }
    }
}
