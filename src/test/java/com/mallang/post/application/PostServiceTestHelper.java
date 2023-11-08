package com.mallang.post.application;

import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import java.util.Arrays;
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

    public Long 포스트를_저장한다(Long 회원_ID, Long 블로그_ID, String 제목, String 내용, String... 태그들) {
        return 포스트를_저장한다(회원_ID, 블로그_ID, 제목, 내용, null, 태그들);
    }

    public Long 포스트를_저장한다(Long 회원_ID, Long 블로그_ID, String 제목, String 내용, Long 카테고리_ID, String... 태그들) {
        return postService.create(new CreatePostCommand(
                회원_ID,
                블로그_ID,
                제목,
                내용,
                카테고리_ID,
                Arrays.asList(태그들)
        ));
    }

    public Post 포스트를_조회한다(Long 포스트_ID) {
        return postRepository.getById(포스트_ID);
    }

    public boolean 포스트_존재여부_확인(Long 포스트_ID) {
        return postRepository.existsById(포스트_ID);
    }
}
