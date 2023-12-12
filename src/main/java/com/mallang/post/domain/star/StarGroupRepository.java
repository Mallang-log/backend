package com.mallang.post.domain.star;

import com.mallang.post.exception.NotFoundStarGroupException;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StarGroupRepository extends JpaRepository<StarGroup, Long> {

    @Override
    default StarGroup getById(Long id) {
        return findById(id).orElseThrow(NotFoundStarGroupException::new);
    }

    default StarGroup getByIdIfIdNotNull(@Nullable Long id) {
        if (id == null) {
            return null;
        }
        return getById(id);
    }
}
