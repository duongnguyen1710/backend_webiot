package com.datn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Blog;
import com.datn.response.BlogResponse;
import com.datn.service.BlogService;

@RestController
@CrossOrigin
@RequestMapping("/blog")
public class BlogController {
	@Autowired
	private BlogService blogService;

	@GetMapping("/page")
	public BlogResponse getAllBlogsPaginated(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		return blogService.getAllBlogsPaginated(page, size);
	}

	@GetMapping("/latest")
    public List<Blog> getLatestBlogs() {
        return blogService.getLatestBlogs();
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        return blogService.getBlogById(id)
                .map(ResponseEntity::ok) // Nếu tìm thấy bài viết, trả về HTTP 200 kèm dữ liệu.
                .orElse(ResponseEntity.notFound().build()); // Nếu không tìm thấy, trả về HTTP 404.
    }
}
