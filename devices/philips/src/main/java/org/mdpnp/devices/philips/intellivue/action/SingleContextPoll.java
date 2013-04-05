package org.mdpnp.devices.philips.intellivue.action;

import java.util.List;

import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public interface SingleContextPoll extends Parseable, Formatable {
	int getMdsContext();
	List<ObservationPoll> getPollInfo();
	
}