package com.mallang.post.query;

import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.auth.MemberServiceTestHelper;
import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.blog.domain.Blog;
import com.mallang.category.application.CategoryServiceTestHelper;
import com.mallang.common.ServiceTest;
import com.mallang.post.application.PostLikeService;
import com.mallang.post.application.PostServiceTestHelper;
import com.mallang.post.application.command.ClickPostLikeCommand;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.BadPostSearchCondException;
import com.mallang.post.exception.IncorrectAccessPostException;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import com.mallang.post.query.data.PostDetailData;
import com.mallang.post.query.data.PostDetailData.WriterDetailInfo;
import com.mallang.post.query.data.PostSearchCond;
import com.mallang.post.query.data.PostSimpleData;
import com.mallang.post.query.data.PostSimpleData.CategorySimpleInfo;
import com.mallang.post.query.data.PostSimpleData.TagSimpleInfos;
import com.mallang.post.query.data.PostSimpleData.WriterSimpleInfo;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("포스트 조회 서비스(PostQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class PostQueryServiceTest {

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private BlogServiceTestHelper blogServiceTestHelper;

    @Autowired
    private PostServiceTestHelper postServiceTestHelper;

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private CategoryServiceTestHelper categoryServiceTestHelper;

    @Autowired
    private PostQueryService postQueryService;

    private Long memberId;
    private Long blogId;

    @BeforeEach
    void setUp() {
        memberId = memberServiceTestHelper.회원을_저장한다("말랑");
        blogId = blogServiceTestHelper.블로그_개설후_ID_반환(memberId, "mallang");
    }

    @Nested
    class 포스트_단일_조회_시 {

        @Test
        void 포스트를_조회한다() {
            // given
            Long id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트 1", "content");

            // when
            PostDetailData response = postQueryService.getById(null, id);

            // then
            assertThat(response.id()).isEqualTo(id);
            assertThat(response.writerInfo()).isEqualTo(new WriterDetailInfo(memberId, "말랑", "말랑"));
            assertThat(response.title()).isEqualTo("포스트 1");
            assertThat(response.content()).isEqualTo("content");
            assertThat(response.createdDate()).isNotNull();
        }

        @Test
        void 좋아요_눌렀는지_여부가_반영된다() {
            // given
            Long id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트 1", "content");
            postLikeService.click(new ClickPostLikeCommand(id, memberId, null));

            // when
            PostDetailData responseClickLike = postQueryService.getById(memberId, id);
            PostDetailData responseNoLike = postQueryService.getById(null, id);

            // then
            assertThat(responseClickLike.isLiked()).isTrue();
            assertThat(responseNoLike.isLiked()).isFalse();
        }

        @Test
        void 블로그_주인은_비공개_글을_볼_수_있다() {
            // given
            Long id = postServiceTestHelper.포스트를_저장한다(memberId, blogId,
                    "포스트 1", "content",
                    new PostVisibilityPolicy(Visibility.PRIVATE, null));

            // when
            PostDetailData response = postQueryService.getById(memberId, id);

            // then
            assertThat(response.id()).isEqualTo(id);
            assertThat(response.writerInfo()).isEqualTo(new WriterDetailInfo(memberId, "말랑", "말랑"));
            assertThat(response.title()).isEqualTo("포스트 1");
            assertThat(response.content()).isEqualTo("content");
            assertThat(response.createdDate()).isNotNull();
        }

        @Test
        void 블로그_주인이_아니라면_비공개_글_조회시_예외() {
            // given
            Long id = postServiceTestHelper.포스트를_저장한다(memberId, blogId,
                    "포스트 1", "content",
                    new PostVisibilityPolicy(Visibility.PRIVATE, null));

            // when & then
            assertThatThrownBy(() ->
                    postQueryService.getById(memberId + 1, id)
            ).isInstanceOf(NoAuthorityAccessPostException.class);
        }

        @Test
        void 블로그_주인은_보호글을_볼_수_있다() {
            // given
            Long id = postServiceTestHelper.포스트를_저장한다(memberId, blogId,
                    "포스트 1", "content",
                    new PostVisibilityPolicy(Visibility.PROTECTED, "1234"));

            // when
            PostDetailData response = postQueryService.getById(memberId, id);

            // then
            assertThat(response.id()).isEqualTo(id);
            assertThat(response.writerInfo()).isEqualTo(new WriterDetailInfo(memberId, "말랑", "말랑"));
            assertThat(response.title()).isEqualTo("포스트 1");
            assertThat(response.content()).isEqualTo("content");
            assertThat(response.createdDate()).isNotNull();
        }

        @Test
        void 블로그_주인이_아닌_경우_보호글_조회시_내용이_보호된다() {
            // given
            Long id = postServiceTestHelper.포스트를_저장한다(memberId, blogId,
                    "포스트 1", "content",
                    new PostVisibilityPolicy(Visibility.PROTECTED, "1234"));

            // when
            PostDetailData response = postQueryService.getById(memberId + 1, id);

            // then
            assertThat(response.id()).isEqualTo(id);
            assertThat(response.writerInfo()).isEqualTo(new WriterDetailInfo(memberId, "말랑", "말랑"));
            assertThat(response.title()).isEqualTo("포스트 1");
            assertThat(response.content()).isEqualTo("보호되어 있는 글입니다. 내용을 보시려면 비밀번호를 입력하세요.");
            assertThat(response.createdDate()).isNotNull();
        }
    }

    @Nested
    class 보호된_포스트_조회_시 {

        @Test
        void 보호된_포스트가_아니라면_예외() {
            // given
            Long id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트 1", "content");

            // when & then
            assertThatThrownBy(() ->
                    postQueryService.getProtectedById(null, id, "1234")
            ).isInstanceOf(IncorrectAccessPostException.class);
        }

        @Test
        void 비밀번호가_일치하지_않으면_조회할_수_없다() {
            // given
            Long id = postServiceTestHelper.포스트를_저장한다(memberId, blogId,
                    "포스트 1", "content",
                    new PostVisibilityPolicy(Visibility.PROTECTED, "1234"));

            // when & then
            assertThatThrownBy(() ->
                    postQueryService.getProtectedById(null, id, "134")
            ).isInstanceOf(NoAuthorityAccessPostException.class);
        }

        @Test
        void 비밀번호가_일치하면_조회된다() {
            // given
            Long id = postServiceTestHelper.포스트를_저장한다(memberId, blogId,
                    "포스트 1", "content",
                    new PostVisibilityPolicy(Visibility.PROTECTED, "1234"));

            // when
            PostDetailData response = postQueryService.getProtectedById(null, id, "1234");

            // then
            assertThat(response.id()).isEqualTo(id);
            assertThat(response.writerInfo()).isEqualTo(new WriterDetailInfo(memberId, "말랑", "말랑"));
            assertThat(response.title()).isEqualTo("포스트 1");
            assertThat(response.content()).isEqualTo("content");
            assertThat(response.createdDate()).isNotNull();
        }
    }

    @Nested
    class 포스트_검색_시 {

        @Test
        void 포스트를_전체_조회한다() {
            // given
            Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트1", "content1");
            Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트2", "content2");
            PostSearchCond cond = PostSearchCond.builder().build();

            // when
            List<PostSimpleData> responses = postQueryService.search(null, cond);

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSimpleData.builder()
                                            .id(post2Id)
                                            .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                            .title("포스트2")
                                            .content("content2")
                                            .build(),
                                    PostSimpleData.builder()
                                            .id(post1Id)
                                            .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                            .title("포스트1")
                                            .content("content1")
                                            .build()
                            )
                    );
        }

        @Test
        void 비공개_포스트인_경우_주인에게만_조회되며_나머지_포스트는_모든_사람이_조회할_수_있다() {
            // given
            Long mallangId = memberServiceTestHelper.회원을_저장한다("말랑");
            Long otherId = memberServiceTestHelper.회원을_저장한다("other");
            Blog blog = blogServiceTestHelper.블로그_개설(mallangId, "mallang-log");
            Blog otherBlog = blogServiceTestHelper.블로그_개설(otherId, "other-log");
            postServiceTestHelper.포스트를_저장한다(mallangId, blog.getId(),
                    "mallang-public", "mallang-public",
                    new PostVisibilityPolicy(PUBLIC, null));
            postServiceTestHelper.포스트를_저장한다(mallangId, blog.getId(),
                    "mallang-protected", "mallang-protected",
                    new PostVisibilityPolicy(PROTECTED, "1234"));
            postServiceTestHelper.포스트를_저장한다(mallangId, blog.getId(),
                    "mallang-private", "mallang-private",
                    new PostVisibilityPolicy(PRIVATE, null));

            postServiceTestHelper.포스트를_저장한다(otherId, otherBlog.getId(),
                    "ohter-public", "ohter-public",
                    new PostVisibilityPolicy(PUBLIC, null));
            postServiceTestHelper.포스트를_저장한다(otherId, otherBlog.getId(),
                    "ohter-protected", "ohter-protected",
                    new PostVisibilityPolicy(PROTECTED, "1234"));
            postServiceTestHelper.포스트를_저장한다(otherId, otherBlog.getId(),
                    "ohter-private", "ohter-private",
                    new PostVisibilityPolicy(PRIVATE, null));

            // when
            List<PostSimpleData> search = postQueryService.search(mallangId, new PostSearchCond(
                    null, null, null, null,
                    null, null, null
            ));

            // then
            assertThat(search)
                    .extracting(PostSimpleData::title)
                    .containsExactly("ohter-protected", "ohter-public",
                            "mallang-private", "mallang-protected", "mallang-public");
            assertThat(search)
                    .extracting(PostSimpleData::content)
                    .containsExactly("보호되어 있는 글입니다.", "ohter-public",
                            "mallang-private", "mallang-protected", "mallang-public");
        }

        @Test
        void 특정_카테고리의_포스트만_조회한다() {
            // given
            Long 스프링 = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogId, "스프링");
            Long 노드 = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogId, "노드");
            Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트1", "content1", 스프링);
            postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트2", "content2", 노드);
            PostSearchCond cond = PostSearchCond.builder()
                    .categoryId(스프링)
                    .blogId(blogId)
                    .build();

            // when
            List<PostSimpleData> responses = postQueryService.search(null, cond);

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSimpleData.builder()
                                            .id(post1Id)
                                            .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                            .title("포스트1")
                                            .content("content1")
                                            .categoryInfo(new CategorySimpleInfo(스프링, "스프링"))
                                            .build()
                            )
                    );
        }

        @Test
        void 최상위_카테고리로_조회_시_하위_카테고리도_포함되면_조회한다() {
            // given
            Long 스프링 = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogId, "스프링");
            Long JPA = categoryServiceTestHelper.하위_카테고리를_저장한다(memberId, blogId, "JPA", 스프링);
            Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트1", "content1", 스프링);
            Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트2", "content2", JPA);
            PostSearchCond cond = PostSearchCond.builder()
                    .categoryId(스프링)
                    .blogId(blogId)
                    .build();

            // when
            List<PostSimpleData> responses = postQueryService.search(null, cond);

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSimpleData.builder()
                                            .id(post2Id)
                                            .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                            .title("포스트2")
                                            .content("content2")
                                            .categoryInfo(new CategorySimpleInfo(JPA, "JPA"))
                                            .build(),
                                    PostSimpleData.builder()
                                            .id(post1Id)
                                            .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                            .title("포스트1")
                                            .content("content1")
                                            .categoryInfo(new CategorySimpleInfo(스프링, "스프링"))
                                            .build()
                            )
                    );
        }

        @Test
        void 특정_태그의_포스트만_조회한다() {
            // given
            Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트1", "content1", "tag1");
            Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트2", "content2", "tag1", "tag2");
            PostSearchCond cond = PostSearchCond.builder()
                    .tag("tag2")
                    .build();

            // when
            List<PostSimpleData> responses = postQueryService.search(null, cond);

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSimpleData.builder()
                                            .id(post2Id)
                                            .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                            .title("포스트2")
                                            .content("content2")
                                            .tagSimpleInfos(new TagSimpleInfos(List.of("tag1", "tag2")))
                                            .build()
                            )
                    );
        }

        @Test
        void 특정_작성자의_포스트만_조회한다() {
            // given
            Long findWriterId = memberServiceTestHelper.회원을_저장한다("말랑말랑");
            Long otherBlogId = blogServiceTestHelper.블로그_개설후_ID_반환(findWriterId, "other");
            Long post1Id = postServiceTestHelper.포스트를_저장한다(findWriterId, otherBlogId, "포스트1", "content1", "tag1");
            Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트2", "content2", "tag1", "tag2");
            PostSearchCond cond = PostSearchCond.builder()
                    .writerId(findWriterId)
                    .build();

            // when
            List<PostSimpleData> responses = postQueryService.search(null, cond);

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSimpleData.builder()
                                            .id(post1Id)
                                            .writerInfo(new WriterSimpleInfo(findWriterId, "말랑말랑", "말랑말랑"))
                                            .title("포스트1")
                                            .content("content1")
                                            .tagSimpleInfos(new TagSimpleInfos(List.of("tag1")))
                                            .build()
                            )
                    );
        }

        @Test
        void 제목으로_조회() {
            // given
            Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트1", "안녕");
            Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트2", "안녕하세요");
            Long post3Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "안녕", "히히");
            PostSearchCond cond = PostSearchCond.builder()
                    .title("안녕")
                    .build();

            // when
            List<PostSimpleData> responses = postQueryService.search(null, cond);

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSimpleData.builder()
                                            .id(post3Id)
                                            .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                            .title("안녕")
                                            .content("히히")
                                            .build()
                            )
                    );
        }

        @Test
        void 내용으로_조회() {
            // given
            Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트1", "안녕");
            Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트2", "안녕하세요");
            Long post3Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "안녕", "히히");
            PostSearchCond cond = PostSearchCond.builder()
                    .content("안녕")
                    .build();

            // when
            List<PostSimpleData> responses = postQueryService.search(null, cond);

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSimpleData.builder()
                                            .id(post2Id)
                                            .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                            .title("포스트2")
                                            .content("안녕하세요")
                                            .build(),
                                    PostSimpleData.builder()
                                            .id(post1Id)
                                            .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                            .title("포스트1")
                                            .content("안녕")
                                            .build()
                            )
                    );
        }

        @DisplayName("내용 + 제목으로 조회")
        @Test
        void 내용_and_제목으로_조회() {
            // given
            Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트1", "안녕");
            Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "포스트2", "안녕하세요");
            Long post3Id = postServiceTestHelper.포스트를_저장한다(memberId, blogId, "안녕히", "히히");
            PostSearchCond cond = PostSearchCond.builder()
                    .titleOrContent("안녕")
                    .build();

            // when
            List<PostSimpleData> responses = postQueryService.search(null, cond);

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSimpleData.builder()
                                            .id(post3Id)
                                            .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                            .title("안녕히")
                                            .content("히히")
                                            .build(),
                                    PostSimpleData.builder()
                                            .id(post2Id)
                                            .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                            .title("포스트2")
                                            .content("안녕하세요")
                                            .build(),
                                    PostSimpleData.builder()
                                            .id(post1Id)
                                            .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                            .title("포스트1")
                                            .content("안녕")
                                            .build()
                            )
                    );
        }

        @DisplayName("제목이나 내용이 있는데 제목 + 내용도 있다면 예외")
        @Test
        void 제목이나_내용이_있는데_제목_and_내용도_있다면_예외() {
            // given
            PostSearchCond cond = PostSearchCond.builder()
                    .title("1")
                    .titleOrContent("안녕")
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postQueryService.search(null, cond)
            ).isInstanceOf(BadPostSearchCondException.class);
        }
    }
}
