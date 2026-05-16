
package com.example.recipes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class RecipeRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String cuisine;

    private Float rating;

    @NotNull
    private Integer prep_time;

    @NotNull
    private Integer cook_time;

    private String description;

    private Map<String, Object> nutrients;

    private String serves;
}
