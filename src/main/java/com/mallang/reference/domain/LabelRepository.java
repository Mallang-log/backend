package com.mallang.reference.domain;

import com.mallang.auth.domain.Member;
import com.mallang.reference.exception.NotFoundLabelException;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {

    @Override
    default Label getById(Long id) {
        return findById(id)
                .orElseThrow(NotFoundLabelException::new);
    }

    default Label getByIdIfIdNotNull(@Nullable Long labelId) {
        if (labelId == null) {
            return null;
        }
        return getById(labelId);
    }


    boolean existsByOwner(Member member);

    boolean existsByOwnerAndName(Member member, String name);
}
