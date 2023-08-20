package com.mallang.post.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.category.application.CategoryServiceTestHelper;
import com.mallang.member.MemberServiceTestHelper;
import com.mallang.post.application.PostServiceTestHelper;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.transaction.annotation.Transactional;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@DisplayName("포스트 조회 서비스(PostQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
@SpringBootTest
class PostQueryServiceTest {

    @Autowired
    private PostServiceTestHelper postServiceTestHelper;

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private CategoryServiceTestHelper categoryServiceTestHelper;

    @Autowired
    private PostQueryService postQueryService;

    private Long memberId;

    @BeforeEach
    void setUp() {
        memberId = memberServiceTestHelper.회원을_저장한다("말랑");
    }

    @Test
    void 포스트를_조회한다() {
        // given
        Long id = postServiceTestHelper.포스트를_저장한다(memberId, "포스트 1", "content");

        // when
        PostDetailData response = postQueryService.getById(id);

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
        Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, "포스트1", "content1");
        Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, "포스트2", "content2");

        // when
        List<PostSimpleData> responses = postQueryService.search(new PostSearchCond(null, null, null));

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
        Long 스프링 = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, "스프링");
        Long 노드 = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, "노드");
        Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, "포스트1", "content1", 스프링);
        postServiceTestHelper.포스트를_저장한다(memberId, "포스트2", "content2", 노드);

        // when
        List<PostSimpleData> responses = postQueryService.search(new PostSearchCond(스프링, null, null));

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
        Long 스프링 = categoryServiceTestHelper.최상위_카테고리를_저장한다(memberId, "스프링");
        Long JPA = categoryServiceTestHelper.하위_카테고리를_저장한다(memberId, "JPA", 스프링);
        Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, "포스트1", "content1", 스프링);
        Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, "포스트2", "content2", JPA);

        // when
        List<PostSimpleData> responses = postQueryService.search(new PostSearchCond(스프링, null, null));

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
        Long post1Id = postServiceTestHelper.포스트를_저장한다(memberId, "포스트1", "content1", "tag1");
        Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, "포스트2", "content2", "tag1", "tag2");

        // when
        List<PostSimpleData> responses = postQueryService.search(new PostSearchCond(null, "tag2", null));

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
        Long post1Id = postServiceTestHelper.포스트를_저장한다(findWriterId, "포스트1", "content1", "tag1");
        Long post2Id = postServiceTestHelper.포스트를_저장한다(memberId, "포스트2", "content2", "tag1", "tag2");

        // when
        List<PostSimpleData> responses = postQueryService.search(new PostSearchCond(null, null, findWriterId));

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
}
