package com.datn.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String title;

	@Column(columnDefinition = "TEXT") // Hỗ trợ lưu văn bản dài
	private String content;
	
	private String category;
	
	@Column(length = 1000)
	@ElementCollection
	private List<String> images;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
}
