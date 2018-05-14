package guru.springframework.domain;

import org.springframework.data.annotation.Id;

import lombok.*;

@Getter
@Setter
public class Notes {

	@Id
    private String id;
	
	//CD ERROR
    //private Recipe recipe;
	
    private String recipeNotes;

}
