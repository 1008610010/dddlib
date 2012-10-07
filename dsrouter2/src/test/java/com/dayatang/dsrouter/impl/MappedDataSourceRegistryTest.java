package com.dayatang.dsrouter.impl;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MappedDataSourceRegistryTest {
	
	private MappedDataSourceRegistry instance;
	private DataSourceCreator dataSourceCreator;
	private DataSource dataSource;

	@Before
	public void setUp() throws Exception {
		String tenantId = "abc";
		instance = new MappedDataSourceRegistry();
		dataSourceCreator = mock(DataSourceCreator.class);
		instance.setDataSourceCreator(dataSourceCreator);
		dataSource = mock(DataSource.class);
		when(dataSourceCreator.createDataSource(tenantId)).thenReturn(dataSource);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetOrCreateDataSourceByTenantId() {
		String tenantId = "abc";
		assertSame(dataSource, instance.getOrCreateDataSourceByTenantId(tenantId));
		verify(dataSourceCreator).createDataSource(tenantId);
		reset(dataSourceCreator);
		assertSame(dataSource, instance.getOrCreateDataSourceByTenantId(tenantId));
		verify(dataSourceCreator, never()).createDataSource(tenantId);
		assertEquals(1, instance.size());
		instance.clear();
		assertEquals(0, instance.size());
	}
}