/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.query.engine.spi;

import java.io.Serializable;

/**
 * Wrapper class containing information about an entity required to load the entity after a search hit.
 *
 * @author Emmanuel Bernard <emmanuel@hibernate.org>
 * @author Hardy Ferentschik
 */
public interface EntityInfo {
	/**
	 * Returns the {@code Class} this {@code EntityInfo} instance represents.
	 *
	 * @return the {@code Class} this {@code EntityInfo} instance represents.
	 */
	Class<?> getClazz();

	/**
	 * The entity id.
	 *
	 * In case of ORM this is the primary key of the entity.
	 *
	 * @return The entity id.
	 */
	Serializable getEntityId();

	/**
	 * Returns the name of the entity id property name.
	 *
	 * @return returns the name of the entity id property name
	 */
	String getEntityIdName();

	/**
	 * The document id.
	 *
	 * This is the property value used as document id for the Lucene index
	 *
	 * @return The the document id.
	 */
	Serializable getDocumentId();

	/**
	 * Returns the name of the document id property name.
	 *
	 * @return returns the name of the document id property name
	 */
	String getDocumentIdName();

	/**
	 * Returns an instance of {@code ProjectionInfo} in case projection was used.
	 *
	 * @return return an instance of {@code ProjectionInfo} in case projection was used, {@code null} otherwise.
	 */
	ProjectionInfo getProjectionInfo();

	/**
	 * Returns {@code true} in case projection is used, {@code false} otherwise.
	 *
	 * @return returns {@code true} in case projection is used, {@code false} otherwise.
	 */
	boolean hasProjections();
}
