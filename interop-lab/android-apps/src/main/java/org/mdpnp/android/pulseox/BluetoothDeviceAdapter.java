package org.mdpnp.android.pulseox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BluetoothDeviceAdapter extends ArrayAdapter<MyBluetoothDevice> {

	public BluetoothDeviceAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		boolean needsLayout = null != v;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item, null);
		}
		
		String name = "";
		{
			MyBluetoothDevice i = getItem(position);
			if (i != null) {
				name = i.getName();
			}
		}

		TextView tt = (TextView) v.findViewById(R.id.top_text);

		if (tt != null) {
			tt.setText(name);
		}
		
		if(needsLayout) {
			v.forceLayout();
		}
		return v;
	}
}
