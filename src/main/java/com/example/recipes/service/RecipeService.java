package com.example.recipes.service;

import com.example.recipes.dto.RecipeRequest;
import com.example.recipes.dto.RecipeResponse;
import com.example.recipes.dto.PagedResponse;
import com.example.recipes.exception.ResourceNotFoundException;
import com.example.recipes.model.Recipe;
import com.example.recipes.repository.RecipeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class RecipeService {

    private static final Logger log = LoggerFactory.getLogger(RecipeService.class);
    private final RecipeRepository recipeRepository;
    private final ObjectMapper objectMapper;

    public RecipeService(RecipeRepository recipeRepository,
                         ObjectMapper objectMapper) {
        this.recipeRepository = recipeRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RecipeResponse createRecipe(RecipeRequest request) throws Exception {
        Recipe saved = recipeRepository.save(mapRequestToEntity(request, new Recipe()));
        log.info("Created recipe id={} title='{}'", saved.getId(), saved.getTitle());
        return convertToResponse(saved);
    }

    public List<RecipeResponse> getTopRecipes(int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be greater than zero");
        }

        List<Recipe> recipes =
                recipeRepository.findAllByOrderByRatingDesc();

        return recipes.stream()
                .limit(limit)
                .map(this::convertToResponse)
                .toList();
    }

    public PagedResponse<RecipeResponse> getRecipes(String cuisine, Pageable pageable) {
        Page<Recipe> page = cuisine == null || cuisine.isBlank()
                ? recipeRepository.findAll(pageable)
                : recipeRepository.findByCuisineIgnoreCaseContaining(cuisine, pageable);

        return new PagedResponse<>(
                page.getContent().stream().map(this::convertToResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public RecipeResponse getRecipeById(Integer id) {
        return convertToResponse(findRecipe(id));
    }

    @Transactional
    public RecipeResponse updateRecipe(Integer id, RecipeRequest request) throws Exception {
        Recipe recipe = findRecipe(id);
        Recipe updated = recipeRepository.save(mapRequestToEntity(request, recipe));
        log.info("Updated recipe id={}", id);
        return convertToResponse(updated);
    }

    @Transactional
    public void deleteRecipe(Integer id) {
        Recipe recipe = findRecipe(id);
        recipeRepository.delete(recipe);
        log.info("Deleted recipe id={}", id);
    }

    private Recipe findRecipe(Integer id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
    }

    private Recipe mapRequestToEntity(RecipeRequest request, Recipe recipe) throws Exception {
        recipe.setTitle(request.getTitle());
        recipe.setCuisine(request.getCuisine());
        recipe.setRating(request.getRating());
        recipe.setPrepTime(request.getPrepTime());
        recipe.setCookTime(request.getCookTime());
        recipe.setTotalTime(request.getPrepTime() + request.getCookTime());
        recipe.setDescription(request.getDescription());
        recipe.setNutrients(request.getNutrients() == null ? null : objectMapper.writeValueAsString(request.getNutrients()));
        recipe.setServes(request.getServes());
        return recipe;
    }

    public RecipeResponse convertToResponse(Recipe recipe) {

        RecipeResponse response = new RecipeResponse();

        response.setId(recipe.getId());
        response.setTitle(recipe.getTitle());
        response.setCuisine(recipe.getCuisine());
        response.setRating(recipe.getRating());
        response.setPrepTime(recipe.getPrepTime());
        response.setCookTime(recipe.getCookTime());
        response.setTotalTime(recipe.getTotalTime());
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
