package org.hibernate.search.bridge.builtin.impl;

import org.apache.lucene.document.Document;

import org.hibernate.search.bridge.AbstractFieldBridge;

/**
 * @author Hardy Ferentschik
 */
public class DefaultNumericFieldBridge extends AbstractFieldBridge {
	@Override
	public void set(Object value, Document
			document) {
		if ( value == null ) {
			manageNull( document );
		}
		else {
			getLuceneOptions().addNumericFieldToDocument( getFieldName(), value, document );
		}
	}

	private void manageNull(Document document) {
		if ( getLuceneOptions().indexNullAs() != null ) {
			getLuceneOptions().addFieldToDocument( getFieldName(), getLuceneOptions().indexNullAs(), document );
		}
	}
}


