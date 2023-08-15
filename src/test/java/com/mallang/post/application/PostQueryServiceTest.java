package com.mallang.post.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.category.application.CategoryServiceTestHelper;
import com.mallang.member.MemberServiceTestHelper;
import com.mallang.post.application.query.PostDetailResponse;
import com.mallang.post.application.query.PostDetailResponse.WriterDetailInfo;
import com.mallang.post.application.query.PostSearchCond;
import com.mallang.post.application.query.PostSimpleResponse;
import com.mallang.post.application.query.PostSimpleResponse.CategorySimpleInfo;
import com.mallang.post.application.query.PostSimpleResponse.WriterSimpleInfo;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        PostDetailResponse response = postQueryService.getById(id);

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
        List<PostSimpleResponse> responses = postQueryService.search(new PostSearchCond(null));

        // then
        assertThat(responses).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(
                                PostSimpleResponse.builder()
                                        .id(post1Id)
                                        .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                        .title("포스트1")
                                        .content("content1")
                                        .build(),
                                PostSimpleResponse.builder()
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
        List<PostSimpleResponse> responses = postQueryService.search(new PostSearchCond(스프링));

        // then
        assertThat(responses).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(
                                PostSimpleResponse.builder()
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
        List<PostSimpleResponse> responses = postQueryService.search(new PostSearchCond(스프링));

        // then
        assertThat(responses).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(
                                PostSimpleResponse.builder()
                                        .id(post1Id)
                                        .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                        .title("포스트1")
                                        .content("content1")
                                        .categoryInfo(new CategorySimpleInfo(스프링, "스프링"))
                                        .build(),
                                PostSimpleResponse.builder()
                                        .id(post2Id)
                                        .writerInfo(new WriterSimpleInfo(memberId, "말랑", "말랑"))
                                        .title("포스트2")
                                        .content("content2")
                                        .categoryInfo(new CategorySimpleInfo(JPA, "JPA"))
                                        .build()
                        )
                );
    }
}
