
package seedu.cookingaids.suggest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import seedu.cookingaids.collections.IngredientStorage;
import seedu.cookingaids.collections.RecipeBank;
import seedu.cookingaids.exception.OverflowQuantityException;
import seedu.cookingaids.items.Ingredient;
import seedu.cookingaids.items.Recipe;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class SuggestTest {

    private MockedStatic<IngredientStorage> mockedIngredientStorage;
    private MockedStatic<RecipeBank> mockedRecipeBank;

    @BeforeEach
    void setUp() throws OverflowQuantityException {
        mockedIngredientStorage = mockStatic(IngredientStorage.class);
        mockedRecipeBank = mockStatic(RecipeBank.class);

        // Prepare test ingredients
        List<Ingredient> testIngredients = List.of(
                new Ingredient( "pasta", 1),
                new Ingredient( "chicken", 2),
                new Ingredient( "sauce", 3),
                new Ingredient( "milk", 4),
                new Ingredient("cheese", 5)
        );

        // Simulate a real storage by using a HashMap
        HashMap<String, List<Ingredient>> ingredientMap = new HashMap<>();

        // Mock addToStorage behavior
        mockedIngredientStorage.when(() -> IngredientStorage.addToStorage(any(Ingredient.class)))
                .thenAnswer(invocation -> {
                    Ingredient ingredient = invocation.getArgument(0);
                    ingredientMap.computeIfAbsent(ingredient.getName(), k -> new ArrayList<>()).add(ingredient);
                    return null; // addToStorage returns void
                });

        // Add ingredients to the mock storage
        for (Ingredient testIngredient : testIngredients) {
            IngredientStorage.addToStorage(testIngredient);
        }

        // Mock getStorage to return the simulated ingredient map
        mockedIngredientStorage.when(IngredientStorage::getStorage).thenReturn(ingredientMap);
    }

    @AfterEach
    void tearDown() {
        // Close the static mocks to avoid conflicts between tests
        mockedIngredientStorage.close();
        mockedRecipeBank.close();
    }



    @Test
    void suggestRecipes_someIngredientsMissing_returnsMatchingRecipesOnly() {
        // Mock ingredient storage with missing ingredients
        HashMap<String, List<Ingredient>> ingredientMap = new HashMap<>();
        ingredientMap.put("Cheese", List.of(new Ingredient("Cheese", 1)));
        ingredientMap.put("Bread", List.of(new Ingredient( "Bread", 1)));
        mockedIngredientStorage.when(IngredientStorage::getStorage).thenReturn(ingredientMap);

        // Mock recipe bank
        Recipe recipe1 = new Recipe("Grilled Cheese", new ArrayList<Ingredient>(
                List.of(
                        new Ingredient("cheese"),
                        new Ingredient( "bread")
                )
        ));
        Recipe recipe2 = new Recipe("Tomato Sandwich", new ArrayList<>(
                List.of(
                        new Ingredient( "tomato"),
                        new Ingredient( "bread")
                )
        ));
        ArrayList<Recipe> recipes = new ArrayList<>(List.of(recipe1, recipe2));
        mockedRecipeBank.when(RecipeBank::getRecipeBank).thenReturn(recipes);

        List<Recipe> suggestedRecipes = Suggest.suggestRecipes();

        assertEquals(1, suggestedRecipes.size());
        assertEquals("Grilled Cheese", suggestedRecipes.get(0).getRecipeName());
    }

    @Test
    void suggestRecipes_allIngredientsPresent_returnsMatchingRecipes() {
        // Set up the ingredient storage with all required ingredients
        HashMap<String, List<Ingredient>> ingredientMap = new HashMap<>();
        ingredientMap.put("Cheese", List.of(new Ingredient( "Cheese", 1)));
        ingredientMap.put("Bread", List.of(new Ingredient( "Bread", 1)));
        ingredientMap.put("Tomato", List.of(new Ingredient( "Tomato", 1)));
        mockedIngredientStorage.when(IngredientStorage::getStorage).thenReturn(ingredientMap);

        // Set up the recipe bank
        Recipe recipe1 = new Recipe("Grilled Cheese", new ArrayList<Ingredient>(
                List.of(
                        new Ingredient( "cheese"),
                        new Ingredient( "bread")
                )
        ));
        Recipe recipe2 = new Recipe("Tomato Sandwich", new ArrayList<>(
                List.of(
                        new Ingredient( "Tomato"),
                        new Ingredient( "bread")
                )
        ));
        ArrayList<Recipe> recipes = new ArrayList<>(List.of(recipe1, recipe2));
        mockedRecipeBank.when(RecipeBank::getRecipeBank).thenReturn(recipes);

        // Call suggestRecipes to get the list of suggested recipes
        List<Recipe> suggestedRecipes = Suggest.suggestRecipes();

        // Validate that both recipes are returned as all ingredients are available
        assertEquals(2, suggestedRecipes.size());
        List<String> suggestedRecipeNames = new ArrayList<>();
        for (Recipe recipe : suggestedRecipes) {
            suggestedRecipeNames.add(recipe.getRecipeName());
        }
        assertTrue(suggestedRecipeNames.contains("Grilled Cheese"));
        assertTrue(suggestedRecipeNames.contains("Tomato Sandwich"));
    }

}
