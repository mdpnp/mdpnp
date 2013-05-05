/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.data.enumeration;

import org.mdpnp.data.MutableIdentifiableUpdateImpl;

@SuppressWarnings("serial")
public class MutableEnumerationUpdateImpl extends MutableIdentifiableUpdateImpl<Enumeration> implements MutableEnumerationUpdate {

	private Enum<?> value;
	
	public MutableEnumerationUpdateImpl() {
	}
	
	public MutableEnumerationUpdateImpl(Enumeration enumeration) {
		super(enumeration);
	}
	
	@Override
	public Enum<?> getValue() {
		return value;
	}

	@Override
	public boolean setValue(Enum<?> e) {
	    if(null == e) {
	        if(null == this.value) {
	            return false;
	        } else {
	            this.value = e;
	            return true;
	        }
	    } else {
	        if(null == this.value) {
	            this.value = e;
	            return true;
	        } else {
	            if(e.equals(this.value)) {
	                return false;
	            } else {
	                this.value = e;
	                return true;
	            }
	        }
	    }
	}
	@Override
	public String toString() {
		return "[identifier="+getIdentifier()+",source="+getSource()+",target="+getTarget()+",value="+value+"]";
	}
	
}
