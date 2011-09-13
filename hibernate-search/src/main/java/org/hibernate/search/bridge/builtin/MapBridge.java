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
package org.hibernate.search.bridge.builtin;

import java.util.Map;

import org.apache.lucene.document.Document;

import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

/**
 * Each entry ({@code null included}) of the values in a {@link Map} is indexed using the specified {@link FieldBridge}.
 * <br>
 * A {@code null} array is not indexed.
 *
 * @author Davide D'Alto
 * @author Hardy Ferentschik
 */
public class MapBridge implements FieldBridge {
	private final FieldBridge bridge;

	/**
	 * @param bridge the {@link FieldBridge} used for each entry of a {@link java.util.Map} object.
	 */
	public MapBridge(FieldBridge bridge) {
		this.bridge = bridge;
	}

	@Override
	public void initialize(String name, LuceneOptions luceneOptions) {
		bridge.initialize( name, luceneOptions );
	}

	@Override
	public void set(Object value, Document document) {
		if ( value != null ) {
			indexNotNullMap( value, document );
		}
	}

	protected void indexNotNullMap(Object value, Document document) {
		Iterable<?> collection = ( (Map<?, ?>) value ).values();
		for ( Object entry : collection ) {
			indexEntry( entry, document );
		}
	}

	protected void indexEntry(Object entry, Document document) {
		bridge.set( entry, document );
	}
}
