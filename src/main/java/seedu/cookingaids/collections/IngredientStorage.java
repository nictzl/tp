package seedu.cookingaids.collections;

import seedu.cookingaids.items.ExpiryDate;
import seedu.cookingaids.items.Ingredient;
import seedu.cookingaids.ui.Ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class IngredientStorage {
    private static final HashMap<String, List<Ingredient>> ingredients = new HashMap<>();

    public static void addToStorage(Ingredient newIngredient) {
        String name = newIngredient.getName();
        List<Ingredient> ingredientList = ingredients.getOrDefault(name, new ArrayList<>());

        // Check if an ingredient with the same expiry date exists, then add quantity
        for (Ingredient ing : ingredientList) {
            if ((newIngredient.getExpiryDate().getDateLocalDate() == null &&
                    ing.getExpiryDate().getDateLocalDate() == null) ||
                    (newIngredient.getExpiryDate().getDateLocalDate() != null &&
                            newIngredient.getExpiryDate().getDateLocalDate()
                            .equals(ing.getExpiryDate().getDateLocalDate()))) {
                ing.addQuantity(newIngredient.getQuantity());
                return;
            }
        }

        // Otherwise, add a new ingredient entry
        ingredientList.add(newIngredient);

        // Sort ingredients by expiry date (null = last)
        ingredientList.sort(Comparator.comparing(
                Ingredient::getExpiryDate,
                Comparator.comparing(ExpiryDate::getDateLocalDate, Comparator.nullsLast(Comparator.naturalOrder()))
        ));


        ingredients.put(name, ingredientList);
    }

    public static void removeIngredient(String ingredientName) {
        ingredients.remove(ingredientName);
    }

    public static HashMap<String, List<Ingredient>> getStorage() {
        return ingredients;
    }

    public static boolean contains(String ingredientName) {
        return ingredients.containsKey(ingredientName);
    }

    public static void clear() {
        ingredients.clear();
    }

    public void displayStorage() {
        HashMap<String, List<Ingredient>> ingredients = getStorage();
        Ui.printIngredientListView(ingredients);
    }

    public static List<Ingredient> getIngredients(String name) {
        return ingredients.getOrDefault(name, new ArrayList<>());
    }
}
