package guru.springframework.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;


@Getter
@Setter
@Document
public class UnitOfMeasure {

	@Id
    private String id;
	
    private String description;

}
