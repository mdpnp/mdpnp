package org.mdpnp.gip.ui;

import org.mdpnp.gip.ui.units.MassUnits;
import org.mdpnp.gip.ui.units.MassUnitsFactory;
import org.mdpnp.gip.ui.units.Units;
import org.mdpnp.gip.ui.units.VolumeUnits;
import org.mdpnp.gip.ui.units.VolumeUnitsFactory;
import org.mdpnp.gip.ui.values.MassValue;
import org.mdpnp.gip.ui.values.MassValueImpl;
import org.mdpnp.gip.ui.values.Value;
import org.mdpnp.gip.ui.values.ValueAdapter;
import org.mdpnp.gip.ui.values.VolumeValue;
import org.mdpnp.gip.ui.values.VolumeValueImpl;

/**
 * @author Jeff Plourde
 *
 */
public class Concentration extends AbstractModel<ConcentrationListener> {
	
	private final MassValue mass = new MassValueImpl(MassUnitsFactory.micrograms, MassUnitsFactory.milligrams, MassUnitsFactory.grams);
	private final VolumeValue volume = new VolumeValueImpl(VolumeUnitsFactory.milliliters);
	
	public Concentration() {
		mass.addListener(new ValueAdapter<MassUnits>() {
			@Override
			protected void anythingChanged(Value<MassUnits> v) {
				fireEvent();
			}
		});
		volume.addListener(new ValueAdapter<VolumeUnits>() {
			@Override
			protected void anythingChanged(Value<VolumeUnits> v) {
				fireEvent();
			}
		});
	}
	
	public MassValue getMass() {
		return mass;
	}
	public VolumeValue getVolume() {
		return volume;
	}
	public Double getValue() {
		Double m = mass.getValue();
		Double v = volume.getValue();
		if(null == m || null == v) {
			return null;
		}
		return m / v;
	}
	public String getAbbreviatedName() {
		Units mu = mass.getUnits();
		Units vu = volume.getUnits();
		if(null == mu || null == vu) {
			return null;
		}
		return mu.getAbbreviatedName() +"/" +vu.getAbbreviatedName();
	}
	@Override
	protected void doFireEvent(Object event, ConcentrationListener listener) {
		listener.concentrationChanged(this);
	}
}
