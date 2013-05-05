/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.data.numeric;

import java.util.Date;

import org.mdpnp.data.MutableIdentifiableUpdateImpl;

@SuppressWarnings("serial")
public class MutableNumericUpdateImpl extends MutableIdentifiableUpdateImpl<Numeric> implements MutableNumericUpdate {
	private Number value;
	private Date updateTime;
	
	public MutableNumericUpdateImpl() {
	}
	
	public MutableNumericUpdateImpl(Numeric numeric) {
	    super(numeric);
	}
	
	public MutableNumericUpdateImpl(Numeric numeric, Number value, Date updateTime) {
		this(numeric);
		this.value = value;
		this.updateTime = updateTime;
	}
	

	
	@Override
	public Date getUpdateTime() {
		return updateTime;
	}

	@Override
	public Number getValue() {
		return value;
	}

	@Override
	public boolean setValue(Number n) {
	    if(null == n) {
	        if(null == this.value) {
	            return false;
	        } else {
	            this.value = n;
	            return true;
	        }
	    } else {
	        if(null == this.value) {
	            this.value = n;
	            return true;
	        } else {
	            if(this.value.equals(n)) {
	                return false;
	            } else {
	                this.value = n;
	                return true;
	            }
	        }
	    }
	}

	@Override
	public boolean setUpdateTime(Date dt) {
	    if(null == dt) {
	        if(null == this.updateTime) {
	            return false;
	        } else {
	            this.updateTime = dt;
	            return true;
	        }
	    } else {
	        if(null == this.updateTime) {
	            this.updateTime = dt;
	            return true;
	        } else {
	            // reference equality check
	            if(dt == this.updateTime) {
	                return false;
	            } else {
	                this.updateTime = dt;
	                return true;
	            }
	        }
	    }
	}

	@Override
	public boolean set(Number m, Date dt) {
		boolean b = setValue(m);
		b |= setUpdateTime(dt);
		return b;
	}
	@Override
	public String toString() {
		return "[identifier="+getIdentifier()+",source="+getSource()+"target="+getTarget()+"updateTime="+getUpdateTime()+",value="+value+"]";
	}

}
