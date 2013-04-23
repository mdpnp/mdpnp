/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.data.textarray;

import java.util.Arrays;

import org.mdpnp.comms.MutableIdentifiableUpdateImpl;

@SuppressWarnings("serial")
public class MutableTextArrayUpdateImpl extends MutableIdentifiableUpdateImpl<TextArray> implements MutableTextArrayUpdate {
	public MutableTextArrayUpdateImpl() {
	}
	
	public MutableTextArrayUpdateImpl(TextArray textArray) {
	    super(textArray);
	}
	
	private String[] value;
	@Override
	public String[] getValue() {
		return value;
	}

	@Override
	public boolean setValue(String[] value) {
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
	            // reference equality check only
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
		return "[identifier="+getIdentifier()+",source="+getSource()+",target="+getTarget()+",value="+Arrays.toString(value)+"]";
	}

}
