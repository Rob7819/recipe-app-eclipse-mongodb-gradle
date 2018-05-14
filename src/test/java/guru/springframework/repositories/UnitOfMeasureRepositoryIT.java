package guru.springframework.repositories;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import guru.springframework.bootstrap.RecipeBootstrap;
import guru.springframework.domain.UnitOfMeasure;

@RunWith(SpringRunner.class)
//replace jpa test for mongo test
@DataMongoTest
public class UnitOfMeasureRepositoryIT {

	@Autowired
	UnitOfMeasureRepository unitOfMeasureRepository;
	
	//added for mongo
	@Autowired
	CategoryRepository categoryRepository;
	
	//added for mongo
	@Autowired
	RecipeRepository recipeRepository;
	
	@Before
	public void setUp() throws Exception {
		//this entire method needed to be reworked for mongo
		recipeRepository.deleteAll();
		unitOfMeasureRepository.deleteAll();
		categoryRepository.deleteAll();
				
		RecipeBootstrap recipeBootstrap = new RecipeBootstrap(categoryRepository, recipeRepository, unitOfMeasureRepository);
		
		recipeBootstrap.onApplicationEvent(null);
	}

	@Test
	//used to keep individual test to own context to prevent influencing other tests
	//@DirtiesContext
	public void FindByDescription() {
		
		Optional<UnitOfMeasure> uomOptional = unitOfMeasureRepository.findByDescription("Teaspoon");
		
		assertEquals("Teaspoon", uomOptional.get().getDescription());
	}
	
	@Test
	public void FindByDescriptionCup() {
		
		Optional<UnitOfMeasure> uomOptional = unitOfMeasureRepository.findByDescription("Cup");
		
		assertEquals("Cup", uomOptional.get().getDescription());
	}

}
