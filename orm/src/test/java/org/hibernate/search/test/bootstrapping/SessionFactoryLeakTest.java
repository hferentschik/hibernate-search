/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2013, Red Hat, Inc. and/or its affiliates or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat, Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.search.test.bootstrapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.search.test.util.ServiceRegistryTools;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.testing.cache.CachingRegionFactory;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Hardy Ferentschik
 */
public class SessionFactoryLeakTest {

	@Test
	public void testAssociationIsAlwaysLazyLoaded() {
		long insertedId = insertData();
		loadEntityAndAssertThatAssociationIsLazy( insertedId );

		Persistence.createEntityManagerFactory( "EntityManagerTestPU", getEntityManagerConfig() );
//		EntityManager entityManager = entityManagerFactory.createEntityManager();
//		entityManager.close();
//		entityManagerFactory.close();

		loadEntityAndAssertThatAssociationIsLazy( insertedId );
	}

	private void loadEntityAndAssertThatAssociationIsLazy(long insertedId) {
		Session session = getNewSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		Foo foo = (Foo) session.load( Foo.class, insertedId );
		assertNotNull( foo );

		PersistentBag bars = (PersistentBag) foo.getBars();
		assertFalse( "Bar instances should not be initialized", bars.wasInitialized() );

		tx.commit();
		session.close();
	}

	@Before
	public void setUp() {
		SchemaExport schemaExport = new SchemaExport( getConfiguration() );
		schemaExport.create( true, true );
	}

	@After
	public void tearDown() {
		SchemaExport schemaExport = new SchemaExport( getConfiguration() );
		schemaExport.drop( true, true );
	}

	private long insertData() {
		Session session = getNewSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		Foo foo = new Foo();
		foo.addBar( new Bar() );
		foo.addBar( new Bar() );
		foo.addBar( new Bar() );

		session.save( foo );

		tx.commit();
		session.close();

		return foo.getId();
	}

	private SessionFactory getNewSessionFactory() {
		Configuration configuration = getConfiguration();

		ServiceRegistryBuilder registryBuilder = new ServiceRegistryBuilder();
		registryBuilder.applySettings( configuration.getProperties() );

		ServiceRegistry serviceRegistry = ServiceRegistryTools.build( registryBuilder );
		return configuration.buildSessionFactory( serviceRegistry );
	}

	private Configuration getConfiguration() {
		Configuration configuration = new Configuration();
		configuration.addAnnotatedClass( Foo.class );
		configuration.addAnnotatedClass( Bar.class );
		configuration.getProperties().putAll( getProperties() );
		return configuration;
	}

	private Properties getProperties() {
		Properties properties = new Properties();

		properties.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
		properties.setProperty(
				Environment.CACHE_REGION_FACTORY,
				CachingRegionFactory.class.getCanonicalName()
		);
		properties.setProperty( Environment.USE_QUERY_CACHE, "true" );

		return properties;
	}

	public Map getEntityManagerConfig() {
		Map<Object, Object> config = loadProperties();

//		ArrayList<Class> classes = new ArrayList<Class>();
//		config.put( AvailableSettings.LOADED_CLASSES, classes );

		return config;
	}

	public static Properties loadProperties() {
		Properties props = new Properties();
		InputStream stream = Persistence.class.getResourceAsStream( "/hibernate.properties" );
		if ( stream != null ) {
			try {
				props.load( stream );
			}
			catch (Exception e) {
				throw new RuntimeException( "could not load hibernate.properties" );
			}
			finally {
				try {
					stream.close();
				}
				catch (IOException ioe) {
				}
			}
		}
		props.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
		return props;
	}

	@Entity
	public static class Foo {
		@Id
		@GeneratedValue
		private long id;

		@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
		private List<Bar> bars;

		public long getId() {
			return id;
		}

		public void addBar(Bar bar) {
			if ( bars == null ) {
				bars = new ArrayList<Bar>();
			}
			bars.add( bar );
		}

		public List<Bar> getBars() {
			return bars;
		}
	}

	@Entity
	public static class Bar {
		@Id
		@GeneratedValue
		private long id;
	}
}


