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
import com.mallang.post.domain.PostIntro;
import com.mallang.post.domain.PostOrderInBlogGenerator;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.PostVisibilityPolicy;
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
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final PostOrderInBlogGenerator postOrderInBlogGenerator;

    public PostId create(CreatePostCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Blog blog = blogRepository.getByNameAndOwner(command.blogName(), command.memberId());
        Category category = getCategoryByIdAndOwnerIdIfPresent(command.categoryId(), command.memberId());
        PostId postId = postOrderInBlogGenerator.generate(blog.getId());
        Post post = command.toPost(member, category, postId);
        return postRepository.save(post).getPostId();
    }

    public void update(UpdatePostCommand command) {
        Post post = postRepository
                .getByIdAndWriter(command.postId(), command.blogName(), command.memberId());
        Category category = getCategoryByIdAndOwnerIdIfPresent(command.categoryId(), command.memberId());
        post.update(
                command.title(),
                command.content(),
                command.postThumbnailImageName(),
                new PostIntro(command.intro()),
                new PostVisibilityPolicy(command.visibility(), command.password()),
                category,
                command.tags()
        );
    }

    public void delete(DeletePostCommand command) {
        List<Post> posts = postRepository
                .findAllByIdInAndWriter(command.postIds(), command.blogName(), command.memberId());
        for (Post post : posts) {
            post.delete();
            postRepository.delete(post);
        }
    }

    private Category getCategoryByIdAndOwnerIdIfPresent(@Nullable Long id, Long ownerId) {
        if (id == null) {
            return null;
        }
        return categoryRepository.getByIdAndOwner(id, ownerId);
    }
}
