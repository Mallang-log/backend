package com.mallang.post.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.post.application.command.CancelPostStarCommand;
import com.mallang.post.application.command.StarPostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.star.PostStar;
import com.mallang.post.domain.star.PostStarRepository;
import com.mallang.post.domain.star.PostStarValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostStarService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostStarRepository postStarRepository;
    private final PostStarValidator postStarValidator;

    public Long star(StarPostCommand command) {
        Post post = postRepository.getById(command.postId(), command.blogName());
        Member member = memberRepository.getById(command.memberId());
        PostStar postStar = new PostStar(post, member);
        postStar.star(postStarValidator, command.postPassword());
        return postStarRepository.save(postStar).getId();
    }

    public void cancel(CancelPostStarCommand command) {
        PostStar postStar = postStarRepository.getByPostAndMember(
                command.postId(),
                command.blogName(),
                command.memberId()
        );
        postStarRepository.delete(postStar);
    }
}
