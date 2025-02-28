package com.datn.controller;

import java.io.IOException;
import java.util.List;

import com.datn.service.CloudinaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.datn.entity.Blog;
import com.datn.response.BlogResponse;
import com.datn.service.BlogService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/blog")
@CrossOrigin
public class AdminBlogController {
	@Autowired
	private BlogService blogService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ObjectMapper objectMapper;

//    @PostMapping(consumes = {"multipart/form-data"})
//    public ResponseEntity<Blog> createBlog(
//            @RequestPart("request") String requestJson,
//            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
//
//        // Chuyển JSON từ chuỗi thành object Blog
//        Blog blog = objectMapper.readValue(requestJson, Blog.class);
//
//        // Nếu có ảnh tải lên, upload và cập nhật danh sách URL
//        if (images != null && !images.isEmpty()) {
//            List<String> imageUrls = cloudinaryService.uploadImages(images);
//            blog.setImages(imageUrls);
//        }
//
//        // Lưu blog vào database
//        Blog createdBlog = blogService.createBlog(blog);
//
//        return ResponseEntity.ok(createdBlog);
//    }

	@GetMapping
    public List<Blog> getAllBlogs() {
        return blogService.getAllBlogs();
    }

    // Chi tiết blog
    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        return blogService.getBlogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Thêm blog mới
//    @PostMapping
//    public Blog createBlog(@RequestBody Blog blog) {
//        return blogService.createBlog(blog);
//    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Blog> createBlog(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Blog blog = objectMapper.readValue(requestJson, Blog.class);

        // Gọi service để tạo blog
        Blog createdBlog = blogService.createBlog1(blog, images);

        return new ResponseEntity<>(createdBlog, HttpStatus.CREATED);
    }


    // Sửa blog
    @PutMapping("/{id}")
    public ResponseEntity<Blog> updateBlog(@PathVariable Long id, @RequestBody Blog blog) {
        try {
            return ResponseEntity.ok(blogService.updateBlog(id, blog));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Xóa blog
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/page")
    public BlogResponse getAllBlogsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return blogService.getAllBlogsPaginated(page, size);
    }
}
