package com.example.recipes.service;

import com.example.recipes.dto.RecipeRequest;
import com.example.recipes.dto.RecipeResponse;
import com.example.recipes.exception.ResourceNotFoundException;
import com.example.recipes.model.Recipe;
import com.example.recipes.repository.RecipeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createRecipeCalculatesTotalTime() throws Exception {
        RecipeService service = new RecipeService(recipeRepository, objectMapper);
        RecipeRequest request = request();
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> {
            Recipe recipe = invocation.getArgument(0);
            recipe.setId(1);
            return recipe;
        });

        RecipeResponse response = service.createRecipe(request);

        assertEquals(1, response.getId());
        assertEquals(45, response.getTotalTime());
        assertEquals("Indian", response.getCuisine());
    }

    @Test
    void getRecipeByIdThrowsWhenMissing() {
        RecipeService service = new RecipeService(recipeRepository, objectMapper);
        when(recipeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getRecipeById(99));
    }

    private RecipeRequest request() {
        RecipeRequest request = new RecipeRequest();
        request.setTitle("Paneer Curry");
        request.setCuisine("Indian");
        request.setRating(4.5f);
        request.setPrepTime(15);
        request.setCookTime(30);
        request.setDescription("Rich curry");
        request.setNutrients(Map.of("protein", "20g"));
        request.setServes("4");
        return request;
    }
}
