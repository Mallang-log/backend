package com.mallang.comment.domain.service;

import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.writer.UnAuthenticatedWriter;
import com.mallang.comment.domain.writer.UnAuthenticatedWriterRepository;
import com.mallang.comment.domain.writer.WriterCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentDeleteService {

    private final CommentRepository commentRepository;
    private final UnAuthenticatedWriterRepository unAuthenticatedWriterRepository;

    public void delete(Comment comment, WriterCredential credential) {
        if (comment.isChild()) {
            deleteChildComment(comment, credential);
            return;
        }
        if (hasChild(comment)) {
            logicalDelete(comment, credential);
            return;
        }
        deleteSingleRootComment(comment, credential);
    }

    private void deleteChildComment(Comment comment, WriterCredential credential) {
        Comment parent = comment.getParent();
        comment.delete(credential);
        physicalDelete(comment);
        if (parent.isDeleted() && parent.getChildren().isEmpty()) {
            physicalDelete(parent);
        }
    }

    private void physicalDelete(Comment comment) {
        commentRepository.delete(comment);
        if (comment.getCommentWriter() instanceof UnAuthenticatedWriter unAuthenticatedWriter) {
            unAuthenticatedWriterRepository.delete(unAuthenticatedWriter);
        }
    }

    private boolean hasChild(Comment comment) {
        return !comment.getChildren().isEmpty();
    }

    private void logicalDelete(Comment comment, WriterCredential credential) {
        comment.delete(credential);
    }

    private void deleteSingleRootComment(Comment comment, WriterCredential credential) {
        comment.delete(credential);
        physicalDelete(comment);
    }
}
