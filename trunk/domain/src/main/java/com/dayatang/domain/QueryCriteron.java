package com.dayatang.domain;

public abstract class QueryCriteron {
	
	private String propName;

	public QueryCriteron(String propName) {
		this.propName = propName;
	}

	public String getPropName() {
		return propName;
	}

}