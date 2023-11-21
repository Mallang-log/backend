package com.mallang.blog.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AboutRepository extends JpaRepository<About, Long> {

    boolean existsByBlog(Blog blog);
}
