package org.mdpnp.gip.ui;

import org.mdpnp.gip.ui.units.RatioUnits;
import org.mdpnp.gip.ui.units.RatioUnitsFactory;
import org.mdpnp.gip.ui.units.TimeUnits;
import org.mdpnp.gip.ui.units.TimeUnitsFactory;
import org.mdpnp.gip.ui.units.VolumeUnits;
import org.mdpnp.gip.ui.units.VolumeUnitsFactory;
import org.mdpnp.gip.ui.values.RatioValue;
import org.mdpnp.gip.ui.values.RatioValueImpl;
import org.mdpnp.gip.ui.values.TimeValue;
import org.mdpnp.gip.ui.values.TimeValueImpl;
import org.mdpnp.gip.ui.values.Value;
import org.mdpnp.gip.ui.values.ValueAdapter;
import org.mdpnp.gip.ui.values.VolumeValue;
import org.mdpnp.gip.ui.values.VolumeValueImpl;

/**
 * @author Jeff Plourde
 *
 */
public class InfusionModel extends AbstractModel<InfusionListener> {
	private final VolumeValue volumeToBeInfused = new VolumeValueImpl(VolumeUnitsFactory.milliliters);
	private final TimeValue duration = new TimeValueImpl(TimeUnitsFactory.minutes, TimeUnitsFactory.hours);
	private final RatioValue rate = new RatioValueImpl(RatioUnitsFactory.ratios);
	
	private static Double reasonableMaximum(VolumeUnits u) {
		if(VolumeUnitsFactory.milliliters.equals(u)) {
			return 1000.0;
		} else {
			return null;
		}
	}
	
	public InfusionModel() {
		volumeToBeInfused.setMinimum(0.0);
		duration.setMinimum(0.0);
		rate.setMinimum(0.0);
		
		volumeToBeInfused.setMaximum(reasonableMaximum(volumeToBeInfused.getUnits()));
		
		volumeToBeInfused.addListener(new ValueAdapter<VolumeUnits>() {
			@Override
			public void unitsChanged(Value<VolumeUnits> v) {
				v.setMaximum(reasonableMaximum(v.getUnits()));
			}
			@Override
			protected void anythingChanged(Value<VolumeUnits> v) {
				fireEvent();
			}
		});
		
		duration.addListener(new ValueAdapter<TimeUnits>() {
			@Override
			protected void anythingChanged(Value<TimeUnits> v) {
				fireEvent();
			}
			
		});
		
		rate.addListener(new ValueAdapter<RatioUnits>() {
			@Override
			protected void anythingChanged(Value<RatioUnits> v) {
				fireEvent();
			}
		});
	}
	
	public TimeValue getDuration() {
		return duration;
	}
	public VolumeValue getVolumeToBeInfused() {
		return volumeToBeInfused;
	}
	public RatioValue getRate() {
		return rate;
	}
	@Override
	protected void doFireEvent(Object event, InfusionListener listener) {
		listener.infusionChanged(this);
	}
}
