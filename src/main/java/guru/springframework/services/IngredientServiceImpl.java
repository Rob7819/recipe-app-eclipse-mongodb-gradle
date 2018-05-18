package guru.springframework.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

	private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
      
    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand,
            IngredientCommandToIngredient ingredientCommandToIngredient,
            RecipeReactiveRepository recipeReactiveRepository, UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository) {
    	this.ingredientToIngredientCommand = ingredientToIngredientCommand;
    	this.ingredientCommandToIngredient = ingredientCommandToIngredient;
    	this.recipeReactiveRepository = recipeReactiveRepository;
    	this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
    	
    }
	
	@Override
	public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {
		
		return recipeReactiveRepository
				.findById(recipeId)
                .flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equalsIgnoreCase(ingredientId))
                .single()
                .map(ingredient -> {
                    IngredientCommand command = ingredientToIngredientCommand.convert(ingredient);
                    command.setRecipeId(recipeId);
                    return command;
                });
		
		//Optional<Recipe> recipeOptional = recipeReactiveRepository.findById(recipeId).block();

        /*if (!recipeOptional.isPresent()){
            //todo impl error handling
            log.error("recipe id not found. Id: " + recipeId);
        }

        Recipe recipe = recipeOptional;

        Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients()
        		.stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .map( ingredient -> ingredientToIngredientCommand.convert(ingredient))
                .findFirst();

        if(!ingredientCommandOptional.isPresent()){
            //todo impl error handling
            log.error("Ingredient id not found: " + ingredientId);
        }
        
        //enhance command object with recipe id
        IngredientCommand ingredientCommand = ingredientCommandOptional.get();
        ingredientCommand.setRecipeId(recipe.getId());

        return Mono.just(ingredientCommandOptional.get());*/
		
	}
	
	@Override
    @Transactional
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
        //going to leave reactive version?  Confused with approach (@DBRef incompatible
		//with reactive mongo driver)
		Recipe recipeOptional = recipeReactiveRepository.findById(command.getRecipeId()).block();

        if(recipeOptional == null){

            //todo toss error if not found!
            log.error("Recipe not found for id: " + command.getRecipeId());
            return Mono.just(new IngredientCommand());
        } else {
            Recipe recipe = recipeOptional;

            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(command.getId()))
                    .findFirst();

            if(ingredientOptional.isPresent()){
            	//edit existing ingredient
                Ingredient ingredientFound = ingredientOptional.get();
                ingredientFound.setDescription(command.getDescription());
                ingredientFound.setAmount(command.getAmount());
                ingredientFound.setUom(unitOfMeasureReactiveRepository
                        .findById(command.getUom().getId()).block());
            //            .orElseThrow(() -> new RuntimeException("UOM NOT FOUND"))); //todo address this
            } else {
                //add new Ingredient
                Ingredient ingredient = ingredientCommandToIngredient.convert(command);
                //CD ERROR
                //ingredient.setRecipe(recipe);
                recipe.addIngredient(ingredient);
            }

            Recipe savedRecipe = recipeReactiveRepository.save(recipe).block();

            Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
                    .findFirst();

            //check by description
            if(!savedIngredientOptional.isPresent()){
            	//no id?.....  means no data (for new ingredient, id would be present for edited ingredient) so pass data and wait for id to be assigned to new ingredient?
                //not totally safe... But best guess
                savedIngredientOptional = savedRecipe.getIngredients().stream()
                        .filter(recipeIngredients -> recipeIngredients.getDescription().equals(command.getDescription()))
                        .filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
                        .filter(recipeIngredients -> recipeIngredients.getUom().getId().equals(command.getUom().getId()))
                        .findFirst();
            }

            //to do check for fail
            
            //Need to assign recipe ID as it doesn't get bound in MongoDB
            IngredientCommand ingredientCommandSaved = ingredientToIngredientCommand.convert(savedIngredientOptional.get());
            ingredientCommandSaved.setRecipeId(recipe.getId());
            
            return Mono.just(ingredientCommandSaved);
        }

    }
	
	@Override
    public Mono<Void> deleteById(String recipeId, String idToDelete) {

        log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);
        //leaving reactive version?
        Recipe recipeOptional = recipeReactiveRepository.findById(recipeId).block();

        if(recipeOptional != null){
            Recipe recipe = recipeOptional;
            log.debug("found recipe");

            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(idToDelete))
                    .findFirst();

            if(ingredientOptional.isPresent()){
                log.debug("found Ingredient");
                //CD ERROR
                //Ingredient ingredientToDelete = ingredientOptional.get();
                //ingredientToDelete.setRecipe(null);
                recipe.getIngredients().remove(ingredientOptional.get());
                recipeReactiveRepository.save(recipe).block();//this line is problematic due to @DBREF issue with reactive mongo driver -- To be resolved later...
            }
        } else {
            log.debug("Recipe Id Not found. Id:" + recipeId);
        }
        
        return Mono.empty();
    }
}
