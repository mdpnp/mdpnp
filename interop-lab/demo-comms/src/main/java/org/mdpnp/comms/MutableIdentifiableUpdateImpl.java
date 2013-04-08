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
	
	@Override
	public String getSource() {
		return source;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public void setSource(String src) {
		this.source = src;
		
	}

	@Override
	public void setTarget(String tgt) {
		this.target = tgt;
	}

}
