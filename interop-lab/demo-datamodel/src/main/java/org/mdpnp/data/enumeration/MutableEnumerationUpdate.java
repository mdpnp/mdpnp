/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.data.enumeration;

import org.mdpnp.data.MutableIdentifiableUpdate;

public interface MutableEnumerationUpdate extends EnumerationUpdate, MutableIdentifiableUpdate<Enumeration> {
	boolean setValue(Enum<?> e);
}
