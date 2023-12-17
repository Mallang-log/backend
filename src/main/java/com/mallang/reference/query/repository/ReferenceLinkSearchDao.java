package com.mallang.reference.query.repository;

import static com.mallang.reference.domain.QLabel.label;
import static com.mallang.reference.domain.QReferenceLink.referenceLink;

import com.mallang.auth.domain.Member;
import com.mallang.reference.domain.ReferenceLink;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

public interface ReferenceLinkSearchDao {

    List<ReferenceLink> search(Member member, ReferenceLinkSearchDaoCond cond);

    record ReferenceLinkSearchDaoCond(
            @Nullable String url,
            @Nullable String title,
            @Nullable String memo,
            @Nullable Long labelId
    ) {
    }

    @RequiredArgsConstructor
    @Component
    class ReferenceLinkSearchDaoImpl implements ReferenceLinkSearchDao {

        private final JPAQueryFactory query;

        @Override
        public List<ReferenceLink> search(Member member, ReferenceLinkSearchDaoCond cond) {
            return query.selectFrom(referenceLink)
                    .leftJoin(referenceLink.label, label).fetchJoin()
                    .where(
                            memberEq(member),
                            urlContains(cond.url()),
                            titleContains(cond.title()),
                            memoContains(cond.memo()),
                            labelEq(cond.labelId())
                    )
                    .orderBy(referenceLink.createdDate.desc())
                    .fetch();
        }

        private BooleanExpression memberEq(Member member) {
            return referenceLink.member.eq(member);
        }

        private BooleanExpression urlContains(String url) {
            if (StringUtils.hasText(url)) {
                return referenceLink.url.url.containsIgnoreCase(url);
            }
            return null;
        }

        private BooleanExpression titleContains(String title) {
            if (StringUtils.hasText(title)) {
                return referenceLink.title.title.containsIgnoreCase(title);
            }
            return null;
        }

        private BooleanExpression memoContains(String memo) {
            if (StringUtils.hasText(memo)) {
                return referenceLink.memo.memo.containsIgnoreCase(memo);
            }
            return null;
        }

        private BooleanExpression labelEq(Long labelId) {
            if (labelId != null) {
                return referenceLink.label.id.eq(labelId);
            }
            return null;
        }
    }
}
