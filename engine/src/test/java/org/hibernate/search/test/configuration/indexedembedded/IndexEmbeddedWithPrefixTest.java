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
package org.hibernate.search.test.configuration.indexedembedded;

import org.hibernate.search.SearchException;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.TwoWayStringBridge;
import org.hibernate.search.bridge.builtin.impl.TwoWayString2FieldBridgeAdaptor;
import org.hibernate.search.engine.spi.DocumentBuilderIndexedEntity;
import org.hibernate.search.engine.spi.EntityIndexBinding;
import org.hibernate.search.impl.MutableSearchFactory;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.spi.SearchFactoryBuilder;
import org.hibernate.search.test.util.ManualConfiguration;
import org.hibernate.search.test.util.TestForIssue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Hardy Ferentschik
 */
@TestForIssue(jiraKey = "HSEARCH-1442")
public class IndexEmbeddedWithPrefixTest {

	@Test
	public void testIndexEmbeddedWithPrefixTrailingDot() {
		createDSLQueryAnDAssertFieldBridgeCalled( "foo.label" );
	}

	@Test
	public void testIndexEmbeddedWithPrefixNoTrailingDot() {
		createDSLQueryAnDAssertFieldBridgeCalled( "bar.label" );
	}

	@Test
	public void testUnknownIndexEmbeddedIField() {
		MutableSearchFactory searchFactory = getSearchFactory();
		QueryBuilder queryBuilder = searchFactory.buildQueryBuilder().forEntity( User.class ).get();

		try {
			String fieldName = "roles.label";
			queryBuilder.keyword()
					.onField( fieldName )
					.matching( "foo" )
					.createQuery();
			fail( "Query building should fail due to unknown field" );
		}
		catch (SearchException e) {
			assertEquals(
					"Unable to find field roles.label in org.hibernate.search.test.configuration.indexedembedded.User",
					e.getMessage()
			);
		}

	}

	private void createDSLQueryAnDAssertFieldBridgeCalled(String fieldName) {
		MutableSearchFactory searchFactory = getSearchFactory();
		QueryBuilder queryBuilder = searchFactory.buildQueryBuilder().forEntity( User.class ).get();

		queryBuilder.keyword()
				.onField( fieldName )
				.matching( "foo" )
				.createQuery();

		InvocationCountingFieldBridge invocationCountingFieldBridge = getFieldBridgeForLabel( searchFactory, fieldName );
		assertEquals(
				"objectToString should have been called once during building of the query",
				1,
				invocationCountingFieldBridge.getObjectToStringCount()
		);
	}

	private InvocationCountingFieldBridge getFieldBridgeForLabel(MutableSearchFactory searchFactory, String fieldName) {
		EntityIndexBinding entityIndexBinding = searchFactory.getIndexBinding( User.class );
		DocumentBuilderIndexedEntity<?> documentBuilder = entityIndexBinding.getDocumentBuilder();
		FieldBridge fieldBridge = documentBuilder.getBridge( fieldName );

		assertNotNull( fieldBridge );
		assertTrue(
				"Unexpected bridge type: " + fieldBridge.getClass(),
				fieldBridge instanceof TwoWayString2FieldBridgeAdaptor
		);

		TwoWayStringBridge stringBridge = ( (TwoWayString2FieldBridgeAdaptor) fieldBridge ).unwrap();
		return (InvocationCountingFieldBridge) stringBridge;
	}


	private MutableSearchFactory getSearchFactory() {
		ManualConfiguration configuration = new ManualConfiguration();
		configuration.addProperty( "hibernate.search.default.directory_provider", "ram" );
		configuration.addClass( User.class );
		configuration.addClass( UserRole.class );

		return (MutableSearchFactory) new SearchFactoryBuilder().configuration( configuration ).buildSearchFactory();
	}
}
