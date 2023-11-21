package com.mallang.post.application;

import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;

import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.DeletePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@ActiveProfiles("test")
@Component
public class PostServiceTestHelper {

    private final PostService postService;
    private final PostRepository postRepository;

    public Long 포스트를_저장한다(Long 회원_ID, String 블로그_이름, String 제목, String 내용, String... 태그들) {
        return 포스트를_저장한다(회원_ID, 블로그_이름, 제목, 내용, new PostVisibilityPolicy(PUBLIC), null, 태그들);
    }

    public Long 포스트를_저장한다(Long 회원_ID, String 블로그_이름, String 제목, String 내용, Long 카테고리_ID, String... 태그들) {
        return 포스트를_저장한다(회원_ID, 블로그_이름, 제목, 내용, new PostVisibilityPolicy(PUBLIC), 카테고리_ID, 태그들);
    }

    public Long 포스트를_저장한다(Long 회원_ID, String 블로그_이름, String 제목, String 내용, PostVisibilityPolicy 공개범위, String... 태그들) {
        return 포스트를_저장한다(회원_ID, 블로그_이름, 제목, 내용, 공개범위, null, 태그들);
    }

    public Long 포스트를_저장한다(
            Long 회원_ID,
            String 블로그_이름,
            String 제목,
            String 내용,
            PostVisibilityPolicy 공개범위,
            Long 카테고리_ID,
            String... 태그들
    ) {
        return postService.create(new CreatePostCommand(
                회원_ID,
                블로그_이름,
                제목,
                내용,
                null,
                내용.substring(0, Math.min(내용.length(), 50)),
                공개범위.getVisibility(),
                공개범위.getPassword(),
                카테고리_ID,
                Arrays.asList(태그들)
        ));
    }

    public void 포스트를_삭제한다(Long memberId, Long postId) {
        postService.delete(new DeletePostCommand(memberId, List.of(postId)));
    }

    public Post 포스트를_조회한다(Long 포스트_ID) {
        return postRepository.getById(포스트_ID);
    }

    public boolean 포스트_존재여부_확인(Long 포스트_ID) {
        return postRepository.existsById(포스트_ID);
    }
}
