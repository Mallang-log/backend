package com.mallang.post.query.repository;

import com.mallang.post.domain.star.StarGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StarGroupQueryRepository extends JpaRepository<StarGroup, Long> {

    List<StarGroup> findAllByOwnerId(Long ownerId);
}
