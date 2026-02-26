package com.example.recipes.controller;

import com.example.recipes.dto.RecipeRequest;
import com.example.recipes.dto.RecipeResponse;
import com.example.recipes.dto.TopRecipesResponse;
import com.example.recipes.model.Recipe;
import com.example.recipes.service.RecipeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/parsed")
    public List<JsonNode> showParsed() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        InputStream inputStream =
                getClass().getResourceAsStream("/US_recipes_null.json");

        JsonNode rootNode = mapper.readTree(inputStream);

        List<JsonNode> list = new ArrayList<>();

        Iterator<Map.Entry<String, JsonNode>> fields =
                rootNode.fields();

        int count = 0;

        while (fields.hasNext()) {
            list.add(fields.next().getValue());
            count++;
            if (count == 5) break;
        }

        return list;
    }

    @PostMapping
    public RecipeResponse createRecipe(@Valid @RequestBody RecipeRequest request)
            throws Exception {

        Recipe saved = recipeService.createRecipe(request);

        return recipeService.convertToResponse(saved);
    }

    @GetMapping("/top")
    public TopRecipesResponse getTopRecipes(@RequestParam(defaultValue = "5") int limit) {

        List<RecipeResponse> recipes = recipeService.getTopRecipes(limit);

        return new TopRecipesResponse(recipes);
    }
}