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

import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.search.bridge.builtin.StringBridge;

/**
 * @author Hardy Ferentschik
 */
public class InvocationCountingFieldBridge extends StringBridge {
	private AtomicInteger stringToObjectCount = new AtomicInteger( 0 );
	private AtomicInteger objectToStringCount = new AtomicInteger( 0 );

	@Override
	public Object stringToObject(String stringValue) {
		stringToObjectCount.incrementAndGet();
		return super.stringToObject( stringValue );
	}

	@Override
	public String objectToString(Object object) {
		objectToStringCount.incrementAndGet();
		return super.objectToString( object );
	}

	public int getStringToObjectCount() {
		return stringToObjectCount.get();
	}

	public int getObjectToStringCount() {
		return objectToStringCount.get();
	}
}


