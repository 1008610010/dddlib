package com.dayatang.domain.internal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.dayatang.domain.QueryCriteron;


public class IsEmptyCriteron extends QueryCriteron {

	public IsEmptyCriteron(String propName) {
		super(propName);
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other)
			return true;
		if (!(other instanceof IsEmptyCriteron))
			return false;
		IsEmptyCriteron castOther = (IsEmptyCriteron) other;
		return new EqualsBuilder()
			.append(this.getPropName(), castOther.getPropName())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getPropName()).toHashCode();
	}

	@Override
	public String toString() {
		return getPropName() + " is empty";
	}
	
}
