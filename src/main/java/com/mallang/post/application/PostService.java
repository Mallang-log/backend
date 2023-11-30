package com.mallang.post.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.category.domain.Category;
import com.mallang.category.domain.CategoryRepository;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.DeletePostCommand;
import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostId;
import com.mallang.post.domain.PostIdGenerator;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.PostVisibilityPolicy;
import com.mallang.post.domain.draft.Draft;
import com.mallang.post.domain.draft.DraftRepository;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final BlogRepository blogRepository;
    private final PostRepository postRepository;
    private final DraftRepository draftRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final PostIdGenerator postIdGenerator;

    public PostId createFromDraft(CreatePostCommand command, Long draftId) {
        Member member = memberRepository.getById(command.memberId());
        Draft draft = draftRepository.getById(draftId);
        draft.validateWriter(member);
        PostId postId = create(command);
        draftRepository.delete(draft);
        return postId;
    }

    public PostId create(CreatePostCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Blog blog = blogRepository.getByName(command.blogName());
        Category category = getCategoryByIdIfPresent(command.categoryId());
        PostId postId = postIdGenerator.generate(blog.getId());
        Post post = command.toPost(member, postId, blog, category);
        return postRepository.save(post).getPostId();
    }

    public void update(UpdatePostCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Post post = postRepository.getById(command.postId(), command.blogName());
        Category category = getCategoryByIdIfPresent(command.categoryId());
        post.validateWriter(member);
        post.update(
                new PostVisibilityPolicy(command.visibility(), command.password()),
                command.title(),
                command.bodyText(),
                command.postThumbnailImageName(),
                command.intro(),
                category,
                command.tags()
        );
    }

    public void delete(DeletePostCommand command) {
        Member member = memberRepository.getById(command.memberId());
        List<Post> posts = postRepository.findAllByIdIn(command.postIds(), command.blogName());
        for (Post post : posts) {
            post.validateWriter(member);
            post.delete();
            postRepository.delete(post);
        }
    }

    private Category getCategoryByIdIfPresent(@Nullable Long id) {
        if (id == null) {
            return null;
        }
        return categoryRepository.getById(id);
    }
}
