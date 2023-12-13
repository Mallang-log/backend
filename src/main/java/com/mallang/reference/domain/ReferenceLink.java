package com.mallang.reference.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.common.domain.CommonRootEntity;
import com.mallang.reference.exception.NoAuthorityReferenceLinkException;
import jakarta.annotation.Nullable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class ReferenceLink extends CommonRootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    private ReferenceLinkUrl url;

    @Embedded
    private ReferenceLinkTitle title;

    @Embedded
    private ReferenceLinkMemo memo;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "label_id", nullable = true)
    private Label label;

    public ReferenceLink(String url, String title, String memo, Member member, @Nullable Label label) {
        this.url = new ReferenceLinkUrl(url);
        this.title = new ReferenceLinkTitle(title);
        this.memo = new ReferenceLinkMemo(memo);
        this.member = member;
        setLabel(label);
    }

    public void update(String url, String title, String memo, @Nullable Label label) {
        this.url = new ReferenceLinkUrl(url);
        this.title = new ReferenceLinkTitle(title);
        this.memo = new ReferenceLinkMemo(memo);
        setLabel(label);
    }

    private void setLabel(Label label) {
        if (label != null) {
            label.validateOwner(member);
        }
        this.label = label;
    }

    public void validateMember(Member member) {
        if (!this.member.equals(member)) {
            throw new NoAuthorityReferenceLinkException();
        }
    }

    public String getUrl() {
        return url.getUrl();
    }

    public String getTitle() {
        return title.getTitle();
    }

    public String getMemo() {
        return memo.getMemo();
    }
}
