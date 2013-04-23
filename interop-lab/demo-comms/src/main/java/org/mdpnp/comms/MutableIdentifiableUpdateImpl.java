/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms;


@SuppressWarnings("serial")
public abstract class MutableIdentifiableUpdateImpl<T extends Identifier> implements MutableIdentifiableUpdate<T> {
	private String source, target;
	private T identifier;
	
	public MutableIdentifiableUpdateImpl() {
    }
	
	public MutableIdentifiableUpdateImpl(T identifier) {
	    this.identifier = identifier;
    }
	
	@Override
	public String getSource() {
		return source;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	@Persistent(key = true)
	public T getIdentifier() {
	    return identifier;
	}
	
	@Override
	public boolean setSource(String src) {
	    if(null == src) {
	        if(null == this.source) {
	            return false;
	        } else {
	            this.source = src;
	            return true;
	        }
	    } else { 
	        if(null == this.source) {
	            this.source = src;
	            return true;
	        } else {
	            if(this.source.equals(src)) {
	                return false;
	            } else {
	                this.source = src;
	                return true;
	            }
	        }
	    }
	}

	@Override
	public boolean setTarget(String tgt) {
	    if(null == tgt) {
            if(null == this.target) {
                return false;
            } else {
                this.target = tgt;
                return true;
            }
        } else { 
            if(null == this.target) {
                this.target = tgt;
                return true;
            } else {
                if(this.target.equals(tgt)) {
                    return false;
                } else {
                    this.target = tgt;
                    return true;
                }
            }
        }
	}
    @Override
    public boolean setIdentifier(T id) {
        if(null == id) {
            if(null == this.identifier) {
                return false;
            } else {
                this.identifier = id;
                return true;
            }
        } else {
            if(null == this.identifier) {
                this.identifier = id;
                return true;
            } else {
                if(id.equals(this.identifier)) {
                    return false;
                } else {
                    this.identifier = id;
                    return true;
                }
            }
        }
    }
}
