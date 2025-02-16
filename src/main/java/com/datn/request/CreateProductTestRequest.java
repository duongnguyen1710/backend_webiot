package com.datn.request;

import com.datn.entity.Category;
import com.datn.entity.CategoryItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductTestRequest {
    private String name;
    private String description;
    private Long price;

    private Category category;
    private CategoryItem categoryItem;
    private Long restaurantId;

}
