package org.mdpnp.gip.ui;

import org.mdpnp.gip.ui.units.LengthUnits;
import org.mdpnp.gip.ui.units.LengthUnitsFactory;
import org.mdpnp.gip.ui.units.MassUnits;
import org.mdpnp.gip.ui.units.MassUnitsFactory;
import org.mdpnp.gip.ui.values.LengthValue;
import org.mdpnp.gip.ui.values.LengthValueImpl;
import org.mdpnp.gip.ui.values.MassValue;
import org.mdpnp.gip.ui.values.MassValueImpl;
import org.mdpnp.gip.ui.values.Value;
import org.mdpnp.gip.ui.values.ValueAdapter;

/**
 * @author Jeff Plourde
 *
 */
public class PatientModel extends AbstractModel<PatientListener> {
	private String id;
	private CareArea careArea;
	
	private final MassValue weight = new MassValueImpl(0.0, 1500.0, MassUnitsFactory.pounds, MassUnitsFactory.kilograms, MassUnitsFactory.stone);
	private final LengthValue height = new LengthValueImpl(0.0, 108.0, LengthUnitsFactory.inches, LengthUnitsFactory.centimeters, LengthUnitsFactory.meters);
	
	public PatientModel() {
		weight.addListener(new ValueAdapter<MassUnits>() {
			@Override
			protected void anythingChanged(Value<MassUnits> v) {
				fireEvent();
			}
		});
		height.addListener(new ValueAdapter<LengthUnits>() {
			@Override
			protected void anythingChanged(Value<LengthUnits> v) {
				fireEvent();
			}
		});
	}
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
		fireEvent();
	}
	public CareArea getCareArea() {
		return careArea;
	}
	public void setCareArea(CareArea careArea) {
		this.careArea = careArea;
		fireEvent();
	}
	public MassValue getWeight() {
		return weight;
	}
	public LengthValue getHeight() {
		return height;
	}
	public Double getBodySurfaceArea() {
		Double massInKg = weight.getValue(MassUnitsFactory.kilograms);
		Double heightInCm = height.getValue(LengthUnitsFactory.centimeters);
		
		if(null == massInKg || heightInCm == null) {
			return null;
		} else {
			return Math.sqrt(massInKg.doubleValue() * heightInCm.doubleValue() / 3600.0);
		}
	}
	@Override
	protected void doFireEvent(Object event, PatientListener listener) {
		listener.patientChanged(this);
	}
}
