package com.example.recipes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

@Data
public class RecipeResponse {

    private Integer id;
    private String title;
    private String cuisine;
    private Float rating;
    @JsonProperty("prep_time")
    private Integer prepTime;
    @JsonProperty("cook_time")
    private Integer cookTime;
    @JsonProperty("total_time")
    private Integer totalTime;
    private String description;
    private Map<String, Object> nutrients;
    private String serves;
}
