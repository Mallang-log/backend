package com.mallang.post.query.dao;

import static com.mallang.auth.domain.QMember.member;
import static com.mallang.post.domain.QPost.post;
import static com.mallang.post.domain.star.QPostStar.postStar;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;

import com.mallang.post.query.data.StaredPostData;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class StaredPostDataDao {

    private final JPAQueryFactory query;

    public List<StaredPostData> find(Long memberId) {
        return query.selectFrom(postStar)
                .join(postStar.post, post).fetchJoin()
                .join(postStar.member, member).fetchJoin()
                .where(
                        filterPrivatePost(memberId),
                        member.id.eq(memberId)
                )
                .orderBy(postStar.id.desc())
                .fetch()
                .stream()
                .map(StaredPostData::from)
                .toList();
    }

    private BooleanExpression filterPrivatePost(Long memberId) {
        return post.visibilityPolish.visibility.ne(PRIVATE)
                .or(post.visibilityPolish.visibility.eq(PRIVATE)
                        .and(post.writer.id.eq(memberId))
                );
    }
}
