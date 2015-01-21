/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.test.interceptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.junit.Before;
import org.junit.Test;

import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.indexes.interceptor.EntityIndexingInterceptor;
import org.hibernate.search.indexes.interceptor.IndexingOverride;
import org.hibernate.search.test.SearchTestBase;
import org.hibernate.search.testsupport.TestForIssue;

import static org.junit.Assert.assertEquals;

/**
 * @author Hardy Ferentschik
 */
@TestForIssue(jiraKey = "HSEARCH-1711")
public class InterceptorTest extends SearchTestBase {

	private FullTextSession fullTextSession;
	private List<Post> postEntities;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		fullTextSession = Search.getFullTextSession( openSession() );
		createTestData();
	}

	@Test
	public void testPostEntitiesAreNotGettingIndexed() throws Exception {
		indexTestData();

		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery( new MatchAllDocsQuery(), Post.class );
		assertEquals(
				"There should be no indexed entities, since automatic indexing is disabled via interceptor",
				0,
				fullTextQuery.list( ).size()
		);
	}

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {
				Group.class,
				Base.class,
				Comment.class,
				Post.class
		};
	}

	private void createTestData() {
		Group testGroup = new Group();
		postEntities = new ArrayList<>();
		// create three posts
		for ( int i = 0; i < 3; i++ ) {
			Post post = new Post();
			// with three comments each
			for ( int j = 0; j < 3; j++ ) {
				Comment comment = new Comment( post );
				// all in the default group
				comment.setGroup( testGroup );
				testGroup.addComment( comment );
				post.addComment( comment );
			}
			postEntities.add( post );
		}
	}

	private void indexTestData() {
		Transaction tx = fullTextSession.beginTransaction();

		for ( Post post : postEntities ) {
			fullTextSession.save( post );
		}

		tx.commit();
		fullTextSession.clear();
	}


	@Entity
	@Indexed
	public static class Group {
		@Id
		@GeneratedValue
		private long id;

		@Field
		private boolean someBoolean;

		@OneToMany(mappedBy = "group")
		@ContainedIn
		private Set<Base> comments = new HashSet<>(  );

		public void addComment(Base comment) {
			comments.add( comment );
		}
	}

	@Entity
	public static abstract class Base {
		@Id
		@GeneratedValue
		private long id;

		@ManyToOne(cascade = CascadeType.ALL)
		@IndexedEmbedded(includePaths = { "someBoolean" })
		private Group group;

		public void setGroup(Group group) {
			this.group = group;
		}
	}

	@Entity
	@Indexed
	public static class Comment extends Base {
		@ManyToOne
		private Post post;

		public Comment() {
		}

		public Comment(Post post) {
			this.post = post;
		}
	}

	@Entity
	@Indexed(interceptor = PostIndexingInterceptor.class)
	public static class Post extends Base {
		@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
		private Set<Comment> comments = new HashSet<>();

		public void addComment(Comment comment) {
			comments.add( comment );
		}
	}

	public static class PostIndexingInterceptor implements EntityIndexingInterceptor<Post> {
		@Override
		public IndexingOverride onAdd(Post post) {
			return IndexingOverride.SKIP;
		}

		@Override
		public IndexingOverride onUpdate(Post post) {
			return IndexingOverride.SKIP;
		}

		@Override
		public IndexingOverride onDelete(Post post) {
			return IndexingOverride.APPLY_DEFAULT;
		}

		@Override
		public IndexingOverride onCollectionUpdate(Post post) {
			return onUpdate( post );
		}
	}
}
