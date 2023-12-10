package com.mallang.common;

import static com.mallang.auth.domain.OauthId.OauthServerType.GITHUB;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;

import com.mallang.auth.application.BasicAuthService;
import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.auth.domain.OauthId;
import com.mallang.auth.domain.OauthMember;
import com.mallang.auth.query.MemberQueryService;
import com.mallang.blog.application.AboutService;
import com.mallang.blog.application.BlogService;
import com.mallang.blog.application.BlogSubscribeService;
import com.mallang.blog.application.command.OpenBlogCommand;
import com.mallang.blog.domain.AboutRepository;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.blog.domain.subscribe.BlogSubscribeRepository;
import com.mallang.blog.query.BlogSubscribeQueryService;
import com.mallang.category.application.PostCategoryService;
import com.mallang.category.domain.PostCategoryRepository;
import com.mallang.category.query.PostCategoryQueryService;
import com.mallang.comment.application.AuthCommentService;
import com.mallang.comment.application.UnAuthCommentService;
import com.mallang.comment.application.command.WriteAuthCommentCommand;
import com.mallang.comment.application.command.WriteUnAuthCommentCommand;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.UnAuthComment;
import com.mallang.comment.query.CommentQueryService;
import com.mallang.post.application.DraftService;
import com.mallang.post.application.PostLikeService;
import com.mallang.post.application.PostService;
import com.mallang.post.application.PostStarService;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.DeletePostCommand;
import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostId;
import com.mallang.post.domain.PostIdGenerator;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.PostVisibilityPolicy;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.mallang.post.domain.draft.DraftRepository;
import com.mallang.post.domain.like.PostLikeRepository;
import com.mallang.post.domain.star.PostStarRepository;
import com.mallang.post.query.DraftQueryService;
import com.mallang.post.query.PostManageQueryService;
import com.mallang.post.query.PostQueryService;
import com.mallang.post.query.PostStarQueryService;
import com.mallang.reference.application.ReferenceLinkService;
import com.mallang.reference.domain.ReferenceLinkRepository;
import com.mallang.reference.query.ReferenceLinkQueryService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@ExtendWith(DataClearExtension.class)
@RecordApplicationEvents
@SpringBootTest
public abstract class ServiceTest {

    protected final Pageable pageable = PageRequest.of(0, 100);

    @Autowired
    protected TransactionHelper transactionHelper;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected BasicAuthService basicAuthService;

    @Autowired
    protected MemberQueryService memberQueryService;

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
    protected PostIdGenerator postIdGenerator;

    @Autowired
    protected PostQueryService postQueryService;

    @Autowired
    protected PostManageQueryService postManageQueryService;

    @Autowired
    protected DraftRepository draftRepository;

    @Autowired
    protected DraftService draftService;

    @Autowired
    protected DraftQueryService draftQueryService;

    @Autowired
    protected PostCategoryRepository postCategoryRepository;

    @Autowired
    protected PostCategoryService postCategoryService;

    @Autowired
    protected PostCategoryQueryService postCategoryQueryService;

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
    protected BlogSubscribeQueryService blogSubscribeQueryService;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected AuthCommentService authCommentService;

    @Autowired
    protected UnAuthCommentService unAuthCommentService;

    @Autowired
    protected CommentQueryService commentQueryService;

    @Autowired
    protected ReferenceLinkRepository referenceLinkRepository;

    @Autowired
    protected ReferenceLinkService referenceLinkService;

    @Autowired
    protected ReferenceLinkQueryService referenceLinkQueryService;

    @Autowired
    protected ApplicationEvents events;

    @Autowired
    protected ApplicationEventPublisher publisher;


    protected Long 회원을_저장한다(String 닉네임) {
        Member member = OauthMember.builder()
                .oauthId(new OauthId(UUID.randomUUID().toString(), GITHUB))
                .nickname(닉네임)
                .profileImageUrl(닉네임)
                .build();
        return memberRepository.save(member).getId();
    }

    protected String 블로그_개설(Long memberId, String name) {
        blogService.open(new OpenBlogCommand(memberId, name));
        return name;
    }

    protected PostId 포스트를_저장한다(Long 회원_ID, String 블로그_이름, String 제목, String 내용, String... 태그들) {
        return 포스트를_저장한다(회원_ID, 블로그_이름, 제목, 내용, PUBLIC, null, null, 태그들);
    }

    protected PostId 포스트를_저장한다(Long 회원_ID, String 블로그_이름, String 제목, String 내용, Long 카테고리_ID, String... 태그들) {
        return 포스트를_저장한다(회원_ID, 블로그_이름, 제목, 내용, PUBLIC, null, 카테고리_ID, 태그들);
    }

    protected PostId 포스트를_저장한다(Long 회원_ID,
                               String 블로그_이름,
                               String 제목,
                               String 내용,
                               PostVisibilityPolicy 공개범위,
                               String... 태그들
    ) {
        return 포스트를_저장한다(회원_ID, 블로그_이름, 제목, 내용, 공개범위.getVisibility(), 공개범위.getPassword(), null, 태그들);
    }

    protected PostId 포스트를_저장한다(
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
                내용.substring(0, Math.min(내용.length(), 50)), 내용,
                null,
                visibility,
                password,
                카테고리_ID,
                Arrays.asList(태그들)
        ));
    }

    protected void 포스트_공개여부를_업데이트한다(Long 회원_ID, Long postId, String blogName, Visibility visibility, String password) {
        Post post = postRepository.getById(postId, blogName);
        postService.update(new UpdatePostCommand(
                회원_ID,
                postId,
                blogName,
                post.getTitle(),
                post.getPostIntro(), post.getBodyText(),
                post.getPostThumbnailImageName(),
                visibility,
                password,
                null,
                Collections.emptyList()
        ));
    }

    protected void 포스트를_삭제한다(Long memberId, Long postId, String blogName) {
        postService.delete(new DeletePostCommand(memberId, List.of(postId), blogName));
    }

    public Long 댓글을_작성한다(Long postId, String blogName, String content, boolean secret, Long memberId) {
        WriteAuthCommentCommand command = WriteAuthCommentCommand.builder()
                .postId(postId)
                .blogName(blogName)
                .content(content)
                .secret(secret)
                .memberId(memberId)
                .build();
        return authCommentService.write(command);
    }

    public Long 비인증_댓글을_작성한다(Long postId, String blogName, String content, String nickname, String password) {
        WriteUnAuthCommentCommand command = WriteUnAuthCommentCommand.builder()
                .postId(postId)
                .blogName(blogName)
                .content(content)
                .nickname(nickname)
                .password(password)
                .build();
        return unAuthCommentService.write(command);
    }

    public Long 대댓글을_작성한다(
            Long postId,
            String blogName,
            String content,
            boolean secret,
            Long memberId,
            Long parentCommentId
    ) {
        WriteAuthCommentCommand command = WriteAuthCommentCommand.builder()
                .postId(postId)
                .blogName(blogName)
                .content(content)
                .secret(secret)
                .memberId(memberId)
                .parentCommentId(parentCommentId)
                .build();
        return authCommentService.write(command);
    }

    public Long 비인증_대댓글을_작성한다(
            Long postId,
            String blogName,
            String content,
            String nickname,
            String password,
            Long parentCommentId
    ) {
        WriteUnAuthCommentCommand command = WriteUnAuthCommentCommand.builder()
                .postId(postId)
                .blogName(blogName)
                .content(content)
                .nickname(nickname)
                .password(password)
                .parentCommentId(parentCommentId)
                .build();
        return unAuthCommentService.write(command);
    }

    public AuthComment 인증된_댓글을_조회한다(Long 댓글_ID) {
        return commentRepository.getAuthCommentById(댓글_ID);
    }

    public UnAuthComment 비인증_댓글을_조회한다(Long 댓글_ID) {
        return commentRepository.getUnAuthCommentById(댓글_ID);
    }
}
