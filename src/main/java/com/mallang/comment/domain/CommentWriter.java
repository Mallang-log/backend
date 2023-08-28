package com.mallang.comment.domain;

import com.mallang.comment.domain.credential.WriterCredential;
import com.mallang.common.domain.CommonDomainModel;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class CommentWriter extends CommonDomainModel {

    public abstract boolean hasAuthority(WriterCredential writerCredential);
}
