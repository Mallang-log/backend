package com.mallang.post.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.blog.domain.BlogName;
import com.mallang.category.application.CategoryServiceTestHelper;
import com.mallang.common.ServiceTest;
import com.mallang.member.MemberServiceTestHelper;
import com.mallang.post.application.PostServiceTestHelper;
import com.mallang.post.exception.BadPostSearchCondException;
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
    private CategoryServiceTestHelper categoryServiceTestHelper;

    @Autowired
    private PostQueryService postQueryService;

    private Long memberId;
    private BlogName blogName;

    @BeforeEach
    void setUp() {
        memberId = memberServiceTestHelper.회원을_저장한다("말랑");
        blogName = blogServiceTestHelper.블로그_개설(memberId, "mallang");
    }

    @Test
    void 포스트를_조회한다() {
        // given
        Long id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트 1", "content");

        // when
        PostDetailData response = postQueryService.getByBlogNameAndId(blogName, id);

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.writerInfo()).isEqualTo(new WriterDetailInfo(memberId, "말랑", "말랑"));
        assertThat(response.title()).isEqualTo("포스트 1");
        assertThat(response.content()).isEqualTo("content");
        assertThat(response.createdDate()).isNotNull();
    }

    @Test
    void 포스트를_전체_조회한다() {
        // given
        Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트1", "content1");
        Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트2", "content2");
        PostSearchCond cond = PostSearchCond.builder().build();

        // when
        List<PostSimpleData> responses = postQueryService.search(cond);

        // then
        assertThat(responses).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(
                                PostSimpleData.builder()
                                        .id(post1Id)
                                        .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                        .title("포스트1")
                                        .content("content1")
                                        .build(),
                                PostSimpleData.builder()
                                        .id(post2Id)
                                        .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                        .title("포스트2")
                                        .content("content2")
                                        .build()
                        )
                );
    }

    @Test
    void 특정_카테고리의_포스트만_조회한다() {
        // given
        Long 스프링 = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogName, "스프링");
        Long 노드 = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogName, "노드");
        Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트1", "content1", 스프링);
        postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트2", "content2", 노드);
        PostSearchCond cond = PostSearchCond.builder()
                .categoryId(스프링)
                .blogName(blogName.getName())
                .build();

        // when
        List<PostSimpleData> responses = postQueryService.search(cond);

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
        Long 스프링 = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, blogName, "스프링");
        Long JPA = categoryServiceTestHelper.하위_카테고리를_저장한다(memberId, blogName, "JPA", 스프링);
        Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트1", "content1", 스프링);
        Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트2", "content2", JPA);
        PostSearchCond cond = PostSearchCond.builder()
                .categoryId(스프링)
                .blogName(blogName.getName())
                .build();

        // when
        List<PostSimpleData> responses = postQueryService.search(cond);

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
                                        .build(),
                                PostSimpleData.builder()
                                        .id(post2Id)
                                        .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                        .title("포스트2")
                                        .content("content2")
                                        .categoryInfo(new CategorySimpleInfo(JPA, "JPA"))
                                        .build()
                        )
                );
    }

    @Test
    void 특정_태그의_포스트만_조회한다() {
        // given
        Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트1", "content1", "tag1");
        Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트2", "content2", "tag1", "tag2");
        PostSearchCond cond = PostSearchCond.builder()
                .tag("tag2")
                .build();

        // when
        List<PostSimpleData> responses = postQueryService.search(cond);

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
        BlogName otherBlogName = blogServiceTestHelper.블로그_개설(findWriterId, "other");
        Long post1Id = postServiceTestHelper.포스트를_저장한다(findWriterId, otherBlogName, "포스트1", "content1", "tag1");
        Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트2", "content2", "tag1", "tag2");
        PostSearchCond cond = PostSearchCond.builder()
                .writerId(findWriterId)
                .build();

        // when
        List<PostSimpleData> responses = postQueryService.search(cond);

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
        Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트1", "안녕");
        Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트2", "안녕하세요");
        Long post3Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "안녕", "히히");
        PostSearchCond cond = PostSearchCond.builder()
                .title("안녕")
                .build();

        // when
        List<PostSimpleData> responses = postQueryService.search(cond);

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
        Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트1", "안녕");
        Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트2", "안녕하세요");
        Long post3Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "안녕", "히히");
        PostSearchCond cond = PostSearchCond.builder()
                .content("안녕")
                .build();

        // when
        List<PostSimpleData> responses = postQueryService.search(cond);

        // then
        assertThat(responses).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(
                                PostSimpleData.builder()
                                        .id(post1Id)
                                        .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                        .title("포스트1")
                                        .content("안녕")
                                        .build(),
                                PostSimpleData.builder()
                                        .id(post2Id)
                                        .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                        .title("포스트2")
                                        .content("안녕하세요")
                                        .build()
                        )
                );
    }

    @DisplayName("내용 + 제목으로 조회")
    @Test
    void 내용_and_제목으로_조회() {
        // given
        Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트1", "안녕");
        Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "포스트2", "안녕하세요");
        Long post3Id = postServiceTestHelper.포스트를_저장한다(memberId, blogName, "안녕히", "히히");
        PostSearchCond cond = PostSearchCond.builder()
                .titleOrContent("안녕")
                .build();

        // when
        List<PostSimpleData> responses = postQueryService.search(cond);

        // then
        assertThat(responses).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(
                                PostSimpleData.builder()
                                        .id(post1Id)
                                        .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                        .title("포스트1")
                                        .content("안녕")
                                        .build(),
                                PostSimpleData.builder()
                                        .id(post2Id)
                                        .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                        .title("포스트2")
                                        .content("안녕하세요")
                                        .build(),
                                PostSimpleData.builder()
                                        .id(post3Id)
                                        .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                        .title("안녕히")
                                        .content("히히")
                                        .build()
                        )
                );
    }

    @DisplayName("제목이나 내용이 있는데 제목 + 내용도 있다면 오류")
    @Test
    void 제목이나_내용이_있는데_제목_and_내용도_있다면_오류() {
        // given
        PostSearchCond cond = PostSearchCond.builder()
                .title("1")
                .titleOrContent("안녕")
                .build();

        // when & then
        assertThatThrownBy(() ->
                postQueryService.search(cond)
        ).isInstanceOf(BadPostSearchCondException.class);
    }
}
