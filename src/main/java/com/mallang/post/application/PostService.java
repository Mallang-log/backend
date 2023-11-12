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
import com.mallang.post.domain.PostIntro;
import com.mallang.post.domain.PostOrderInBlogGenerator;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
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

    public Long create(CreatePostCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Blog blog = blogRepository.getById(command.blogId());
        Category category = getCategoryByIdIfPresent(command.categoryId());
        Long postIdInBlog = postOrderInBlogGenerator.generate(blog);
        Post post = command.toPost(member, blog, category, postIdInBlog);
        return postRepository.save(post).getId();
    }

    public void update(UpdatePostCommand command) {
        Post post = postRepository.getById(command.postId());
        Category category = getCategoryByIdIfPresent(command.categoryId());
        post.update(
                command.memberId(),
                command.title(),
                command.content(),
                new PostIntro(command.intro()),
                new PostVisibilityPolicy(command.visibility(), command.password()),
                category,
                command.tags()
        );
    }

    public void delete(DeletePostCommand command) {
        List<Post> posts = postRepository.findAllByIdIn(command.postIds());
        for (Post post : posts) {
            post.delete(command.memberId());
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
