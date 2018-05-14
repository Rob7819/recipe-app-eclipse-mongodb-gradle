package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;

public interface IngredientService {

	IngredientCommand findByRecipeIdAndIngredientId(String recipeId, String id);
	
	IngredientCommand saveIngredientCommand(IngredientCommand command);

	void deleteById(String recipeId, String id);
	
}
