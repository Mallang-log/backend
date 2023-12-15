package com.mallang.notification.domain.generator;

import com.mallang.auth.domain.Member;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.CommentWrittenEvent;
import com.mallang.common.domain.DomainEvent;
import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.type.CommentWrittenNotification;
import com.mallang.post.domain.Post;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentWrittenNotificationGenerator implements NotificationGenerator {

    private final CommentRepository commentRepository;

    @Override
    public boolean canGenerateFrom(DomainEvent<?> domainEvent) {
        return domainEvent instanceof CommentWrittenEvent;
    }

    @Override
    public List<Notification> generate(DomainEvent<?> domainEvent) {
        CommentWrittenEvent event = (CommentWrittenEvent) domainEvent;
        Comment comment = commentRepository.getById(event.id());
        Post post = comment.getPost();
        if (comment.getParent() == null) {
            return convertComment(comment, post);
        }
        return convertReply(comment, post);
    }

    private List<Notification> convertComment(Comment comment, Post post) {
        List<Notification> list = new ArrayList<>();
        Member commentWriter = getWriterOrNull(comment);
        if (post.isWriter(commentWriter)) {
            return list;
        }
        list.add(CommentWrittenNotification.of(post.getWriter(), post, comment));
        return list;
    }

    private List<Notification> convertReply(Comment reply, Post post) {
        Comment parent = reply.getParent();
        Member postWriter = post.getWriter();
        Member parentWriter = getWriterOrNull(parent);
        Member replyWriter = getWriterOrNull(reply);
        List<Notification> list = new ArrayList<>();
        if (post.isWriter(parentWriter) && post.isWriter(replyWriter)) {
            return list;
        }
        if (post.isWriter(parentWriter)) {
            list.add(CommentWrittenNotification.replyOf(postWriter, post, parent, reply));
            return list;
        }
        if (post.isWriter(replyWriter)) {
            if (parent instanceof AuthComment authParent) {
                list.add(CommentWrittenNotification.replyOf(authParent.getWriter(), post, parent, reply));
            }
            return list;
        }
        list.add(CommentWrittenNotification.replyOf(postWriter, post, parent, reply));
        if (parent instanceof AuthComment authComment) {
            if (authComment.getWriter().equals(replyWriter)) {
                return list;
            }
            list.add(CommentWrittenNotification.replyOf(authComment.getWriter(), post, parent, reply));
        }
        return list;
    }

    private Member getWriterOrNull(Comment comment) {
        if (comment instanceof AuthComment authComment) {
            return authComment.getWriter();
        }
        return null;
    }
}
