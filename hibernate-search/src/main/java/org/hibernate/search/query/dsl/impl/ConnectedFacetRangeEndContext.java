package org.hibernate.search.query.dsl.impl;

import org.hibernate.search.query.dsl.FacetRangeEndContext;
import org.hibernate.search.query.dsl.FacetRangeLimitContext;
import org.hibernate.search.query.facet.FacetRequest;

/**
 * @author Hardy Ferentschik
 */
public class ConnectedFacetRangeEndContext<N extends Number> implements FacetRangeEndContext<N> {
	private final FacetBuildingContext context;

	public ConnectedFacetRangeEndContext(FacetBuildingContext context) {
		this.context = context;
	}

	public FacetRangeEndContext<N> excludeLimit() {
		context.setIncludeRangeEnd( false );
		return this;
	}

	public FacetRangeLimitContext<N> from(N rangeStart) {
		context.makeRange();
		context.setRangeStart( rangeStart );
		return new ConnectedFacetRangeLimitContext<N>( context );
	}

	public FacetRequest createFacet() {
		context.makeRange();
		return context.getFacetRequest();
	}
}


