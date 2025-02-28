package com.datn.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.datn.entity.Blog;
import com.datn.response.BlogResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BlogService {
	public List<Blog> getAllBlogs();
	
	public Optional<Blog> getBlogById(Long id);
	
	public Blog createBlog(Blog blog);
	
	public Blog updateBlog(Long id, Blog updatedBlog);
	
	public void deleteBlog(Long id);
	
//	public Page<Blog> getAllBlogsPaginated(int page, int size);
	
	public BlogResponse getAllBlogsPaginated(int page, int size);
	
	 public List<Blog> getLatestBlogs();

	public Blog createBlog1(Blog blog, List<MultipartFile> imageFiles) throws IOException;
	
}
