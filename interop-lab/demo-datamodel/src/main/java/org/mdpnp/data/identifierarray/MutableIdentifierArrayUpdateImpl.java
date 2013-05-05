/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.data.identifierarray;

import java.util.Arrays;

import org.mdpnp.data.Identifier;
import org.mdpnp.data.MutableIdentifiableUpdateImpl;

@SuppressWarnings("serial")
public class MutableIdentifierArrayUpdateImpl extends MutableIdentifiableUpdateImpl<IdentifierArray> implements MutableIdentifierArrayUpdate {
	private Identifier[] value;
	
	public MutableIdentifierArrayUpdateImpl() {
	}
	
	public MutableIdentifierArrayUpdateImpl(IdentifierArray identifierArray) {
		super(identifierArray);
	}
	
	@Override
	public Identifier[] getValue() {
		return value;
	}
	
	@Override
	public boolean setValue(Identifier[] value) {
	    if(null == value) {
	        if(null == this.value) {
	            return false;
	        } else {
	            this.value = value;
	            return true;
	        }
	    } else {
	        if(null == this.value) {
	            this.value = value;
	            return true;
	        } else {
	            // check for the same reference only
	            // want to ensure postcondition getIdentifier() == value
	            if(value == this.value) {
	                return false;
	            } else {
	                this.value = value;
	                return true;
	            }
	        }
	    }
	}
	@Override
	public String toString() {
		return "[identifier="+getIdentifier()+",source="+getSource()+"target="+getTarget()+"value="+Arrays.toString(value)+"]";
	}

}
