/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
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
package org.hibernate.search.test.batchindexing;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;

import org.hibernate.search.bridge.AbstractFieldBridge;
import org.hibernate.search.bridge.TwoWayFieldBridge;

/**
 * @author Bayo Erinle
 */
public class LegacyCarPlantPKBridge extends AbstractFieldBridge implements TwoWayFieldBridge {
	private static final String PLANT_ID = ".plantId";
	private static final String CAR_ID = ".carId";

	@Override
	public Object get(String name, Document document) {
		LegacyCarPlantPK id = new LegacyCarPlantPK();
		Fieldable fieldable = document.getFieldable( name + PLANT_ID );
		id.setPlantId( fieldable.stringValue() );

		fieldable = document.getFieldable( name + CAR_ID );
		id.setCarId( fieldable.stringValue() );
		return id;
	}

	@Override
	public String objectToString(Object o) {
		LegacyCarPlantPK id = (LegacyCarPlantPK) o;
		StringBuilder sb = new StringBuilder();
		sb.append( id.getPlantId() ).append( "-" ).append( id.getCarId() );
		return sb.toString();
	}

	@Override
	public void set(Object o, Document document) {
		LegacyCarPlantPK id = (LegacyCarPlantPK) o;
		Field.Store store = getLuceneOptions().getStore();
		Field.Index index = getLuceneOptions().getIndex();
		Field.TermVector termVector = getLuceneOptions().getTermVector();
		Float boost = getLuceneOptions().getBoost();

		Fieldable field = new Field( getFieldName() + PLANT_ID, id.getPlantId(), store, index, termVector );
		field.setBoost( boost );
		document.add( field );

		field = new Field( getFieldName() + CAR_ID, id.getCarId(), store, index, termVector );
		field.setBoost( boost );
		document.add( field );
	}
}
