/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.android.pulseox;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.devices.connected.AbstractGetConnected;
import org.mdpnp.devices.masimo.radical.DemoRadical7;
import org.mdpnp.devices.nellcor.pulseox.DemoN595;
import org.mdpnp.devices.nonin.pulseox.DemoPulseOx;
import org.mdpnp.devices.nonin.pulseox.DemoPulseOx.Bool;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.simulation.pulseox.SimPulseOximeter;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

/**
 * @author Jeff Plourde
 *
 */
public class PulseOxActivity extends Activity implements GatewayListener {
	private AbstractDevice pulseox;
	private WaveformRepresentation wave;
	private TextView heartRate, spo2, state, name, guid;
	private BluetoothDevice device;
	private EventLoop eventLoop;
	private EventLoopHandler eventLoopHandler;
	private DeviceMonitor deviceMonitor;
	
//	private Gateway gateway; // = new Gateway();
	
	private WaveformUpdateWaveformSource wuws; // = new WaveformUpdateWaveformSource();
	
	private static final String logName = PulseOxActivity.class.getName();
	private static final void debug(String str) {
		Log.d(logName, str);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		debug("*** onStart ***");
		int domainId = 0;
//		gateway.addListener(this);
		eventLoop = new EventLoop();
		eventLoopHandler = new EventLoopHandler(eventLoop);
		
		if(null == pulseox) {
			if(null == device) {
				pulseox = new SimPulseOximeter(domainId);
			} else if(device.getName().startsWith("Nellcor")) {
				try {
					pulseox = new DemoN595(domainId);
				} catch (Exception e) {
					Log.e(PulseOxActivity.class.getName(), "Unable to construct", e);
				}
			} else if(device.getName().startsWith("Masimo")) {
				try {
					pulseox = new DemoRadical7(domainId);
				} catch (Exception e) {
					Log.e(PulseOxActivity.class.getName(), "Unable to construct", e);
				}
			} else {
				pulseox = new DemoPulseOx(domainId);
			}
			
		}
		getConnected = new GetConnected(domainId, pulseox.getDeviceIdentity().unique_device_identifier, null==device?"":device.getAddress(), eventLoop);
		
		wave.setSource(wuws);
	}
	@Override
	protected void onStop() {
		debug("*** onStop ***");
		gateway.removeListener(this);
        if(null != wave) {
        	wave.setSource(null);
        }
        
        if(null != pulseox) {
        	gateway.removeListener(pulseox);
			pulseox = null;
		}
        getConnected = null;
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		debug("*** onPause ***");
		wave.pause();
		// Thing here is that as part of connecting a pairing dialog may
		// be presented and trigger this onPause... so we must allow a continuation of
		// the pairing process across onPause/onResume
		ice.ConnectionState state = this.lastState;
		if(state != null) {
			switch(state.ordinal()) {
			case ice.ConnectionState._Connected:
			case ice.ConnectionState._Negotiating:
				getConnected.disconnect();
				break;
			default:
			}
		}
//		getConnected.disconnect();
	}
	
	private static class GetConnected extends AbstractGetConnected {
		private final String address;
		
		GetConnected(int domainId, String unique_device_identifier, String address, EventLoop eventLoop) {
			super(domainId, unique_device_identifier, eventLoop);
			this.address = address;
		}
		
		@Override
		protected void abortConnect() {
			
		}

		@Override
		protected String addressFromUser() {
			return address;
		}

		@Override
		protected String addressFromUserList(String[] list) {
			return address;
		}
		@Override
		protected boolean isFixedAddress() {
			return true;
		}
	}
	private GetConnected getConnected;
	
	@Override
	protected void onResume() {
		debug("*** onResume ***");
		super.onResume();
		wave.resume();
		getConnected.connect();
	}
	

	
	@Override
	protected void onDestroy() {
		debug("*** onDestroy ***");
		super.onDestroy();

		this.device = null;
		this.wuws = null;
//		this.gateway = null;
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		debug("*** onRetainNonConfigurationInstance ***");
		return device;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		debug("*** onSaveInstanceState ***");
		super.onSaveInstanceState(outState);
		outState.putParcelable("DEVICE", device);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		debug("*** onCreate ***");
        super.onCreate(savedInstanceState);
//        gateway = new Gateway();
        wuws = new WaveformUpdateWaveformSource();
        
        SerialProviderFactory.setDefaultProvider(new BluetoothSerialProvider());
        setContentView(R.layout.activity_pulse_ox);

        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        wave = (WaveformRepresentation)findViewById(R.id.waveform);
		heartRate = ((TextView)findViewById(R.id.heart_rate));
		spo2 = ((TextView)findViewById(R.id.spo2)); 	
		name = ((TextView) findViewById(R.id.name));
		guid = ((TextView) findViewById(R.id.guid));
		state = ((TextView) findViewById(R.id.status));

		noValue = getString(R.string.no_value);
        
        Object lnci = getLastNonConfigurationInstance();
        if(null == lnci) {
        	device = getIntent().getParcelableExtra("DEVICE");
        	if(device == null && savedInstanceState != null) {
        		device = savedInstanceState.getParcelable("DEVICE");
        	}
        } else {
        	this.device = (BluetoothDevice) lnci;
        }
    }

	private static class DeviceUpdate implements Runnable {
		private TextView textView;
		private String value;
		 
		public DeviceUpdate(TextView textView, String value) {
			set(textView, value);
		}
		
		public DeviceUpdate set(TextView textView, String value) {
			this.textView = textView;
			this.value = value;
			return this;
		}
		public void run() {
			textView.setText(value);
		}
	}

	private String noValue;
	
	private ice.ConnectionState lastState;
	private String lastConnectionInfo;
	
	private void doStateAndConnectionInfo() {
		runOnUiThread(new DeviceUpdate(this.state, (null == lastState ? noValue : lastState.toString())+((null == lastConnectionInfo || "".equals(lastConnectionInfo)) ? "" : ("("+lastConnectionInfo+")")) ));
	}

	private boolean outOfTrack = false;
	
	@Override
	public void update(IdentifiableUpdate<?> update) {
		if(Device.NAME.equals(update.getIdentifier())) {
			runOnUiThread(new DeviceUpdate(name, ((TextUpdate)update).getValue()));
		} else if(Device.GUID.equals(update.getIdentifier())) {
			runOnUiThread(new DeviceUpdate(guid, ((TextUpdate)update).getValue()));
		} else if(PulseOximeter.SPO2.equals(update.getIdentifier())) {
			Number number = ((NumericUpdate)update).getValue();
			if(number!=null&&number.intValue()>100) {
				number = null;
			}
			runOnUiThread(new DeviceUpdate(spo2, null == number ? noValue :Integer.toString(number.intValue()))); 
		} else if(PulseOximeter.PULSE.equals(update.getIdentifier())) {
			Number number = ((NumericUpdate)update).getValue();
			if(number!=null&&number.intValue()>250) {
				number = null;
			}
			runOnUiThread(new DeviceUpdate(heartRate, null == number ? noValue :Integer.toString(number.intValue())));
		} else if(PulseOximeter.PLETH.equals(update.getIdentifier())) {
			wuws.applyUpdate((WaveformUpdate) update);
		} else if(ConnectedDevice.STATE.equals(update.getIdentifier())) {
			ConnectedDevice.State newState = (ConnectedDevice.State) ((EnumerationUpdate)update).getValue(); 
			if((lastState == null || !ConnectedDevice.State.Connected.equals(lastState)) &&
				newState != null && ConnectedDevice.State.Connected.equals(newState)) {
				Log.d(logName, "Resetting the WaveformUpdateWaveformSource where newly Connected");
				wuws.reset();
			}
			lastState = newState;
			doStateAndConnectionInfo();
		} else if(ConnectedDevice.CONNECTION_INFO.equals(update.getIdentifier())) {
			lastConnectionInfo = ((TextUpdate)update).getValue();
			doStateAndConnectionInfo();
		} else if(DemoPulseOx.PERFUSION.equals(update.getIdentifier())) {
		    DemoPulseOx.Perfusion perfusion = (DemoPulseOx.Perfusion) ((EnumerationUpdate)update).getValue(); 
			if(null != perfusion) {
				switch(perfusion) {
				case Green:
					spo2.setTextColor(getResources().getColor(R.color.greenTextColor));
					break;
				case Red:
					spo2.setTextColor(getResources().getColor(R.color.redTextColor));
					break;
				case Yellow:
					spo2.setTextColor(getResources().getColor(R.color.yellowTextColor));
					break;
				default:
					break;
				
				}
			} else {
				spo2.setTextColor(getResources().getColor(R.color.greenTextColor));
			}
		} else if(DemoPulseOx.FIRMWARE_REVISION.equals(update.getIdentifier())) {
			
		} else if(DemoPulseOx.OUT_OF_TRACK.equals(update.getIdentifier())) {
			Bool _outOfTrack = (Bool) ((EnumerationUpdate)update).getValue();
			boolean outOfTrack = Bool.True.equals(_outOfTrack);
			if(this.outOfTrack ^ outOfTrack) {
				this.outOfTrack = outOfTrack;
				if(!outOfTrack) {
					Log.d(logName, "NO LONGER OUT OF TRACK");
					wave.setOutOfTrack(false);
					wave.setForeground(getResources().getColor(R.color.greenTextColor));
					wuws.reset();
				} else {
					wave.setOutOfTrack(true);
					wave.setForeground(getResources().getColor(R.color.yellowTextColor));
					Log.d(logName, "SENSOR OUT OF TRACK");
				}
			}
		}
	}

}
