package guru.springframework.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;

public class IndexControllerTest {

    @Mock
    RecipeService recipeService;

    @Mock
    Model model;

    IndexController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        controller = new IndexController(recipeService);
    }

    @Test
    public void testMockMVC() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
    
    @Test
    public void getIndexPage() throws Exception {
    	
    	//given
    	Set<Recipe> recipes = new HashSet<>();
    	recipes.add(new Recipe());
    	
    	Recipe recipe = new Recipe();
    	recipe.setId("1");
    	
    	recipes.add(recipe);
    	
    	when(recipeService.getRecipes()).thenReturn(recipes);
    	
    	ArgumentCaptor<Set<Recipe>> argumentCaptor = ArgumentCaptor.forClass(Set.class);

    	//when
        String viewName = controller.getIndexPage(model);

        //then
        assertEquals("index", viewName);
        //verify getRecipes (of recipeService) is being called once
        verify(recipeService, times(1)).getRecipes();
        
        /*//evaluate that the attribute is being added once to model, "eq" is an equal evaluator
        //"anySet()" represents that "recipes" is a set (so we're telling mockito
        //to make sure "recipes" is the attribute and represents a set)
        verify(model, times(1)).addAttribute(eq("recipes"),anySet());*/
        
        //update verify to use argument captor
        verify(model, times(1)).addAttribute(eq("recipes"),argumentCaptor.capture());
        Set<Recipe> setInController = argumentCaptor.getValue();
        assertEquals(2, setInController.size());
    }
}