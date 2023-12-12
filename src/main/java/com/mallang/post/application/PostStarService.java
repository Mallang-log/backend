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
import com.mallang.post.domain.star.StarGroup;
import com.mallang.post.domain.star.StarGroupRepository;
import jakarta.annotation.Nullable;
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
    private final StarGroupRepository starGroupRepository;
    private final PostStarValidator postStarValidator;

    public Long star(StarPostCommand command) {
        Post post = postRepository.getById(command.postId(), command.blogName());
        Member member = memberRepository.getById(command.memberId());
        StarGroup group = starGroupRepository.getByIdIfIdNotNull(command.starGroupId());
        PostStar postStar = new PostStar(post, member, group);
        postStar.star(postStarValidator, command.postPassword());
        return postStarRepository.save(postStar).getId();
    }

    public void updateGroup(Long starId, @Nullable Long myGroupId) {
        PostStar postStar = postStarRepository.getById(starId);
        StarGroup group = starGroupRepository.getByIdIfIdNotNull(myGroupId);
        postStar.updateGroup(group);
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
