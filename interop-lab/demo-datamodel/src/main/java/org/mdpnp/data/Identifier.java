/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.data;

/**
 * A globally unique identifier.
 * Identifiers must guarantee uniqueness so 
 * implementors might use package name to 
 * differentiate from similar identifiers
 * from other sources.
 * 
 * Of course this could be subverted with
 * careful classpath ordering; but at this 
 * point that is more feature than bug.
 * 
 * @author jplourde
 *
 */
public interface Identifier extends java.io.Serializable {
	String getIdentifierClass();
	java.lang.reflect.Field getField();
}
