package com.example.recipes.service;

import com.example.recipes.dto.RecipeRequest;
import com.example.recipes.dto.RecipeResponse;
import com.example.recipes.model.Recipe;
import com.example.recipes.repository.RecipeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ObjectMapper objectMapper;

    public RecipeService(RecipeRepository recipeRepository,
                         ObjectMapper objectMapper) {
        this.recipeRepository = recipeRepository;
        this.objectMapper = objectMapper;
    }

    public Recipe createRecipe(RecipeRequest request) throws Exception {

        Recipe recipe = new Recipe();

        recipe.setTitle(request.getTitle());
        recipe.setCuisine(request.getCuisine());
        recipe.setRating(request.getRating());

        recipe.setPrepTime(request.getPrep_time());
        recipe.setCookTime(request.getCook_time());

        recipe.setTotalTime(
                request.getPrep_time() + request.getCook_time()
        );

        recipe.setDescription(request.getDescription());

        if (request.getNutrients() != null) {
            recipe.setNutrients(
                    objectMapper.writeValueAsString(request.getNutrients())
            );
        }

        recipe.setServes(request.getServes());

        return recipeRepository.save(recipe);
    }

    public List<RecipeResponse> getTopRecipes(int limit) {

        List<Recipe> recipes =
                recipeRepository.findAllByOrderByRatingDesc();

        return recipes.stream()
                .limit(limit)
                .map(this::convertToResponse)
                .toList();
    }

    public RecipeResponse convertToResponse(Recipe recipe) {

        RecipeResponse response = new RecipeResponse();

        response.setId(recipe.getId());
        response.setTitle(recipe.getTitle());
        response.setCuisine(recipe.getCuisine());
        response.setRating(recipe.getRating());
        response.setPrep_time(recipe.getPrepTime());
        response.setCook_time(recipe.getCookTime());
        response.setTotal_time(recipe.getTotalTime());
        response.setDescription(recipe.getDescription());
        response.setServes(recipe.getServes());

        try {
            if (recipe.getNutrients() != null) {
                response.setNutrients(
                        objectMapper.readValue(
                                recipe.getNutrients(),
                                Map.class
                        )
                );
            }
        } catch (Exception e) {
            response.setNutrients(null);
        }

        return response;
    }
}