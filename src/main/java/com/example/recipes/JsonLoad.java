package com.example.recipes;

import com.example.recipes.model.Recipe;
import com.example.recipes.repository.RecipeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "app.data-loader.enabled", havingValue = "true", matchIfMissing = true)
public class JsonLoad implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(JsonLoad.class);
    private final RecipeRepository recipeRepository;

    public JsonLoad(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (recipeRepository.count() > 0) {
            log.info("Skipping JSON load because recipes already exist");
            return;
        }

        log.info("Loading recipe JSON data into database");
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/US_recipes_null.json");
        JsonNode rootNode = mapper.readTree(inputStream);
        Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();

        List<Recipe> batch = new ArrayList<>();
        int insertedCount = 0;

        while (fields.hasNext()) {
            JsonNode node = fields.next().getValue();
            Recipe recipe = new Recipe();
            recipe.setTitle(readText(node, "title"));
            recipe.setCuisine(readText(node, "cuisine"));
            recipe.setRating(node.has("rating") && !node.get("rating").isNull() ? (float) node.get("rating").asDouble() : null);

            int prepTime = readInt(node, "prep_time");
            int cookTime = readInt(node, "cook_time");
            recipe.setPrepTime(prepTime);
            recipe.setCookTime(cookTime);
            recipe.setTotalTime(prepTime + cookTime);
            recipe.setDescription(readText(node, "description"));
            recipe.setNutrients(node.has("nutrients") && !node.get("nutrients").isNull() ? node.get("nutrients").toString() : null);
            recipe.setServes(readText(node, "serves"));

            batch.add(recipe);
            insertedCount++;

            if (batch.size() == 100) {
                recipeRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            recipeRepository.saveAll(batch);
        }

        log.info("Successfully inserted {} recipes into database", insertedCount);
    }

    private String readText(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }

    private int readInt(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asInt() : 0;
    }
}
