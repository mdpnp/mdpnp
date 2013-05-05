/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.data;

/**
 * An update that furnishes an Identifiable
 * type that will discern the type of this
 * update.
 * 
 * @author jplourde
 *
 * @param <T>
 */
@Persistent
public interface IdentifiableUpdate<T extends Identifier> extends java.io.Serializable {
    
	String WILDCARD = "*";
	
	@Persistent(key=true)
	T getIdentifier();
	@Persistent(key=true)
	String getSource();
	@Persistent(key=true)
	String getTarget();
	
	
}
