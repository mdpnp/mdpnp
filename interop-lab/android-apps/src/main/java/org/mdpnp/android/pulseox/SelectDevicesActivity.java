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

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * selects a bluetooth device from a list of bound (and discovered) devices
 * @author jplourde
 *
 */
public class SelectDevicesActivity extends Activity implements OnItemClickListener {
	
	protected final static String SIMULATION = "SIMULATION";
	
	private static final int REQUEST_ENABLE_BT = 1;
	
	/**
	 * Broadcast receiver for BluetoothDevice.ACTION_FOUND
	 */
	private final BroadcastReceiver receiveDeviceDiscovered = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if(BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				doDeviceDiscovered(new MyBluetoothDevice(device));
			}
			
		}
	};
	
	/**
	 * Broadcast receiver for BluetoothAdapter.ACTION_SCAN_MODE_CHANGED
	 */
	private final BroadcastReceiver receiveScanmodeChange = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
				int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1);
				Log.i(logName, "ACTION_SCAN_MODE_CHANGED:"+scanMode);
				switch(scanMode) {
				case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
					button1.setText("Cancel Discovery");
					break;
				default:
					button1.setText("Search for Devices");
					break;
				}
			}
		}
	};
	
	private final BroadcastReceiver bondedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
				switch(bondState) {
				case BluetoothDevice.BOND_BONDED:
					Log.i(logName, "starting PulseOxActivity for newly bonded device:"+device);
					Intent i = new Intent(getBaseContext(), PulseOxActivity.class);
					i.putExtra("DEVICE", device);
					startActivity(i);
				default:
					
				}
			}
		};
	};
	
	private final BroadcastReceiver discoveryStarted = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
				button1.setText("Cancel Discovery");
			}
		};
	};
	
	private final BroadcastReceiver discoveryEnded = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
				button1.setText("Search for Devices");
			}
		};
	};
	
	protected void doDeviceDiscovered(MyBluetoothDevice device) {
		if(acceptDevice(device)) {
			if(deviceListAdapter.getPosition(device)<0) {
				deviceListAdapter.add(device);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiveDeviceDiscovered);
		unregisterReceiver(receiveScanmodeChange);
		unregisterReceiver(bondedReceiver);
		unregisterReceiver(discoveryStarted);
		unregisterReceiver(discoveryEnded);
	}
	
	protected boolean acceptDevice(MyBluetoothDevice device_) {
		BluetoothDevice device = device_.getDevice();
		
		if(null == device) {
			return true;
		} else {
			switch(device.getBluetoothClass().getDeviceClass()) {
			case 2324:
			case 7936:
				return true;
			default:
				Log.d(SelectDevicesActivity.class.getName(), "Rejected Device:"+device+"("+device.getName()+")");
				return false;
			}
		}
	}
	
	protected boolean acceptDevice(Set<MyBluetoothDevice> devices) {
		Set<MyBluetoothDevice> remove = new HashSet<MyBluetoothDevice>();
		for(MyBluetoothDevice device : devices) {
			if(!acceptDevice(device)) {
				remove.add(device);
			}
		}
		devices.removeAll(remove);
		return !devices.isEmpty();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
        button1.setText(bt.isDiscovering()?"Cancel Discovery":"Search for Devices");
        if(null != bt && bt.isEnabled()) {
        	populateBoundDevices();
        }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	
	protected void populateBoundDevices() {
		deviceListAdapter.clear();
		deviceListAdapter.add(new MyBluetoothDevice(null));
		
		
		Set<MyBluetoothDevice> devices = new HashSet<MyBluetoothDevice>();
		for(BluetoothDevice d : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
			devices.add(new MyBluetoothDevice(d));
		}
		Log.d(SelectDevicesActivity.class.getName(), "Bound Devices size:"+devices.size());
		if(acceptDevice(devices)) {
			for(MyBluetoothDevice device : devices) {
				deviceListAdapter.add(device);
			}
			
		} else {

		}
	}
	
	private ArrayAdapter<MyBluetoothDevice> deviceListAdapter;
	private Button button1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.selectdevices);
        registerReceiver(receiveDeviceDiscovered, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(receiveScanmodeChange, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
        registerReceiver(bondedReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        registerReceiver(discoveryStarted, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(discoveryEnded, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        deviceListAdapter = new BluetoothDeviceAdapter(getApplicationContext(), R.id.top_text);
        deviceListAdapter.add(new MyBluetoothDevice(null));
        button1 = (Button)findViewById(R.id.button1);
        ((ListView)findViewById(R.id.deviceList)).setAdapter(deviceListAdapter);
        ((ListView)findViewById(R.id.deviceList)).setOnItemClickListener(this);
		
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    	case REQUEST_ENABLE_BT:
    		switch(resultCode) {
    		case RESULT_OK:
    			populateBoundDevices();
    			BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
    			if(bt.isEnabled()) {
    				bt.startDiscovery();
    			}
    			break;
    		case RESULT_CANCELED:
    			break;
    		}
    		break;
    	default:
    		super.onActivityResult(requestCode, resultCode, data);
    	}
    }
    
    public void startDiscovery(View view) {
    	BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
    	if(bt != null) {
    		if(bt.isEnabled()) {
    			if(bt.isDiscovering()) {
    				bt.cancelDiscovery();
    			} else {
    				bt.startDiscovery();
    			}
    		} else {
    			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    		}
    	}
    }
    private static final String logName = PulseOxActivity.class.getName();
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		MyBluetoothDevice bd = (MyBluetoothDevice) arg0.getItemAtPosition(arg2);
		// Try to create a bond here... pausing *this* activity and returning before invoking the other activity
		if(null != bd.getDevice()) {
			switch(bd.getDevice().getBondState()) {
			case BluetoothDevice.BOND_NONE:
				Boolean b = BluetoothUtils.createBond(bd.getDevice());
				if(null != b) {
					if(b) {
						Log.i(logName, "Creating a bond for unbonded device:"+bd.getDevice());
						return;
					} else {
						Log.i(logName, "Unable to create a bond for unbonded device:"+bd.getDevice());
					}
				} else {
					Log.i(logName, "createBond is unavailable, bonding will occur on connect:"+bd.getDevice());
				}
				break;
			default:
				Log.i(logName, "device is already bonded or bonding:"+bd.getDevice());
			}
		}
		Intent i = new Intent(getBaseContext(), PulseOxActivity.class);
		i.putExtra("DEVICE", bd.getDevice());
		startActivity(i);
	}
}
