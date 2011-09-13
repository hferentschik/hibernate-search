/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat, Inc. and/or its affiliates or third-party contributors as
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
package org.hibernate.search.bridge.builtin.impl;

import org.apache.lucene.document.Document;

import org.hibernate.search.bridge.AbstractFieldBridge;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.StringBridge;

/**
 * Adapter to use a {@link StringBridge} as a {@link FieldBridge}.
 *
 * @author Emmanuel Bernard (C) 2011 Red Hat Inc.
 * @author Sanne Grinovero (C) 2011 Red Hat Inc.
 * @author Hardy Ferentschik
 */
public class String2FieldBridgeAdaptor extends AbstractFieldBridge implements StringBridge {
	private final StringBridge stringBridge;

	public String2FieldBridgeAdaptor(StringBridge stringBridge) {
		this.stringBridge = stringBridge;
	}

	@Override
	public void set(Object value, Document document) {
		String indexedString = stringBridge.objectToString( value );
		if ( indexedString == null && getLuceneOptions().indexNullAs() != null ) {
			indexedString = getLuceneOptions().indexNullAs();
		}
		getLuceneOptions().addFieldToDocument( getFieldName(), indexedString, document );
	}

	@Override
	public String objectToString(Object object) {
		return stringBridge.objectToString( object );
	}
}
