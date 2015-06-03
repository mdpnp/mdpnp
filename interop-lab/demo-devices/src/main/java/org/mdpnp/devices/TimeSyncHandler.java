package org.mdpnp.devices;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.SampleInfo;
import ice.HeartBeat;
import ice.TimeSync;
import ice.TimeSyncDataWriter;

import java.util.HashMap;
import java.util.Map;


abstract class TimeSyncHandler {

	enum HandlerType { Chatty, SupervisorAware };

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

	private TimeSyncHandler(String uniqueDeviceIdentifier, ice.TimeSyncDataWriter tsWriter) {
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

		boolean b = shouldRespondTo(sampleInfo, heartbeat);
		if(!b)
			return;

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

	abstract boolean shouldRespondTo(SampleInfo sampleInfo, HeartBeat heartbeat);

	ice.TimeSyncDataWriter shutdown() {
		for(TimeSyncHolder holder : sync.values()) {
			tsWriter.unregister_instance(holder.timeSync, holder.handle);
		}
		sync.clear();
		return tsWriter;
	}

	static TimeSyncHandler makeTimeSyncHandler(HandlerType t, String uniqueDeviceIdentifier, TimeSyncDataWriter tsWriter) {
		switch(t) {
			case SupervisorAware:
				return new SupervisorAware(uniqueDeviceIdentifier, tsWriter);
			case Chatty:
			default:
				return new Chatty(uniqueDeviceIdentifier, tsWriter);
		}

	}

	private static class Chatty extends TimeSyncHandler {

		public Chatty(String uniqueDeviceIdentifier, TimeSyncDataWriter tsWriter) {
			super(uniqueDeviceIdentifier, tsWriter);
		}

		boolean shouldRespondTo(SampleInfo sampleInfo, HeartBeat heartbeat) {
			return true;
		}
	}

	private static class SupervisorAware extends TimeSyncHandler {

		private static final String TARGET_TYPE="Supervisor";

		public SupervisorAware(String uniqueDeviceIdentifier, TimeSyncDataWriter tsWriter) {
			super(uniqueDeviceIdentifier, tsWriter);
		}

		boolean shouldRespondTo(SampleInfo sampleInfo, HeartBeat heartbeat) {
			return TARGET_TYPE.equalsIgnoreCase(heartbeat.type);
		}
	}
}