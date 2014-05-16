/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.query.engine.impl;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.search.query.engine.spi.EntityInfo;
import org.hibernate.search.query.engine.spi.ProjectionInfo;

/**
 * Wrapper class describing the loading of an element.
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
public class EntityInfoImpl implements EntityInfo {
	/**
	 * The entity class.
	 */
	private final Class<?> clazz;

	/**
	 * The document id.
	 */
	private final Serializable id;

	/**
	 * The name of the document id property.
	 */
	private final String idName;

	private final ProjectionInfo projectionInfo;

	public EntityInfoImpl(Class clazz, String idName, Serializable id, Object[] projection) {
		this.clazz = clazz;
		this.idName = idName;
		this.id = id;
		if ( projection == null ) {
			this.projectionInfo = null;
		}
		else {
			this.projectionInfo = new ProjectionInfoImpl( projection );
		}
	}

	@Override
	public Class<?> getClazz() {
		return clazz;
	}

	@Override
	public Serializable getEntityId() {
		return id;
	}

	@Override
	public String getEntityIdName() {
		return idName;
	}

	@Override
	public Serializable getDocumentId() {
		return null;
	}

	@Override
	public String getDocumentIdName() {
		return null;
	}

	@Override
	public ProjectionInfo getProjectionInfo() {
		return projectionInfo;
	}

	@Override
	public boolean hasProjections() {
		return projectionInfo != null;
	}

	public static class ProjectionInfoImpl implements ProjectionInfo {
		/**
		 * Array of projected values. {@code null} in case there are no projections.
		 */
		private final Object[] projection;

		private final List<Integer> indexesOfThis = new LinkedList<Integer>();

		public ProjectionInfoImpl(Object[] projection) {
			if ( projection != null ) {
				this.projection = projection.clone();
			}
			else {
				this.projection = null;
			}
		}

		@Override
		public Object[] getProjectedValues() {
			return projection;
		}

		@Override
		public List<Integer> getIndexesOfThisProjection() {
			return indexesOfThis;
		}

		@Override
		public boolean isThisProjected() {
			return indexesOfThis.size() != 0;
		}

		@Override
		public void populateWithEntityInstance(Object entity) {
			for ( int index : indexesOfThis ) {
				projection[index] = entity;
			}
		}
	}
}
