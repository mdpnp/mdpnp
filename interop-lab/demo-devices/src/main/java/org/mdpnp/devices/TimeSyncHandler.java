package org.mdpnp.devices;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.SampleInfo;
import ice.HeartBeat;
import ice.TimeSync;

import java.util.HashMap;
import java.util.Map;


class TimeSyncHandler {

	private static final class TimeSyncHolder {
		public final TimeSync timeSync;
		public final InstanceHandle_t handle;

		public TimeSyncHolder(final TimeSync timeSync, final InstanceHandle_t handle) {
			this.timeSync = timeSync;
			this.handle = handle;
		}
	}

	private final ice.TimeSyncDataWriter tsWriter;
	private final String uniqueDeviceIdentifier;
	private final Map<String,TimeSyncHolder> sync = new HashMap<String, TimeSyncHolder>();

	public TimeSyncHandler(String uniqueDeviceIdentifier, ice.TimeSyncDataWriter tsWriter) {
		this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
		this.tsWriter  = tsWriter;
	}

	void processNotAliveHeartbeat(final String unique_device_identifier) {
		TimeSyncHolder holder = sync.get(unique_device_identifier);
		if (null != holder) {
			tsWriter.unregister_instance(holder.timeSync, holder.handle);
		}
	}

	void handleTimeSync(SampleInfo sampleInfo, HeartBeat heartbeat) {
		TimeSyncHolder holder = sync.get(heartbeat.unique_device_identifier);
		if(sampleInfo.valid_data) {
			if(holder == null) {
				TimeSync ts = new TimeSync();
				ts.heartbeat_source = heartbeat.unique_device_identifier;
				ts.heartbeat_recipient = this.uniqueDeviceIdentifier;
				holder = new TimeSyncHolder(ts, tsWriter.register_instance(ts));
				sync.put(heartbeat.unique_device_identifier, holder);
			}
			holder.timeSync.source_source_timestamp.sec = sampleInfo.source_timestamp.sec;
			holder.timeSync.source_source_timestamp.nanosec = sampleInfo.source_timestamp.nanosec;
			holder.timeSync.recipient_receipt_timestamp.sec = sampleInfo.reception_timestamp.sec;
			holder.timeSync.recipient_receipt_timestamp.nanosec = sampleInfo.reception_timestamp.nanosec;
			tsWriter.write(holder.timeSync, holder.handle);
		}
	}

	ice.TimeSyncDataWriter shutdown() {
		for(TimeSyncHolder holder : sync.values()) {
			tsWriter.unregister_instance(holder.timeSync, holder.handle);
		}
		sync.clear();
		return tsWriter;
	}
}