package com.mallang.post.application;

import static com.mallang.member.domain.OauthServerType.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.member.MemberServiceHelper;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.MemberRepository;
import com.mallang.member.domain.OauthId;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.NoAuthorityUpdatePostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("포스트 서비스(PostService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
@SpringBootTest
class PostServiceTest {

    @Autowired
    private MemberServiceHelper memberServiceHelper;

    @Autowired
    private PostServiceTestHelper postServiceTestHelper;

    @Autowired
    private PostService postService;

    @Autowired
    private MemberRepository memberRepository;

    private Long memberId;

    @BeforeEach
    void setUp() {
        memberId = memberRepository.save(Member.builder()
                .oauthId(new OauthId("1", GITHUB))
                .nickname("말랑")
                .profileImageUrl("https://mallang.com")
                .build()
        ).getId();
    }

    @Test
    void 게시글을_저장한다() {
        // given
        CreatePostCommand command = new CreatePostCommand(memberId, "게시글 1", "content");

        // when
        Long id = postService.create(command);

        // then
        assertThat(id).isNotNull();
    }

    @Test
    void 게시글_수정_성공() {
        // given
        Long 말랑_ID = memberServiceHelper.회원을_저장한다("말랑");
        Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(말랑_ID, "게시글", "내용");

        // when
        postService.update(new UpdatePostCommand(말랑_ID, 포스트_ID, "수정제목", "수정내용"));

        // then
        Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
        assertThat(post.getTitle()).isEqualTo("수정제목");
        assertThat(post.getContent()).isEqualTo("수정내용");
    }

    @Test
    void 게시글_수정_실패() {
        // given
        Long 말랑_ID = memberServiceHelper.회원을_저장한다("말랑");
        Long 동훈_ID = memberServiceHelper.회원을_저장한다("동훈");
        Long 포스트_ID = postServiceTestHelper.포스트를_저장한다(말랑_ID, "게시글", "내용");

        // when
        assertThatThrownBy(() ->
                postService.update(new UpdatePostCommand(동훈_ID, 포스트_ID, "수정제목", "수정내용"))
        ).isInstanceOf(NoAuthorityUpdatePostException.class);

        // then
        Post post = postServiceTestHelper.포스트를_조회한다(포스트_ID);
        assertThat(post.getTitle()).isEqualTo("게시글");
        assertThat(post.getContent()).isEqualTo("내용");
    }
}
