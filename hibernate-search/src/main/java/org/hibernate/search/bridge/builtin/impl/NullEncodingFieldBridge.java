/* 
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.hibernate.search.bridge.builtin.impl;

import org.apache.lucene.document.Document;

import org.hibernate.search.bridge.AbstractFieldBridge;
import org.hibernate.search.bridge.StringBridge;

/**
 * @author Davide D'Alto
 * @author Hardy Ferentschik
 */
public class NullEncodingFieldBridge extends AbstractFieldBridge implements StringBridge {

	private final String2FieldBridgeAdaptor bridge;
	private final String nullMarker;

	public NullEncodingFieldBridge(StringBridge bridge, String nullMarker) {
		this.bridge = new String2FieldBridgeAdaptor( bridge );
		this.nullMarker = nullMarker;
	}


	@Override
	public void set(Object value, Document document) {
		if ( value == null ) {
			getLuceneOptions().addFieldToDocument( getFieldName(), nullMarker, document );
		}
		else {
			bridge.set( value, document );
		}
	}

	@Override
	public String objectToString(Object object) {
		if ( object == null ) {
			return nullMarker;
		}
		return bridge.objectToString( object );
	}
}
