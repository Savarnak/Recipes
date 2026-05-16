package com.example.recipes.repository;

import com.example.recipes.model.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class RecipeRepositoryTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    void filtersRecipesByCuisine() {
        Recipe italian = recipe("Pasta", "Italian", 4.2f);
        Recipe indian = recipe("Dal", "Indian", 4.7f);
        recipeRepository.save(italian);
        recipeRepository.save(indian);

        var page = recipeRepository.findByCuisineIgnoreCaseContaining("ital", PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("Italian", page.getContent().get(0).getCuisine());
    }

    private Recipe recipe(String title, String cuisine, float rating) {
        Recipe recipe = new Recipe();
        recipe.setTitle(title);
        recipe.setCuisine(cuisine);
        recipe.setRating(rating);
        recipe.setPrepTime(10);
        recipe.setCookTime(20);
        recipe.setTotalTime(30);
        recipe.setServes("2");
        return recipe;
    }
}
