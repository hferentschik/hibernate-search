/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.test.query.facet;

import java.util.Iterator;
import java.util.List;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.junit.Test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.query.facet.Facet;
import org.hibernate.search.query.facet.FacetingRequest;
import org.hibernate.testing.TestForIssue;

import static org.junit.Assert.assertEquals;

public class ManyToOneFacetingTest extends AbstractFacetTest {

	@Test
	public void testAllIndexedManyToOneValuesGetCounted() throws Exception {
		String indexFieldName = "companyFacilities.country";
		String facetName = "countryFacility";
		FacetingRequest request = queryBuilder( Company.class ).facet()
				.name( facetName )
				.onField( indexFieldName )
				.discrete()
				.includeZeroCounts( true )
				.createFacetingRequest();
		FullTextQuery query = queryCompanyWithFacet( request );

		List<Facet> facetList = query.getFacetManager().getFacets( facetName );
		assertEquals( "Wrong number of facets", 3, facetList.size() );

		// check count in facet
		Iterator<Facet> iter = facetList.iterator();
		while ( iter.hasNext() ) {
			Facet facet = iter.next();
			assertEquals( "Wrong count of facet", 1, facet.getCount() );
		}
	}

	@Test
	@TestForIssue( jiraKey = "HSEARCH-1927")
	public void testMultiValuedLongFacetingReturnsCorrectResults() throws Exception {
		String indexFieldName = "companyFacilities.employees";
		String facetName = "employees";
		FacetingRequest request = queryBuilder( Company.class ).facet()
				.name( facetName )
				.onField( indexFieldName )
				.range()
				.from( 1000 )
				.to(10000)
				.createFacetingRequest();
		FullTextQuery query = queryCompanyWithFacet( request );

		List<Facet> facetList = query.getFacetManager().getFacets( facetName );
		assertEquals( "Wrong number of facets", 2, facetList.size() );
	}

	private FullTextQuery queryCompanyWithFacet(FacetingRequest request) {
		FullTextQuery query = fullTextSession.createFullTextQuery( new MatchAllDocsQuery() );
		query.getFacetManager().enableFaceting( request );
		assertEquals( "Wrong number of query matches", 1, query.getResultSize() );
		return query;
	}

	public void loadTestData(Session session) {
		Transaction tx = session.beginTransaction();

		Company acme = new Company( "ACME" );

		CompanyFacility usFacility = new CompanyFacility( "US", 100, 1.0 );
		usFacility.setCompany( acme );
		acme.addCompanyFacility( usFacility );

		CompanyFacility germanFacility = new CompanyFacility( "Germany", 1000, 5.0 );
		germanFacility.setCompany( acme );
		acme.addCompanyFacility( germanFacility );

		CompanyFacility indianFacility = new CompanyFacility( "INDIA", 10000, 10.0 );
		indianFacility.setCompany( acme );
		acme.addCompanyFacility( indianFacility );

		session.save( acme );

		tx.commit();
		session.clear();
	}

	public Class<?>[] getAnnotatedClasses() {
		return new Class[] { Company.class, CompanyFacility.class };
	}
}
