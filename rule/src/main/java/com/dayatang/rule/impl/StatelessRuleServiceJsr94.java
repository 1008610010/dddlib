package com.dayatang.rule.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetCreateException;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dayatang.rule.RuleRuntimeException;
import com.dayatang.rule.StatelessRuleService;
import com.dayatang.rule.UnSupportedRuleFormatException;

@SuppressWarnings("rawtypes")
public class StatelessRuleServiceJsr94 implements StatelessRuleService {

	private static final long serialVersionUID = -6550908446842944288L;
	
	private RuleAdministrator ruleAdministrator;
	private LocalRuleExecutionSetProvider ruleExecutionSetProvider;
	private RuleRuntime ruleRuntime;
	

	protected static Logger LOGGER = LoggerFactory.getLogger(StatelessRuleServiceJsr94.class);

	public StatelessRuleServiceJsr94(RuleServiceProvider ruleServiceProvider) {
		this(ruleServiceProvider, null);
	}
	
	public StatelessRuleServiceJsr94(RuleServiceProvider ruleServiceProvider, Map properties) {
		try {
			ruleAdministrator = ruleServiceProvider.getRuleAdministrator();
			ruleExecutionSetProvider = ruleAdministrator.getLocalRuleExecutionSetProvider(properties);
			ruleRuntime = ruleServiceProvider.getRuleRuntime();
			LOGGER.info("The rule service provider of JSR94 is " + ruleServiceProvider.getClass());
		} catch (Exception e) {
			throw new RuleRuntimeException("Cannot create Rule Service!!", e);
		}
	}

	@Override
	public List executeRules(String ruleSource, Map executionSetProperties, Map sessionProperties, List params) {
		RuleExecutionSet ruleExecutionSet = createRuleExecutionSet(ruleSource, executionSetProperties);
		StatelessRuleSession session = createRuleSession(ruleExecutionSet, sessionProperties);
		return executeRules(session, params);
	}

	private RuleExecutionSet createRuleExecutionSet(String ruleSource, Map executionSetProperties) {
		return createRuleExecutionSet(new StringReader(ruleSource), executionSetProperties);
	}

	@Override
	public List executeRules(Reader ruleSource, Map executionSetProperties, Map sessionProperties, List params) {
		RuleExecutionSet ruleExecutionSet = createRuleExecutionSet(ruleSource, executionSetProperties);
		StatelessRuleSession session = createRuleSession(ruleExecutionSet, sessionProperties);
		return executeRules(session, params);
	}

	private RuleExecutionSet createRuleExecutionSet(Reader ruleSource, Map executionSetProperties) {
		try {
			return ruleExecutionSetProvider.createRuleExecutionSet(ruleSource, executionSetProperties);
		} catch (RuleExecutionSetCreateException e) {
			throw new UnSupportedRuleFormatException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List executeRules(InputStream ruleSource, Map executionSetProperties, Map sessionProperties, List params) {
		RuleExecutionSet ruleExecutionSet = createRuleExecutionSet(ruleSource, executionSetProperties);
		StatelessRuleSession session = createRuleSession(ruleExecutionSet, sessionProperties);
		return executeRules(session, params);
	}

	private RuleExecutionSet createRuleExecutionSet(InputStream ruleSource, Map executionSetProperties) {
		try {
			return ruleExecutionSetProvider.createRuleExecutionSet(ruleSource, executionSetProperties);
		} catch (RuleExecutionSetCreateException e) {
			throw new UnSupportedRuleFormatException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List executeRules(Object ruleSource, Map executionSetProperties, Map sessionProperties, List params) {
		RuleExecutionSet ruleExecutionSet = createRuleExecutionSet(ruleSource, executionSetProperties);
		StatelessRuleSession session = createRuleSession(ruleExecutionSet, sessionProperties);
		return executeRules(session, params);
	}

	private RuleExecutionSet createRuleExecutionSet(Object ruleSource, Map executionSetProperties) {
		try {
			return ruleExecutionSetProvider.createRuleExecutionSet(ruleSource, executionSetProperties);
		} catch (RuleExecutionSetCreateException e) {
			throw new UnSupportedRuleFormatException(e);
		}
	}

	private StatelessRuleSession createRuleSession(RuleExecutionSet ruleExecutionSet, Map sessionProperties) {
		try{
			String packageName = ruleExecutionSet.getName();
			ruleAdministrator.registerRuleExecutionSet(packageName, ruleExecutionSet, null);
			return (StatelessRuleSession) ruleRuntime.createRuleSession(packageName, sessionProperties, RuleRuntime.STATELESS_SESSION_TYPE);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuleRuntimeException("Cannot create Rule Session!!!", e);
		}
	}

	private List executeRules(StatelessRuleSession session, List params) {
		List results;
		StopWatch watch = null;
		if (LOGGER.isDebugEnabled()) {
			watch = new StopWatch();
			watch.start();
		}

		try{
			results = session.executeRules(params);
		} catch (Exception e) {
			throw new RuleRuntimeException(e);
		} finally {
			try {
				session.release();
			} catch (Exception e) {
				throw new RuleRuntimeException("Cannot release rule session!!", e);
			}
		}

		if (LOGGER.isDebugEnabled()) {
			watch.stop();
			LOGGER.debug("Rule session executed in " + watch);
			// for gc
			watch = null;
		}
		
		return results;
	}

	@Override
	public List executeRules(String ruleSource, List params) {
		return executeRules(ruleSource, null, null, params);
	}

	@Override
	public List executeRules(InputStream ruleSource, List params) {
		return executeRules(ruleSource, null, null, params);
	}

	@Override
	public List executeRules(Reader ruleSource, List params) {
		return executeRules(ruleSource, null, null, params);
	}

	@Override
	public List executeRules(Object ruleSource, List params) {
		return executeRules(ruleSource, null, null, params);
	}
}
