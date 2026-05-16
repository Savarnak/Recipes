package com.example.recipes.controller;

import com.example.recipes.dto.RecipeRequest;
import com.example.recipes.dto.RecipeResponse;
import com.example.recipes.dto.TopRecipesResponse;
import com.example.recipes.dto.PagedResponse;
import com.example.recipes.service.RecipeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private static final Logger log = LoggerFactory.getLogger(RecipeController.class);
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "title", "cuisine", "rating", "prepTime", "cookTime", "totalTime");
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/parsed")
    @Operation(summary = "Preview parsed source data", description = "Returns the first five recipe records from the bundled JSON dataset.")
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
    @Operation(summary = "Create recipe")
    @ApiResponse(
            responseCode = "201",
            description = "Recipe created",
            content = @Content(examples = @ExampleObject("""
                    {"id":1,"title":"Paneer Curry","cuisine":"Indian","rating":4.7,"prep_time":15,"cook_time":30,"total_time":45,"serves":"4"}
                    """))
    )
    public ResponseEntity<RecipeResponse> createRecipe(@Valid @RequestBody RecipeRequest request)
            throws Exception {
        log.info("POST /recipes requested for title='{}'", request.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.createRecipe(request));
    }

    @GetMapping("/top")
    public TopRecipesResponse getTopRecipes(@RequestParam(defaultValue = "5") int limit) {

        List<RecipeResponse> recipes = recipeService.getTopRecipes(limit);

        return new TopRecipesResponse(recipes);
    }

    @GetMapping
    @Operation(summary = "List recipes", description = "Supports pagination, optional cuisine filtering, and sorting.")
    public ResponseEntity<PagedResponse<RecipeResponse>> getRecipes(
            @RequestParam(required = false) String cuisine,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("GET /recipes requested cuisine={} page={} size={} sortBy={} direction={}", cuisine, page, size, sortBy, direction);
        if (page < 0) {
            throw new IllegalArgumentException("Page must be zero or greater");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Size must be greater than zero");
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Unsupported sort field: " + sortBy);
        }
        Sort sort = "desc".equalsIgnoreCase(direction) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(recipeService.getRecipes(cuisine, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recipe by ID")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable Integer id) {
        log.info("GET /recipes/{} requested", id);
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update recipe")
    public ResponseEntity<RecipeResponse> updateRecipe(@PathVariable Integer id, @Valid @RequestBody RecipeRequest request)
            throws Exception {
        log.info("PUT /recipes/{} requested", id);
        return ResponseEntity.ok(recipeService.updateRecipe(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete recipe")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Integer id) {
        log.info("DELETE /recipes/{} requested", id);
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
