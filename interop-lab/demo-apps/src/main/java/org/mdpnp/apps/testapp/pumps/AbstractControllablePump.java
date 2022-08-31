package org.mdpnp.apps.testapp.pumps;

import org.mdpnp.apps.fxbeans.AlertFxList;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.testapp.Device;

import ice.InfusionObjectiveDataWriter;
import ice.InfusionProgramDataWriter;

/**
 * A UI Controller for pumps that can be controlled by the pump controller application.
 * Exact UI elements depend on the pump and the data that it provides and allows to be
 * controlled.  Some basic methods that are expected to be widely applicable are defined.
 * @author Simon
 *
 */
public abstract class AbstractControllablePump {
	
	/**
	 * The device instance that represents the actual pump in the device tree.
	 * We have this so we can know the UDI, and therefore know the correct way
	 * to publish objectives etc.
	 */
	protected Device device;
	
	/**
	 * We have the NumericFxList so that extending classes can listen to it
	 * for things like flow rate.
	 */
	protected NumericFxList numericList;
	
	/**
	 * We have the AlertFxList so that extending classes can listen to it for
	 * things like alarms or other non numeric/sample device/patient info.  
	 */
	protected AlertFxList alertList;
	
	/**
	 * We have this so that extending classes can write pause/resume objectives. 
	 */
	protected InfusionObjectiveDataWriter infusionObjectiveWriter;
	
	/**
	 * We have this so that extending classes can write program objectives.
	 */
	protected InfusionProgramDataWriter infusionProgramDataWriter;
	
	/**
	 * Set the OpenICE Device instance that this controller is handling.
	 * @param device The device to use
	 */
	public void setDevice(Device device) {
		this.device=device;
	}
	
	public void setNumerics(NumericFxList numericList) {
		this.numericList=numericList;
	}
	
	public void setAlerts(AlertFxList alertList) {
		this.alertList=alertList;
	}
	
	public void setInfusionObjectiveDataWriter(InfusionObjectiveDataWriter writer) {
		this.infusionObjectiveWriter=writer;
	}
	
	public void setInfusionProgramDataWriter(InfusionProgramDataWriter writer) {
		this.infusionProgramDataWriter=writer;
	}

	public AbstractControllablePump() {
		

	}
	
	/**
	 * The channel number for this pump.
	 */
	protected int channel;
	
	/**
	 * The number of controllable channels the device has.  Assumed to be one in most cases,
	 * so subclasses should override if they have more than one.
	 */
	public int getChanneCount() {
		return 1;
	}
	
	/**
	 * The number of this channel to distinguish between channels if the pump has more than one.
	 * @return
	 */
	public int getChannel() {
		return channel;
	}
	
	/**
	 * Set the channel number for this instance.
	 * 
	 * @param channel The channel to set.
	 */
	public void setChannel(int channel) {
		this.channel=channel;
	}
	
	
	/**
	 * Set the flow rate/infusion rate for the given pump channel.  Accepting the channel number
	 * here as a <b>1 based integer (not 0)</b> avoids us needing separate methods for each channel
	 * that might not be required for most models that only have one channel.
	 * @param channel
	 */
	public void setFlowRate(int channel) {
		
	}
	
	/**
	 * This should be overridden by extending classes, and will be called by the application
	 * when the app has finished setting fields etc., so that the extending class can start
	 * updating its UI or whatever it does.
	 */
	public abstract void start();

}
