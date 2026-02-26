package com.example.recipes.dto;

import lombok.Data;
import java.util.Map;

@Data
public class RecipeResponse {

    private Integer id;
    private String title;
    private String cuisine;
    private Float rating;
    private Integer prep_time;
    private Integer cook_time;
    private Integer total_time;
    private String description;
    private Map<String, Object> nutrients;
    private String serves;
}
