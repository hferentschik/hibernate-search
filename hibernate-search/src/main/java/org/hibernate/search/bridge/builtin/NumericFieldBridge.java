package org.hibernate.search.bridge.builtin;

import org.apache.lucene.document.Document;

import org.hibernate.search.bridge.AbstractFieldBridge;
import org.hibernate.search.bridge.TwoWayFieldBridge;

/**
 * Bridge to index numeric values using a <a href="http://en.wikipedia.org/wiki/Trie">Trie structure</a>
 * (multiple terms representing different precisions)
 *
 * @author Gustavo Fernandes
 * @author Hardy Ferentschik
 */
public abstract class NumericFieldBridge extends AbstractFieldBridge implements TwoWayFieldBridge {
	@Override
	public void set(Object value, Document document) {
		if ( value != null ) {
			getLuceneOptions().addNumericFieldToDocument( getFieldName(), value, document );
		}
	}

	public String objectToString(Object object) {
		return object.toString();
	}
}
