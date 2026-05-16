
package com.example.recipes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class RecipeRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Cuisine is required")
    private String cuisine;

    @DecimalMin(value = "0.0", message = "Rating must be at least 0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5")
    private Float rating;

    @NotNull(message = "Prep time is required")
    @Min(value = 0, message = "Prep time must be zero or greater")
    @JsonProperty("prep_time")
    private Integer prepTime;

    @NotNull(message = "Cook time is required")
    @Min(value = 0, message = "Cook time must be zero or greater")
    @JsonProperty("cook_time")
    private Integer cookTime;

    private String description;

    private Map<String, Object> nutrients;

    @NotBlank(message = "Serves is required")
    @Size(max = 100, message = "Serves must not exceed 100 characters")
    private String serves;
}
