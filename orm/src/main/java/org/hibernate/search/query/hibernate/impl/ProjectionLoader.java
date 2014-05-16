/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.query.hibernate.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.search.exception.AssertionFailure;
import org.hibernate.search.engine.spi.SearchFactoryImplementor;
import org.hibernate.search.query.engine.spi.EntityInfo;
import org.hibernate.search.query.engine.spi.TimeoutManager;
import org.hibernate.transform.ResultTransformer;

/**
 * Implementation of the {@code Loader} interface used for loading entities which are projected via
 * {@link org.hibernate.search.engine.ProjectionConstants#THIS}.
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
public class ProjectionLoader implements Loader {
	private Loader objectLoader;
	private boolean projectThisIsInitialized = false;//guard for next variable
	private boolean projectThis;
	private ResultTransformer transformer;
	private String[] aliases;
	private ObjectLoaderBuilder loaderBuilder;

	@Override
	public void init(Session session,
					SearchFactoryImplementor searchFactoryImplementor,
					ObjectsInitializer objectsInitializer,
					TimeoutManager timeoutManager) {
	}

	public void init(Session session,
					SearchFactoryImplementor searchFactoryImplementor,
					ResultTransformer transformer,
					ObjectLoaderBuilder loaderBuilder,
					String[] aliases,
					TimeoutManager timeoutManager) {
		init( session, searchFactoryImplementor, null, timeoutManager ); // TODO why do we call this method?
		this.transformer = transformer;
		this.aliases = aliases;
		this.loaderBuilder = loaderBuilder;
	}

	@Override
	public Object load(EntityInfo entityInfo) {
		// no need to timeout here, the underlying loader is the real time consumer
		if ( projectionEnabledOnThis( entityInfo ) ) {
			Loader objectLoader = getObjectLoader();
			final Object entityInstance = objectLoader.load( entityInfo );
			entityInfo.getProjectionInfo().populateWithEntityInstance( entityInstance );
		}
		if ( transformer != null ) {
			return transformer.transformTuple( entityInfo.getProjectionInfo().getProjectedValues(), aliases );
		}
		else {
			return entityInfo.getProjectionInfo().getProjectedValues();
		}
	}

	@Override
	public Object loadWithoutTiming(EntityInfo entityInfo) {
		throw new AssertionFailure( "This method is not meant to be used on ProjectionLoader" );
	}

	private boolean projectionEnabledOnThis(final EntityInfo entityInfo) {
		if ( projectThisIsInitialized == false ) {
			projectThisIsInitialized = true;
			projectThis = entityInfo.getProjectionInfo().isThisProjected();
		}
		return projectThis;
	}

	@Override
	public List load(EntityInfo... entityInfos) {
		//no need to timeout here, the underlying loader is the real time consumer
		List results = new ArrayList( entityInfos.length );
		if ( entityInfos.length == 0 ) {
			return results;
		}

		if ( projectionEnabledOnThis( entityInfos[0] ) ) {
			Loader objectLoader = getObjectLoader();
			objectLoader.load( entityInfos ); // load by batch
			for ( EntityInfo entityInfo : entityInfos ) {
				final Object entityInstance = objectLoader.loadWithoutTiming( entityInfo );
				entityInfo.getProjectionInfo().populateWithEntityInstance( entityInstance );
			}
		}
		for ( EntityInfo entityInfo : entityInfos ) {
			if ( transformer != null ) {
				results.add( transformer.transformTuple( entityInfo.getProjectionInfo().getProjectedValues(), aliases ) );
			}
			else {
				results.add( entityInfo.getProjectionInfo().getProjectedValues() );
			}
		}

		return results;
	}

	private Loader getObjectLoader() {
		if ( objectLoader == null ) {
			objectLoader = loaderBuilder.buildLoader();
		}
		return objectLoader;
	}

	@Override
	public boolean isSizeSafe() {
		return getObjectLoader().isSizeSafe();
	}
}
