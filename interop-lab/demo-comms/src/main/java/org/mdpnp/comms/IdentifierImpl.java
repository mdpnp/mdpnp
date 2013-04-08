/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms;

import java.lang.reflect.Field;

/**
 * A default implementation of identifier
 * that uses a unique string to maintain identity.
 * 
 * Constructors are provided that derive their 
 * identity string from an enumerated value of
 * from a Class (along with specified field name)
 * 
 * @author jplourde
 *
 */
@SuppressWarnings("serial")
public abstract class IdentifierImpl implements Identifier {
 
    private final String identifier;
	private transient Field field;
		
	public IdentifierImpl(Class<?> cls, String name) {
		identifier = cls.getName()+"."+name;
		try {
			field = cls.getField(name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof IdentifierImpl) {
			return ((IdentifierImpl)obj).identifier.equals(this.identifier);
		} else {
			return false;
		}
	}
	@Override
	public int hashCode() {
		return identifier.hashCode();
	}
	
	@Override
	public Field getField() {
		if(null == field) {
			int lastDot = identifier.lastIndexOf('.');
			try {
				field = Class.forName(identifier.substring(0, lastDot)).getField(identifier.substring(lastDot+1, identifier.length()));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return field;
	}
	@Override
	public String toString() {
		return identifier;
	}
	
}
