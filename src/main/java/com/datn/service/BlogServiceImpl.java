package com.datn.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.datn.entity.Blog;
import com.datn.repository.BlogRepository;
import com.datn.response.BlogResponse;

@Service
public class BlogServiceImpl implements BlogService {
	
	@Autowired
	private BlogRepository blogRepository;
	
	@Override
	public List<Blog> getAllBlogs() {
		 return blogRepository.findAll();
	}

	@Override
	public Optional<Blog> getBlogById(Long id) {
		return blogRepository.findById(id);
	}

	@Override
	public Blog createBlog(Blog blog) {
		blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        return blogRepository.save(blog);
	}

	@Override
	public Blog updateBlog(Long id, Blog updatedBlog) {
		 return blogRepository.findById(id).map(blog -> {
	            blog.setTitle(updatedBlog.getTitle());
	            blog.setContent(updatedBlog.getContent());
	            blog.setImages(updatedBlog.getImages());
	            blog.setCategory(updatedBlog.getCategory());
	            blog.setUpdatedAt(LocalDateTime.now());
	            return blogRepository.save(blog);
	        }).orElseThrow(() -> new RuntimeException("Blog not found with id: " + id));
	}

	@Override
	public void deleteBlog(Long id) {
		 blogRepository.deleteById(id);
	}

	@Override
	public BlogResponse getAllBlogsPaginated(int page, int size) {
		 Pageable pageable = PageRequest.of(page, size);
	        Page<Blog> blogPage = blogRepository.findAll(pageable);
	        
	        return new BlogResponse(
	            blogPage.getContent(),
	            blogPage.getNumber(),
	            blogPage.getSize(),
	            blogPage.getTotalElements(),
	            blogPage.getTotalPages(),
	            blogPage.isFirst(),
	            blogPage.isLast()
	        );
	}

	@Override
	public List<Blog> getLatestBlogs() {
		return blogRepository.findTop3BlogsByCreatedAtDesc();
	}

//	@Override
//	public Page<Blog> getAllBlogsPaginated(int page, int size) {
//		Pageable pageable = PageRequest.of(page, size);
//        return blogRepository.findAll(pageable);
//	}
	
}
