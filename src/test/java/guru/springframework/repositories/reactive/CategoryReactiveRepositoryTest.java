package guru.springframework.repositories.reactive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import guru.springframework.domain.Category;

@RunWith(SpringRunner.class)
@DataMongoTest
public class CategoryReactiveRepositoryTest {
	
	public static final String POLISH = "Polish";
	
	@Autowired
	CategoryReactiveRepository catReactiveRepo;

	@Before
	public void setUp() throws Exception {
		catReactiveRepo.deleteAll().block();
	}

	@Test
	public void testSave() {
		Category cat = new Category();
		cat.setDescription(POLISH);
		
		catReactiveRepo.save(cat).block();
		
		Long count = catReactiveRepo.count().block();
		
		assertEquals(Long.valueOf(1L), count);
	}
	
	@Test
	public void testFetch() {
		
		Category cat = new Category();
		cat.setDescription(POLISH);
		
		catReactiveRepo.save(cat).block();
		
		Category FetchedCategory = catReactiveRepo.findByDescription(POLISH).block();
		
		assertNotNull(FetchedCategory.getId());
		//assertEquals(POLISH, FetchedCategory.getDescription());
		
	}

}
