/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2011, Red Hat, Inc. and/or its affiliates or third-party contributors as
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
package org.hibernate.search.test.query.facet.manytoone;

import java.util.List;

import org.apache.lucene.search.Query;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.query.facet.Facet;
import org.hibernate.search.query.facet.FacetingRequest;
import org.hibernate.search.test.query.facet.AbstractFacetTest;

/**
 * @author Pragnesh
 */
public class ManyToOneFacetingTest extends AbstractFacetTest {

	public void testManyToOneFaceting() throws Exception {
		String indexFieldName = "companyFacilities.country";
		String facetName = "countryFacility";

		FacetingRequest request = queryBuilder( Company.class ).facet()
				.name( facetName )
				.onField( indexFieldName )
				.discrete()
				.createFacetingRequest();
		FullTextQuery query = queryCompanyWithFacet( request );

		List<Facet> facetList = query.getFacetManager().getFacets( facetName );
		assertEquals( "Wrong number of facets", 2, facetList.size() );

		//check count in facet
		for ( Facet item : facetList ) {
			assertEquals( "Wrong count of facet", 1, item.getCount() );
		}
	}

	private FullTextQuery queryCompanyWithFacet(FacetingRequest request) {
		Query luceneQuery = queryBuilder( Company.class ).keyword()
				.onField( "companyName" )
				.matching( "ABC" )
				.createQuery();
		FullTextQuery query = fullTextSession.createFullTextQuery( luceneQuery, Company.class );
		query.getFacetManager().enableFaceting( request );
		assertEquals( "Wrong number of query matches", 1, query.getResultSize() );
		return query;
	}

	public void loadTestData(Session session) {
		Transaction tx = session.beginTransaction();

		Company a = new Company( "ABC" );
		session.save( a );

		CompanyFacility us = new CompanyFacility( a, "US" );
		session.save( us );

		CompanyFacility india = new CompanyFacility( a, "INDIA" );
		session.save( india );

		tx.commit();
		session.clear();
	}

	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] {
				Company.class, CompanyFacility.class
		};
	}
}
