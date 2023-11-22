package com.mallang.common;

import com.mallang.auth.MemberServiceTestHelper;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.auth.query.dao.MemberDao;
import com.mallang.blog.application.AboutService;
import com.mallang.blog.application.BlogService;
import com.mallang.blog.application.BlogServiceTestHelper;
import com.mallang.blog.domain.AboutRepository;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.category.application.CategoryService;
import com.mallang.category.application.CategoryServiceTestHelper;
import com.mallang.category.query.CategoryQueryService;
import com.mallang.comment.application.AuthCommentService;
import com.mallang.comment.application.CommentServiceTestHelper;
import com.mallang.comment.application.UnAuthCommentService;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.query.CommentQueryService;
import com.mallang.post.application.PostLikeService;
import com.mallang.post.application.PostService;
import com.mallang.post.application.PostServiceTestHelper;
import com.mallang.post.application.PostStarService;
import com.mallang.post.domain.PostOrderInBlogGenerator;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.like.PostLikeRepository;
import com.mallang.post.domain.star.PostStarRepository;
import com.mallang.post.query.PostQueryService;
import com.mallang.post.query.PostStarQueryService;
import com.mallang.post.query.dao.PostSearchDao;
import com.mallang.subscribe.application.BlogSubscribeService;
import com.mallang.subscribe.domain.BlogSubscribeRepository;
import com.mallang.subscribe.query.dao.SubscriberDao;
import com.mallang.subscribe.query.dao.SubscribingBlogDao;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@ExtendWith(DataClearExtension.class)
@RecordApplicationEvents
@SpringBootTest
public abstract class ServiceTest {

    @Autowired
    protected MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    protected BlogServiceTestHelper blogServiceTestHelper;

    @Autowired
    protected PostServiceTestHelper postServiceTestHelper;

    @Autowired
    protected CategoryServiceTestHelper categoryServiceTestHelper;

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected PostService postService;

    @Autowired
    protected TransactionHelper transactionHelper;

    @Autowired
    protected PostStarService postStarService;

    @Autowired
    protected PostStarRepository postStarRepository;

    @Autowired
    protected PostLikeService postLikeService;

    @Autowired
    protected PostLikeRepository postLikeRepository;

    @Autowired
    protected ApplicationEvents events;

    @Autowired
    protected ApplicationEventPublisher publisher;

    @Autowired
    protected PostQueryService postQueryService;

    @Autowired
    protected PostStarQueryService postStarQueryService;

    @Autowired
    protected BlogSubscribeRepository blogSubscribeRepository;

    @Autowired
    protected BlogSubscribeService blogSubscribeService;

    @Autowired
    protected SubscriberDao subscriberDao;

    @Autowired
    protected SubscribingBlogDao subscribingBlogDao;

    @Autowired
    protected CommentServiceTestHelper commentServiceTestHelper;

    @Autowired
    protected CommentQueryService commentQueryService;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected AuthCommentService authCommentService;

    @Autowired
    protected UnAuthCommentService unAuthCommentService;

    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected BlogService blogService;

    @Autowired
    protected AboutRepository aboutRepository;

    @Autowired
    protected AboutService aboutService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected BlogRepository blogRepository;

    @Autowired
    protected MemberDao memberDao;

    @Autowired
    protected PostOrderInBlogGenerator postOrderInBlogGenerator;

    @Autowired
    protected PostSearchDao postSearchDao;

    @Autowired
    protected CategoryQueryService categoryQueryService;
}
