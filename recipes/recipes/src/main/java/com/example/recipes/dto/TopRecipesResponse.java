package com.example.recipes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TopRecipesResponse {

    private List<RecipeResponse> data;
}
