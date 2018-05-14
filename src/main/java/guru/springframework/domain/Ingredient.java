package guru.springframework.domain;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Getter
@Setter
public class Ingredient {

	//id won't get a value as it doesn't get carried over (recipeform.html
	//issue) so we use a random universal unique ID
    private String id = UUID.randomUUID().toString();
	
    private String description;
    private BigDecimal amount;
    
    @DBRef
    private UnitOfMeasure uom;
    
    //Can't have this for MongoDB, otherwise we get circular dependency errors
    //CD ERROR
    //private Recipe recipe;

    public Ingredient() {
    }

    public Ingredient(String description, BigDecimal amount, UnitOfMeasure uom) {
        this.description = description;
        this.amount = amount;
        this.uom = uom;
    }

    public Ingredient(String description, BigDecimal amount, UnitOfMeasure uom, Recipe recipe) {
        this.description = description;
        this.amount = amount;
        this.uom = uom;
        //this.recipe = recipe;
    }

}
