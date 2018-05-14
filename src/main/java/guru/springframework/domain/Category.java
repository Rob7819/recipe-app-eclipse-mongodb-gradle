package guru.springframework.domain;

import lombok.*;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Category {

	@Id
    private String id;
	
    private String description;
    
    @DBRef
    private Set<Recipe> recipes;

}
