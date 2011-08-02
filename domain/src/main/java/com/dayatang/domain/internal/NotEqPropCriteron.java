package com.dayatang.domain.internal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.dayatang.domain.QueryCriterion;

public class NotEqPropCriteron implements QueryCriterion {
	
	private String otherProp;
	private String propName;

	public NotEqPropCriteron(String propName, String otherProp) {
		this.propName = propName;
		this.otherProp = otherProp;
	}

	public String getOtherProp() {
		return otherProp;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other)
			return true;
		if (!(other instanceof NotEqPropCriteron))
			return false;
		NotEqPropCriteron castOther = (NotEqPropCriteron) other;
		return new EqualsBuilder()
			.append(this.getPropName(), castOther.getPropName())
			.append(otherProp, castOther.otherProp).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getPropName()).append(otherProp).toHashCode();
	}

	@Override
	public String toString() {
		return getPropName() + " != " + otherProp;
	}

	public String getPropName() {
		return propName;
	}

	
}
