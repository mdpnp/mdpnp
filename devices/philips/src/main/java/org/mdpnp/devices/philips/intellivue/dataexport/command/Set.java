package org.mdpnp.devices.philips.intellivue.dataexport.command;

import java.util.List;

import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportCommand;
import org.mdpnp.devices.philips.intellivue.dataexport.ModifyOperator;

public interface Set extends DataExportCommand {
	interface AttributeModEntry extends Parseable, Formatable {
		ModifyOperator getModifyOperator();
        Attribute<?> getAttributeValueAssertion();
	}
	
	List<AttributeModEntry> getList();
//	void add(ModifyOperator modifyOperator, AttributeValueAssertion ava);
	void add(ModifyOperator modifyOperator, Attribute<?> attribute);
	SetResult createResult();
}
