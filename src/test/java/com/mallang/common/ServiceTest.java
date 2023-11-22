package com.mallang.common;

import static com.mallang.auth.domain.OauthServerType.GITHUB;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.auth.domain.OauthId;
import com.mallang.auth.query.dao.MemberDao;
import com.mallang.blog.application.AboutService;
import com.mallang.blog.application.BlogService;
import com.mallang.blog.application.command.OpenBlogCommand;
import com.mallang.blog.domain.AboutRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.category.application.CategoryService;
import com.mallang.category.domain.CategoryRepository;
import com.mallang.category.query.CategoryQueryService;
import com.mallang.comment.application.AuthCommentService;
import com.mallang.comment.application.UnAuthCommentService;
import com.mallang.comment.application.command.WriteAuthenticatedCommentCommand;
import com.mallang.comment.application.command.WriteUnAuthenticatedCommentCommand;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.UnAuthComment;
import com.mallang.comment.query.CommentQueryService;
import com.mallang.post.application.PostLikeService;
import com.mallang.post.application.PostService;
import com.mallang.post.application.PostStarService;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.DeletePostCommand;
import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostOrderInBlogGenerator;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.like.PostLikeRepository;
import com.mallang.post.domain.star.PostStarRepository;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.query.PostQueryService;
import com.mallang.post.query.PostStarQueryService;
import com.mallang.post.query.dao.PostSearchDao;
import com.mallang.subscribe.application.BlogSubscribeService;
import com.mallang.subscribe.domain.BlogSubscribeRepository;
import com.mallang.subscribe.query.dao.SubscriberDao;
import com.mallang.subscribe.query.dao.SubscribingBlogDao;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
    protected TransactionHelper transactionHelper;
    
    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected MemberDao memberDao;

    @Autowired
    protected BlogRepository blogRepository;

    @Autowired
    protected BlogService blogService;

    @Autowired
    protected AboutRepository aboutRepository;

    @Autowired
    protected AboutService aboutService;

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected PostService postService;

    @Autowired
    protected PostOrderInBlogGenerator postOrderInBlogGenerator;

    @Autowired
    protected PostSearchDao postSearchDao;

    @Autowired
    protected PostQueryService postQueryService;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected CategoryQueryService categoryQueryService;

    @Autowired
    protected PostStarRepository postStarRepository;

    @Autowired
    protected PostStarService postStarService;

    @Autowired
    protected PostStarQueryService postStarQueryService;

    @Autowired
    protected PostLikeRepository postLikeRepository;

    @Autowired
    protected PostLikeService postLikeService;

    @Autowired
    protected BlogSubscribeRepository blogSubscribeRepository;

    @Autowired
    protected BlogSubscribeService blogSubscribeService;

    @Autowired
    protected SubscriberDao subscriberDao;

    @Autowired
    protected SubscribingBlogDao subscribingBlogDao;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected AuthCommentService authCommentService;

    @Autowired
    protected UnAuthCommentService unAuthCommentService;

    @Autowired
    protected CommentQueryService commentQueryService;

    @Autowired
    protected ApplicationEvents events;

    @Autowired
    protected ApplicationEventPublisher publisher;


    protected Long 회원을_저장한다(String 닉네임) {
        Member member = Member.builder()
                .oauthId(new OauthId(UUID.randomUUID().toString(), GITHUB))
                .nickname(닉네임)
                .profileImageUrl(닉네임)
                .build();
        return memberRepository.save(member).getId();
    }

    protected Blog 블로그_개설(Long memberId, String name) {
        return blogRepository.getById(blogService.open(new OpenBlogCommand(memberId, name)));
    }

    protected Long 포스트를_저장한다(Long 회원_ID, String 블로그_이름, String 제목, String 내용, String... 태그들) {
        return 포스트를_저장한다(회원_ID, 블로그_이름, 제목, 내용, PUBLIC, null, null, 태그들);
    }

    protected Long 포스트를_저장한다(Long 회원_ID, String 블로그_이름, String 제목, String 내용, Long 카테고리_ID, String... 태그들) {
        return 포스트를_저장한다(회원_ID, 블로그_이름, 제목, 내용, PUBLIC, null, 카테고리_ID, 태그들);
    }

    protected Long 포스트를_저장한다(Long 회원_ID,
                             String 블로그_이름,
                             String 제목,
                             String 내용,
                             PostVisibilityPolicy 공개범위,
                             String... 태그들
    ) {
        return 포스트를_저장한다(회원_ID, 블로그_이름, 제목, 내용, 공개범위.getVisibility(), 공개범위.getPassword(), null, 태그들);
    }

    protected Long 포스트를_저장한다(
            Long 회원_ID,
            String 블로그_이름,
            String 제목,
            String 내용,
            Visibility visibility,
            String password,
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
                visibility,
                password,
                카테고리_ID,
                Arrays.asList(태그들)
        ));
    }

    protected void 포스트_공개여부를_업데이트한다(Long 회원_ID, Long postId, Visibility visibility, String password) {
        Post post = postRepository.getById(postId);
        postService.update(new UpdatePostCommand(
                회원_ID,
                postId,
                post.getTitle(),
                post.getContent(),
                post.getPostThumbnailImageName(),
                post.getPostIntro(),
                visibility,
                password,
                null,
                Collections.emptyList()
        ));
    }

    protected void 포스트를_삭제한다(Long memberId, Long postId) {
        postService.delete(new DeletePostCommand(memberId, List.of(postId)));
    }

    public Long 댓글을_작성한다(Long postId, String content, boolean secret, Long memberId) {
        WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .secret(secret)
                .memberId(memberId)
                .build();
        return authCommentService.write(command);
    }

    public Long 비인증_댓글을_작성한다(Long postId, String content, String nickname, String password) {
        WriteUnAuthenticatedCommentCommand command = WriteUnAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .nickname(nickname)
                .password(password)
                .build();
        return unAuthCommentService.write(command);
    }

    public Long 대댓글을_작성한다(Long postId, String content, boolean secret, Long memberId, Long parentCommentId) {
        WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .secret(secret)
                .memberId(memberId)
                .parentCommentId(parentCommentId)
                .build();
        return authCommentService.write(command);
    }

    public Long 비인증_대댓글을_작성한다(Long postId, String content, String nickname, String password, Long parentCommentId) {
        WriteUnAuthenticatedCommentCommand command = WriteUnAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .nickname(nickname)
                .password(password)
                .parentCommentId(parentCommentId)
                .build();
        return unAuthCommentService.write(command);
    }

    public AuthComment 인증된_댓글을_조회한다(Long 댓글_ID) {
        return commentRepository.getAuthenticatedCommentById(댓글_ID);
    }

    public UnAuthComment 비인증_댓글을_조회한다(Long 댓글_ID) {
        return commentRepository.getUnAuthenticatedCommentById(댓글_ID);
    }
}
