/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.query.engine.spi;

import java.util.List;

/**
 * Projection meta data required for result loading in case of searches using projection.
 *
 * @author Hardy Ferentschik
 */
public interface ProjectionInfo {
	/**
	 * If the underlying search had projections enabled, the array of projected values is returned.
	 *
	 * @return Array of projected values, if projections are enabled, {@code null} in case there are no projections.
	 */
	Object[] getProjectedValues();

	/**
	 * Returns a list of indexes into the projected values array at which the whole entity instance is projected.
	 *
	 * @return Returns a list of indexes into the projected values array at which the whole entity instance is projected.
	 * The list is empty in case {@link org.hibernate.search.engine.ProjectionConstants#THIS} is not projected.
	 *
	 * @see #isThisProjected()
	 */
	List<Integer> getIndexesOfThisProjection();

	/**
	 * Returns {@code true} if the whole entity instance is part of the projected values, otherwise {@code false}.
	 *
	 * @return returns {@code true} in case projection is used, {@code false} otherwise.
	 *
	 * @see org.hibernate.search.engine.ProjectionConstants#THIS
	 */
	boolean isThisProjected();

	/**
	 * Called to populate the projection value array with the entity instance (if required).
	 *
	 * @param entity The loaded entity
	 */
	void populateWithEntityInstance(Object entity);
}


