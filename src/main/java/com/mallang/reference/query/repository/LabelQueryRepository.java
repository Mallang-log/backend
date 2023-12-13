package com.mallang.reference.query.repository;

import com.mallang.reference.domain.Label;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelQueryRepository extends JpaRepository<Label, Long> {

    List<Label> findAllByOwnerId(Long ownerId);
}
