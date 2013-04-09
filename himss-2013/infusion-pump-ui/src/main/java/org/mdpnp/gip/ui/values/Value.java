package org.mdpnp.gip.ui.values;

import org.mdpnp.gip.ui.units.Units;

public interface Value<U extends Units> {
	Double getValue();

	void setValue(Double value);

	U getUnits();

	void setUnits(U massUnits);

	Double getValue(U massUnits);

	void setValue(U massUnits, Double value);
	
	U[] getAcceptableUnits();
	
	Double getMaximum();
	
	Double getMinimum();
	
	Double getSoftMinimum();
	
	Double getSoftMaximum();
	
	Double getStarting();
	
	void setSoftMinimum(Double d);
	
	void setSoftMaximum(Double d);
	
	void setStarting(Double d);
	
	void setMaximum(Double d);
	
	void setMinimum(Double d);
	
	Double getStepSize();
	
	int getFractionDigits();

	void addListener(ValueListener<U> listener);
	
	void removeListener(ValueListener<U> listener);
}
