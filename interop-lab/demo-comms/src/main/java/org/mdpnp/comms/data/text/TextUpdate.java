/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.data.text;

import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.Persistent;

@Persistent
public interface TextUpdate extends IdentifiableUpdate<Text> {
	@Persistent
	String getValue();
}
