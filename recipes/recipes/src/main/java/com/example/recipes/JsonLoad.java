package com.example.recipes;

import com.example.recipes.model.Recipe;
import com.example.recipes.repository.RecipeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

@Component
public class JsonLoad implements CommandLineRunner {

    private final RecipeRepository recipeRepository;

    public JsonLoad(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // 🔹 Prevent duplicate insertion
        if (recipeRepository.count() > 0) {
            System.out.println("Recipes already loaded in DB");
            return;
        }

        System.out.println("Loading JSON data into database.");

        ObjectMapper mapper = new ObjectMapper();

        InputStream inputStream =
                getClass().getResourceAsStream("/US_recipes_null.json");

        JsonNode rootNode = mapper.readTree(inputStream);

        Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();

        int insertedCount = 0;

        while (fields.hasNext()) {

            Map.Entry<String, JsonNode> entry = fields.next();
            JsonNode node = entry.getValue();

            Recipe recipe = new Recipe();

            recipe.setTitle(
                    node.has("title") && !node.get("title").isNull()
                            ? node.get("title").asText()
                            : null
            );


            recipe.setCuisine(
                    node.has("cuisine") && !node.get("cuisine").isNull()
                            ? node.get("cuisine").asText()
                            : null
            );

            recipe.setRating(
                    node.has("rating") && !node.get("rating").isNull()
                            ? (float) node.get("rating").asDouble()
                            : null
            );


            int prepTime = node.has("prep_time") && !node.get("prep_time").isNull()
                    ? node.get("prep_time").asInt()
                    : 0;


            int cookTime = node.has("cook_time") && !node.get("cook_time").isNull()
                    ? node.get("cook_time").asInt()
                    : 0;

            recipe.setPrepTime(prepTime);
            recipe.setCookTime(cookTime);

            recipe.setTotalTime(prepTime + cookTime);

            recipe.setDescription(
                    node.has("description") && !node.get("description").isNull()
                            ? node.get("description").asText()
                            : null
            );


            recipe.setNutrients(
                    node.has("nutrients") && !node.get("nutrients").isNull()
                            ? node.get("nutrients").toString()
                            : null
            );

            recipe.setServes(
                    node.has("serves") && !node.get("serves").isNull()
                            ? node.get("serves").asText()
                            : null
            );

            recipeRepository.save(recipe);
            insertedCount++;
        }

        System.out.println("Successfully inserted " + insertedCount + " recipes into database.");
    }
}