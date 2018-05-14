package guru.springframework.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.RecipeService;

public class RecipeControllerTest {
	
	@Mock
	RecipeService recipeService;
	
	RecipeController controller;
	
	MockMvc mockMvc;
	
	ArgumentCaptor<Long> argumentCaptorLong;
	
	ArgumentCaptor<RecipeCommand> argumentCaptorRecipeCommand;
	
	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
		controller = new RecipeController(recipeService);
		
		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
		
		argumentCaptorRecipeCommand = ArgumentCaptor.forClass(RecipeCommand.class);
		
	}
	
	@Test
	public void testGetRecipe() throws Exception {
		
		Recipe recipe = new Recipe();
		recipe.setId("1");
		
		
		
		when(recipeService.findById(anyString())).thenReturn(recipe);
		
		mockMvc.perform(get("/recipe/1/show"))
				.andExpect(status().isOk())
				.andExpect(view().name("recipe/show"))
				.andExpect(model().attributeExists("recipe"));
		
	}
	
	@Test
    public void testGetRecipeNotFound() throws Exception {

        when(recipeService.findById(anyString())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/recipe/1/show"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404error"));
    }
	
	@Test
    public void testGetNewRecipeForm() throws Exception {
        RecipeCommand command = new RecipeCommand();

        mockMvc.perform(get("/recipe/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/recipeform"))
                .andExpect(model().attributeExists("recipe"));
    }

    @Test
    public void testPostNewRecipeForm() throws Exception {
    	//Passing (posting) of user entered form data
        RecipeCommand command = new RecipeCommand();
        command.setId("2");

        when(recipeService.saveRecipeCommand(argumentCaptorRecipeCommand.capture())).thenReturn(command);

        //"directions" needed to be added for test to pass since validation
        //was added in RecipeCommand
        mockMvc.perform(post("/recipe")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "")
                .param("description", "some string")
                .param("directions", "some directions")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/2/show"));
    }
    
    @Test
    public void testPostNewRecipeFormValidationFail() throws Exception {
    	//returning back to the input form after a validation fail
        RecipeCommand command = new RecipeCommand();
        command.setId("2");

        when(recipeService.saveRecipeCommand(any())).thenReturn(command);

        mockMvc.perform(post("/recipe")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "")

        )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"))
                .andExpect(view().name("recipe/recipeform"));
    }

    @Test
    public void testGetUpdateView() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId("2");

        when(recipeService.findCommandById(anyString())).thenReturn(command);

        mockMvc.perform(get("/recipe/1/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/recipeform"))
                .andExpect(model().attributeExists("recipe"));
    }
    
    @Test
    public void testDeleteAction() throws Exception {
    	
    	mockMvc.perform(get("/recipe/1/delete"))
    		.andExpect(status().is3xxRedirection())
    		.andExpect(view().name("redirect:/"));
    	
    	verify(recipeService, times(1)).deleteById(anyString());
    	
    }

}
