package com.mallang.comment.domain.service;

import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentDeleteService {

    private final CommentRepository commentRepository;

    public void delete(Comment comment) {
        if (hasChild(comment)) {
            return;
        }
        if (comment.isChild()) {
            deleteChildComment(comment);
            return;
        }
        deleteSingleRootComment(comment);
    }

    private boolean hasChild(Comment comment) {
        return !comment.getChildren().isEmpty();
    }

    private void deleteChildComment(Comment comment) {
        Comment parent = comment.getParent();
        comment.unlinkFromParent();
        physicalDelete(comment);
        if (parent.isDeleted() && parent.getChildren().isEmpty()) {
            physicalDelete(parent);
        }
    }

    private void physicalDelete(Comment comment) {
        commentRepository.delete(comment);
    }

    private void deleteSingleRootComment(Comment comment) {
        physicalDelete(comment);
    }
}
