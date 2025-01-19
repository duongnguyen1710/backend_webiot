package com.datn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.datn.entity.Blog;

public interface BlogRepository extends JpaRepository<Blog, Long> {
	@Query(value = "SELECT * FROM blog ORDER BY created_at DESC LIMIT 3", nativeQuery = true)
	List<Blog> findTop3BlogsByCreatedAtDesc();

}

