package com.mallang.comment.query.data;

import com.mallang.comment.domain.writer.AuthenticatedWriter;
import com.mallang.comment.domain.writer.CommentWriter;
import com.mallang.comment.domain.writer.UnAuthenticatedWriter;
import com.mallang.common.execption.MallangLogException;

public interface CommentWriterData {

    static CommentWriterData from(CommentWriter commentWriter) {
        if (commentWriter instanceof AuthenticatedWriter authed) {
            return AuthenticatedWriterData.from(authed.getMember());
        }
        if (commentWriter instanceof UnAuthenticatedWriter unAuthed) {
            return UnAuthenticatedWriterData.from(unAuthed);
        }
        throw new MallangLogException("해당 CommentWriter 타입이 CommentWriterData 에서 지원되지 않습니다.");
    }
}
