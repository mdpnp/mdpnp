/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.data.numeric;

import java.util.Date;

import org.mdpnp.comms.MutableIdentifiableUpdateImpl;

@SuppressWarnings("serial")
public class MutableNumericUpdateImpl extends MutableIdentifiableUpdateImpl<Numeric> implements MutableNumericUpdate {

	private Numeric numeric;
	private Number value;
	private Date updateTime;
	
	public MutableNumericUpdateImpl() {
	}
	
	public MutableNumericUpdateImpl(Numeric numeric) {
		this.numeric = numeric;
	}
	
	public MutableNumericUpdateImpl(Numeric numeric, Number value, Date updateTime) {
		this(numeric);
		this.value = value;
		this.updateTime = updateTime;
	}
	
	@Override
	public Numeric getIdentifier() {
		return numeric;
	}
	
	@Override
	public void setIdentifier(Numeric numeric) {
		this.numeric = numeric;
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
	public void setValue(Number n) {
		this.value = n;
	}

	@Override
	public void setUpdateTime(Date dt) {
		this.updateTime = dt;
	}

	@Override
	public void set(Number m, Date dt) {
		setValue(m);
		setUpdateTime(dt);
	}
	@Override
	public String toString() {
		return "[identifier="+getIdentifier()+",source="+getSource()+"target="+getTarget()+"updateTime="+getUpdateTime()+",value="+value+"]";
	}

}
