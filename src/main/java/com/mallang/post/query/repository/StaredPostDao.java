package com.mallang.post.query.repository;

import static com.mallang.auth.domain.QMember.member;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.QPost.post;
import static com.mallang.post.domain.star.QPostStar.postStar;

import com.mallang.post.domain.star.PostStar;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

public interface StaredPostDao {

    Page<PostStar> searchByMember(Long memberId, @Nullable Long starGroupId, Pageable pageable);

    @RequiredArgsConstructor
    @Repository
    class StaredPostDaoImpl implements StaredPostDao {

        private final JPAQueryFactory query;

        @Override
        public Page<PostStar> searchByMember(Long memberId, @Nullable Long starGroupId, Pageable pageable) {
            JPAQuery<Long> countQuery = query.select(postStar.countDistinct())
                    .from(postStar)
                    .where(
                            filterPrivatePost(),
                            member.id.eq(memberId),
                            starGroupEq(starGroupId)
                    );
            List<PostStar> result = query.selectFrom(postStar)
                    .distinct()
                    .join(postStar.post, post).fetchJoin()
                    .join(postStar.member, member).fetchJoin()
                    .where(
                            filterPrivatePost(),
                            member.id.eq(memberId),
                            starGroupEq(starGroupId)
                    )
                    .orderBy(postStar.id.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
        }

        private BooleanExpression filterPrivatePost() {
            return post.visibilityPolish.visibility.ne(PRIVATE);
        }

        private BooleanExpression starGroupEq(@Nullable Long starGroupId) {
            if (starGroupId == null) {
                return null;
            }
            return postStar.starGroup.id.eq(starGroupId);
        }
    }
}
