package org.hibernate.search.query.dsl.impl;

import org.hibernate.search.query.dsl.FacetRangeEndContext;
import org.hibernate.search.query.dsl.FacetRangeLimitContext;

/**
 * @author Hardy Ferentschik
 */
public class ConnectedFacetRangeLimitContext<N extends Number> implements FacetRangeLimitContext<N> {
	private final FacetBuildingContext context;

	public ConnectedFacetRangeLimitContext(FacetBuildingContext context) {
		this.context = context;
	}

	public FacetRangeLimitContext<N> excludeLimit() {
		context.setIncludeRangeStart( false );
		return this;
	}

	public FacetRangeEndContext<N> to(N upperLimit) {
		context.setRangeEnd( upperLimit );
		return new ConnectedFacetRangeEndContext( context );
	}

}


