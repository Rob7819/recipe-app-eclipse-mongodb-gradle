package guru.springframework.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {
	//Note: '.setControllerAdvice(new ControllerExceptionHandler())' needs to
	//be added to MockMvc builder in setup, for all individual controller testing 
	//modules affected by exception handler(s) in this class...
	
	//ControllerAdvice is a global control over individual controllers, used
	//to avoid duplicating code. For example, handleBadRequest is used for
	//both RecipeController and ImageController, MockMvc builder for related
	//testing modules will need to be modified as described above...
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NumberFormatException.class)
    public ModelAndView handleBadRequest(Exception exception){
		//add Exception parameter to tell spring to take in more detailed
		//exception message.  In this case NumberFormatException is a built in 
		//class, so it is handled automatically.
		
        log.error("Handling number format exception");
        //display the new exception message to log
        log.error(exception.getMessage());

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("4XXerror");
        //Need to pass new message to 4xx (400) error html page so we add
        //it to the model as object
        modelAndView.addObject("exception", exception);

        return modelAndView;
    }

}
