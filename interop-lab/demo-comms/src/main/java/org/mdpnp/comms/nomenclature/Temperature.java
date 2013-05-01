package org.mdpnp.comms.nomenclature;

import org.mdpnp.comms.data.numeric.Numeric;
import org.mdpnp.comms.data.numeric.NumericImpl;

public interface Temperature {
    Numeric TEMP1 = new NumericImpl(Temperature.class, "TEMP1", null, null);
    Numeric TEMP2 = new NumericImpl(Temperature.class, "TEMP2", null, null);
}
