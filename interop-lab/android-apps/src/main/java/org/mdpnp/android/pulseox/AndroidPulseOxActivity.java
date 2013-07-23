package org.mdpnp.android.pulseox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

//import org.mdpnp.messaging.Gateway;
import org.mdpnp.devices.nonin.pulseox.DemoPulseOx;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class AndroidPulseOxActivity extends Activity {
    /** Called when the activity is first created. */
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_DISCOVERABLE = 2;
	
//	private static final Gateway gateway = new Gateway();
	
	private void doBlue2() {
		BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
		Log.d(AndroidPulseOxActivity.class.getName(), "STARTING DISCOVERABLE ACTIVITY");
		startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), REQUEST_DISCOVERABLE);
	}
	
	private void doListen() {
		new Thread(new Runnable() {
			public void run() {
				try {
					Log.d(AndroidPulseOxActivity.class.getName(), "CALLING LISTEN");
					BluetoothServerSocket sSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("BP APP 1", SPP_UUID);
					Log.d(AndroidPulseOxActivity.class.getName(), "CALLING ACCEPT");
					BluetoothSocket socket = sSocket.accept();
					if(socket != null) {
						Log.d(AndroidPulseOxActivity.class.getName(), "ACCEPTED FROM " + socket.getRemoteDevice().getAddress() + " " + socket.getRemoteDevice().getName());
						sSocket.close();
						BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String line = null;
						while( null != (line = reader.readLine()) ) {
							Log.d(AndroidPulseOxActivity.class.getName(), "*** "+line);
						}
					} else {
						Log.d(AndroidPulseOxActivity.class.getName(), "THE CLIENT SOCKET WAS NULL");
					}
				} catch (IOException e) {
					Log.e(AndroidPulseOxActivity.class.getName(), "ERROR LISTENING", e);

				}
			}
		}).start();
		
		
	}
	
	private void doBlue() {
		BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
		Log.d(AndroidPulseOxActivity.class.getName(), ""+bt.getBondedDevices().size());
		for(BluetoothDevice device : bt.getBondedDevices()) {
			Log.d(AndroidPulseOxActivity.class.getName(), device.getName());
			Log.d(AndroidPulseOxActivity.class.getName(), device.getAddress());
			Log.d(AndroidPulseOxActivity.class.getName(), device.getBluetoothClass().toString());
		}
		bt.startDiscovery();
	}
	private void doConnect(BluetoothDevice device, UUID uuid) {
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		
		try {
			BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
			DemoPulseOx pulseox = new DemoPulseOx(0);
//			pulseox.addListener(new PulseOximeterListener() {
//				public void pulseOximeter(final PulseOximeter po) {
//					runOnUiThread(new Runnable() {
//						public void run() {
//							((TextView)findViewById(R.id.heartRate)).setText(""+po.getHeartRate());
//							((TextView)findViewById(R.id.spO2)).setText(""+po.getSpO2());
//						}
//					});
//				}
//
//				@Override
//				public void device(Device device) {
//					// TODO Auto-generated method stub
//					
//				}
//
//			});
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private final static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private void doServiceDiscovery(BluetoothDevice device) {
		doConnect(device, SPP_UUID );
	}
//	private final ArrayAdapter aa = new ArrayAdapter();
	
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.d(AndroidPulseOxActivity.class.getName(), ""+device.getAddress());
				Log.d(AndroidPulseOxActivity.class.getName(), ""+device.getBondState());
				Log.d(AndroidPulseOxActivity.class.getName(), ""+device.getName());
				Log.d(AndroidPulseOxActivity.class.getName(), ""+device.getBluetoothClass().getDeviceClass());
				Log.d(AndroidPulseOxActivity.class.getName(), ""+device.getBluetoothClass().getMajorDeviceClass());
				if(device.getBluetoothClass().getDeviceClass()==2324) {
					doServiceDiscovery(device);
					
				}
			}
			
		}
	};
	private final BroadcastReceiver receiver2 = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
				int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1);
				String s ;
				switch(scanMode) {
				case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
					s = "CONNECTABLE";
					break;
				case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
					s = "CONNECTABLE_DISCOVERABLE";
					break;
				case BluetoothAdapter.SCAN_MODE_NONE:
					s = "NONE";
					break;
				}
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		unregisterReceiver(receiver2);
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        registerReceiver(receiver2, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
        if(null == bt) {
        	// forget it
        } else {
        	if(!bt.isEnabled()) {
        		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        		startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        	} else {
        		doBlue2();
        	}
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    	case REQUEST_ENABLE_BT:
    		switch(resultCode) {
    		case RESULT_OK:
    			doBlue2();
    			break;
    		}
    		break;
    	case REQUEST_DISCOVERABLE:
    		if(resultCode != RESULT_CANCELED) {
    			Log.d(AndroidPulseOxActivity.class.getName(), "OK to DISCOVERABLE " + resultCode);
    			doListen();
    		} else {
    			Log.d(AndroidPulseOxActivity.class.getName(), "CANCEL " + resultCode + " to DISCOVERABLE");
    		}
    	default:
    		super.onActivityResult(requestCode, resultCode, data);
    	}
    }
}