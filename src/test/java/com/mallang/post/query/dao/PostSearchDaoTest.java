package com.mallang.post.query.dao;

import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.blog.domain.Blog;
import com.mallang.common.ServiceTest;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.query.dao.PostSearchDao.PostSearchCond;
import com.mallang.post.query.response.PostSearchResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 전체 조회 DAO(PostSearchDao) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostSearchDaoTest extends ServiceTest {

    private Long mallangId;
    private Long otherId;

    @BeforeEach
    void setUp() {
        mallangId = memberServiceTestHelper.회원을_저장한다("말랑");
        otherId = memberServiceTestHelper.회원을_저장한다("other");
        Blog blog = blogServiceTestHelper.블로그_개설(mallangId, "mallang-log");
        Blog otherBlog = blogServiceTestHelper.블로그_개설(otherId, "other-log");
        postServiceTestHelper.포스트를_저장한다(mallangId, blog.getName(),
                "mallang-public", "content",
                new PostVisibilityPolicy(PUBLIC, null));
        postServiceTestHelper.포스트를_저장한다(mallangId, blog.getName(),
                "mallang-protected", "content",
                new PostVisibilityPolicy(PROTECTED, "1234"));
        postServiceTestHelper.포스트를_저장한다(mallangId, blog.getName(),
                "mallang-private", "content",
                new PostVisibilityPolicy(PRIVATE, null));

        postServiceTestHelper.포스트를_저장한다(otherId, otherBlog.getName(),
                "ohter-public", "content",
                new PostVisibilityPolicy(PUBLIC, null));
        postServiceTestHelper.포스트를_저장한다(otherId, otherBlog.getName(),
                "ohter-protected", "content",
                new PostVisibilityPolicy(PROTECTED, "1234"));
        postServiceTestHelper.포스트를_저장한다(otherId, otherBlog.getName(),
                "ohter-private", "content",
                new PostVisibilityPolicy(PRIVATE, null));
    }

    @Test
    void 비공개_포스트인_경우_주인에게만_조회되며_나머지_포스트는_모든_사람이_조회할_수_있다() {
        // when
        List<PostSearchResponse> search = postSearchDao.search(mallangId, new PostSearchCond(
                null, null, null, null,
                null, null, null
        ));

        // then
        assertThat(search)
                .extracting(PostSearchResponse::title)
                .containsExactly("ohter-protected", "ohter-public",
                        "mallang-private", "mallang-protected", "mallang-public"
                );
    }
}
