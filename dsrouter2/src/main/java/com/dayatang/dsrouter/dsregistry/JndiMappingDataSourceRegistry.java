package com.dayatang.dsrouter.dsregistry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.dayatang.utils.Slf4jLogger;

/**
 * 数据源注册表实现，将租户数据源映射到JNDI
 * 
 * @author yyang
 * 
 */
public class JndiMappingDataSourceRegistry extends AbstractDataSourceRegistry {

	private static final Slf4jLogger LOGGER = Slf4jLogger.getLogger(JndiMappingDataSourceRegistry.class);
	private Context context;
	private String jndiPrefix;
	private String jndiSuffix;

	public Context getContext() {
		if (context == null) {
			try {
				context = new InitialContext();
			} catch (NamingException e) {
				throw new RuntimeException("Cannot initiate JNDI environment!", e);
			}
		}
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setJndiPrefix(String jndiPrefix) {
		this.jndiPrefix = jndiPrefix;
	}

	public void setJndiSuffix(String jndiSuffix) {
		this.jndiSuffix = jndiSuffix;
	}

	@Override
	protected DataSource findOrCreateDataSourceForTenant(String tenant) {
		DataSource result = null;
		String dataSourceJndi = StringUtils.defaultString(jndiPrefix) + tenant + StringUtils.defaultString(jndiSuffix);
		try {
			result = (DataSource) context.lookup(dataSourceJndi);
		} catch (NamingException e) {
			String message = "Lookup jndi: " + dataSourceJndi + " failed!";
			LOGGER.error(message, e);
			throw new RuntimeException(message, e);
		}
		if (result == null) {
			throw new RuntimeException("There's no data source prepared for tenant " + tenant);
		}
		LOGGER.info("Found JNDI " + dataSourceJndi + " for tenant {}", tenant);
		return result;
	}
}
