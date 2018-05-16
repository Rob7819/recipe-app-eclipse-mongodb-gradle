package guru.springframework.repositories.reactive;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import guru.springframework.domain.UnitOfMeasure;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryTest {
	
	public static final String EACH = "Each";

	@Autowired
	UnitOfMeasureReactiveRepository uomReactiveRepo;
	
	@Before
	public void setUp() throws Exception {
		//this entire method needed to be reworked for mongo
		
		uomReactiveRepo.deleteAll().block();
						
		
	}
	
	@Test
	public void testSave() {
		
		UnitOfMeasure uom = new UnitOfMeasure();
		uom.setDescription(EACH);
		
		uomReactiveRepo.save(uom).block();
		
		Long count = uomReactiveRepo.count().block();
		
		assertEquals(Long.valueOf(1L), count);
	}

	@Test
	public void testFindByDescription() {
		
		UnitOfMeasure uom = new UnitOfMeasure();
		uom.setDescription(EACH);
		
		uomReactiveRepo.save(uom).block();
		
		UnitOfMeasure fetchedUOM = uomReactiveRepo.findByDescription(EACH).block();
		
		assertEquals(EACH, fetchedUOM.getDescription());
	}
	
	


}
