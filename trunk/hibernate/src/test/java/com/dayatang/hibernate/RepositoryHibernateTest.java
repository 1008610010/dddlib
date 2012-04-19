/**
 *
 */
package com.dayatang.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ValidationException;

import org.dbunit.DatabaseUnitException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dayatang.commons.domain.Dictionary;
import com.dayatang.commons.domain.DictionaryCategory;
import com.dayatang.commons.repository.HibernateUtils;
import com.dayatang.domain.AbstractEntity;
import com.dayatang.domain.ExampleSettings;
import com.dayatang.domain.QuerySettings;

/**
 * 
 * @author yang
 */
public class RepositoryHibernateTest {

	private static SessionFactory sessionFactory;
	
	private Session session;

	private Transaction tx;

	private static EntityRepositoryHibernate repository;

	private DictionaryCategory gender;

	private DictionaryCategory education;

	private Dictionary male;

	private Dictionary undergraduate;

	@BeforeClass
	public static void setUpClass() throws DatabaseUnitException, SQLException, Exception {
		sessionFactory = HibernateUtils.getSessionFactory();
	}
	
	@AfterClass
	public static void tearDownClass() {
		sessionFactory.close();
	}
	
	@Before
	public void setUp() {
		session = sessionFactory.getCurrentSession();
		tx = session.beginTransaction();
		repository = new EntityRepositoryHibernate(sessionFactory);
		AbstractEntity.setRepository(repository);
		gender = createCategory("gender", 1);
		education = createCategory("education", 2);
		male = createDictionary("01", "男", gender, 100, "01");
		undergraduate = createDictionary("01", "本科", education, 200, "05");
	}

	@After
	public void tearDown() {
		tx.rollback();
		if (session.isOpen()) {
			session.close();
		}
		AbstractEntity.setRepository(null);
	}

	@Test
	public void testAddAndRemove() {
		Dictionary dictionary = new Dictionary("2001", "双硕士", gender);
		dictionary = repository.save(dictionary);
		assertNotNull(dictionary.getId());
		repository.remove(dictionary);
		assertNull(repository.get(Dictionary.class, dictionary.getId()));
	}

	@Test
	public void testValidateFailure() {
		Dictionary dictionary = new Dictionary("", "", gender);
		try {
			dictionary.save();
			fail("应抛出异常！");
		} catch (ValidationException e) {
			System.out.println(e.getMessage());
			assertTrue(true);
		}
	}

	@Test
	public void testExistsById() {
		assertTrue(repository.exists(Dictionary.class, male.getId()));
		assertFalse(repository.exists(Dictionary.class, 1000L));
	}

	@Test
	public void testGet() {
		assertEquals(male, repository.get(Dictionary.class, male.getId()));
	}

	@Test
	public void testLoad() {
		assertEquals(male.getId(), repository.load(Dictionary.class, male.getId()).getId());
	}

	@Test
	public void testGetUnmodified() {
		male.setText("xyz");
		Dictionary unmodified = repository.getUnmodified(Dictionary.class, male);
		assertEquals("男", unmodified.getText());
		assertEquals("xyz", male.getText());
	}

	@Test
	public void testFindQueryStringArrayParams() {
		String queryString = "select o from  Dictionary o where o.category = ?";
		Object[] params = new Object[] { gender };
		List<Dictionary> results = repository.find(queryString, params, Dictionary.class);
		assertTrue(results.contains(male));
		assertFalse(results.contains(undergraduate));
	}

	@Test
	public void testFindQueryStringMapParams() {
		String queryString = "select o from  Dictionary o where o.category = :category";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("category", gender);
		List<Dictionary> results = repository.find(queryString, params, Dictionary.class);
		assertTrue(results.contains(male));
		assertFalse(results.contains(undergraduate));
	}

	@Test
	public void testFindNamedQueryArrayParams() {
		Object[] params = new Object[] { gender, "01" };
		List<Dictionary> results = repository.findByNamedQuery("findByCategoryAndCode", params, Dictionary.class);
		assertTrue(results.contains(male));
		assertFalse(results.contains(undergraduate));
	}

	@Test
	public void testFindNamedQueryMapParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("category", gender);
		List<Dictionary> results = repository.findByNamedQuery("findByCategory", params, Dictionary.class);
		assertTrue(results.contains(male));
		assertFalse(results.contains(undergraduate));
	}

	@Test
	public void testFindByExample() {
		Dictionary example = new Dictionary(null, "本", null);
		List<Dictionary> dictionaries = repository.findByExample(example, ExampleSettings.create(Dictionary.class).excludeZeroes());
		assertFalse(dictionaries.contains(male));
		assertFalse(dictionaries.contains(undergraduate));
		dictionaries = repository.findByExample(example, ExampleSettings.create(Dictionary.class).excludeZeroes().enableLike());
		assertTrue(dictionaries.contains(undergraduate));
		assertFalse(dictionaries.contains(male));
	}

	@Test
	public void testGetSingleResultSettings() {
		QuerySettings<Dictionary> settings = QuerySettings.create(Dictionary.class).eq("category", gender)
				.eq("code", "01");
		Dictionary dictionary = repository.getSingleResult(settings);
		assertEquals(male, dictionary);
	}

	@Test
	public void testGetSingleResultArray() {
		String queryString = "select o from  Dictionary o where o.category = ? and o.code = ?";
		Object[] params = new Object[] { gender, "01" };
		Dictionary dictionary = (Dictionary) repository.getSingleResult(queryString, params, Dictionary.class);
		assertEquals(male, dictionary);
	}

	@Test
	public void testGetSingleResultMap() {
		String queryString = "select o from  Dictionary o where o.category = :category and o.code = :code";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("category", gender);
		params.put("code", "01");
		Dictionary dictionary = (Dictionary) repository.getSingleResult(queryString, params, Dictionary.class);
		assertEquals(male, dictionary);
	}

	@Test
	public void testExecuteUpdateArrayParams() {
		String description = "abcd";
		String queryString = "update Dictionary o set o.description = ? where o.category = ?";
		repository.executeUpdate(queryString, new Object[] { description, gender });
		session.clear();
		QuerySettings<Dictionary> settings = QuerySettings.create(Dictionary.class).eq("category", gender);
		List<Dictionary> results = repository.find(settings);
		assertTrue(results.size() > 0);
		for (Dictionary dictionary : results) {
			assertEquals(description, dictionary.getDescription());
		}
	}

	@Test
	public void testExecuteUpdateMapParams() {
		String description = "abcd";
		String queryString = "update Dictionary set description = :description where category = :category";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("category", gender);
		params.put("description", description);
		repository.executeUpdate(queryString, params);
		session.clear();
		QuerySettings<Dictionary> settings = QuerySettings.create(Dictionary.class).eq("category", gender);
		List<Dictionary> results = repository.find(settings);
		assertTrue(results.size() > 0);
		for (Dictionary dictionary : results) {
			assertEquals(description, dictionary.getDescription());
		}
	}


	private DictionaryCategory createCategory(String name, int sortOrder) {
		DictionaryCategory category = new DictionaryCategory();
		category.setName(name);
		category.setSortOrder(sortOrder);
		session.persist(category);
		return category;
	}

	private Dictionary createDictionary(String code, String text, DictionaryCategory category, int sortOrder,
			String parentCode) {
		Dictionary dictionary = new Dictionary(code, text, category);
		dictionary.setSortOrder(sortOrder);
		dictionary.setParentCode(parentCode);
		session.persist(dictionary);
		return dictionary;
	}
}
