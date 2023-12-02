package com.mallang.reference.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.blog.domain.Blog;
import com.mallang.common.domain.CommonRootEntity;
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
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    public ReferenceLink(String url, String title, String memo, Blog blog) {
        this.url = new ReferenceLinkUrl(url);
        this.title = new ReferenceLinkTitle(title);
        this.memo = new ReferenceLinkMemo(memo);
        this.blog = blog;
    }

    public void update(String url, String title, String memo) {
        this.url = new ReferenceLinkUrl(url);
        this.title = new ReferenceLinkTitle(title);
        this.memo = new ReferenceLinkMemo(memo);
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
