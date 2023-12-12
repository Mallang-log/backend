package com.mallang.reference.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.category.FlatCategory;
import com.mallang.reference.exception.NoAuthorityLabelException;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
public class Label extends FlatCategory<Label> {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "prev_sibling_id")
    private Label previousSibling;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "next_sibling_id")
    private Label nextSibling;

    @Embedded
    private LabelColor color;

    public Label(String name, Member owner, String colorCode) {
        super(name, owner);
        this.color = new LabelColor(colorCode);
    }

    public void update(String name, String colorCode) {
        this.name = name;
        this.color = new LabelColor(colorCode);
    }

    @Override
    public void validateOwner(Member member) {
        if (!owner.equals(member)) {
            throw new NoAuthorityLabelException();
        }
    }

    public String getColor() {
        return color.getRgbColorCode();
    }

    @Override
    public Label getPreviousSibling() {
        return previousSibling;
    }

    @Override
    public Label getNextSibling() {
        return nextSibling;
    }

    @Override
    protected Label self() {
        return this;
    }

    @Override
    protected void setPreviousSibling(Label previousSibling) {
        this.previousSibling = previousSibling;
    }

    @Override
    protected void setNextSibling(Label nextSibling) {
        this.nextSibling = nextSibling;
    }
}
