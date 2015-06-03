package org.mdpnp.devices;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.SampleInfo;
import ice.HeartBeat;
import ice.TimeSync;
import ice.TimeSyncDataWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


abstract class TimeSyncHandler {

	private final Logger log = LoggerFactory.getLogger(TimeSyncHandler.class);

	enum HandlerType { Chatty, SupervisorAware }

	private static final class TimeSyncHolder {
		final TimeSync timeSync;
		final InstanceHandle_t handle;
		long  lastSync = 0;

		public TimeSyncHolder(final TimeSync timeSync, final InstanceHandle_t handle) {
			this.timeSync = timeSync;
			this.handle = handle;
		}
	}

	private final ice.TimeSyncDataWriter tsWriter;
	private final String uniqueDeviceIdentifier;
	private final Map<String,TimeSyncHolder> sync = new HashMap<>();

	private TimeSyncHandler(String uniqueDeviceIdentifier, ice.TimeSyncDataWriter tsWriter) {
		if(tsWriter == null)
			throw new IllegalArgumentException("Writer cannot be null");

		this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
		this.tsWriter  = tsWriter;
	}

	void processNotAliveHeartbeat(final String unique_device_identifier) {
		TimeSyncHolder holder = sync.remove(unique_device_identifier);
		if (null != holder) {
			tsWriter.unregister_instance(holder.timeSync, holder.handle);
		}
	}

	void handleTimeSync(SampleInfo sampleInfo, HeartBeat heartbeat) {

		if(!sampleInfo.valid_data)
			return;

		TimeSyncHolder holder = sync.get(heartbeat.unique_device_identifier);
		if(holder == null) {
			TimeSync ts = new TimeSync();
			ts.heartbeat_source = heartbeat.unique_device_identifier;
			ts.heartbeat_recipient = this.uniqueDeviceIdentifier;
			holder = new TimeSyncHolder(ts, tsWriter.register_instance(ts));
			sync.put(heartbeat.unique_device_identifier, holder);
		}

		boolean b = shouldRespondTo(holder, heartbeat);
		if(log.isDebugEnabled())
			log.debug(uniqueDeviceIdentifier + " will " + ((b) ? "" : "not ") +
					  "respond to ping from " + heartbeat.unique_device_identifier);

		if(b) {
			fill(sampleInfo, holder);

			tsWriter.write(holder.timeSync, holder.handle);
		}
	}

	void fill(SampleInfo sampleInfo, TimeSyncHolder holder) {
		holder.timeSync.source_source_timestamp.sec = sampleInfo.source_timestamp.sec;
		holder.timeSync.source_source_timestamp.nanosec = sampleInfo.source_timestamp.nanosec;
		holder.timeSync.recipient_receipt_timestamp.sec = sampleInfo.reception_timestamp.sec;
		holder.timeSync.recipient_receipt_timestamp.nanosec = sampleInfo.reception_timestamp.nanosec;
	}

	abstract boolean shouldRespondTo(TimeSyncHolder holder, HeartBeat heartbeat);

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
				return new TypeAware("Supervisor", uniqueDeviceIdentifier, tsWriter);
			case Chatty:
			default:
				return new Chatty(uniqueDeviceIdentifier, tsWriter);
		}

	}

	private static class Chatty extends TimeSyncHandler {

		public Chatty(String uniqueDeviceIdentifier, TimeSyncDataWriter tsWriter) {
			super(uniqueDeviceIdentifier, tsWriter);
		}

		boolean shouldRespondTo(TimeSyncHolder holder, HeartBeat heartbeat) {
			return true;
		}
	}

	private static class TypeAware extends TimeSyncHandler {

		private final String targetType;

		public TypeAware(String targetType, String uniqueDeviceIdentifier, TimeSyncDataWriter tsWriter) {
			super(uniqueDeviceIdentifier, tsWriter);
			this.targetType  = targetType;
		}

		boolean shouldRespondTo(TimeSyncHolder holder, HeartBeat heartbeat) {
			return targetType.equalsIgnoreCase(heartbeat.type);
		}
	}


	private static class Infrequent extends TimeSyncHandler {

		private final long deltaMs;

		public Infrequent(long deltaMs, String uniqueDeviceIdentifier, TimeSyncDataWriter tsWriter) {
			super(uniqueDeviceIdentifier, tsWriter);
			this.deltaMs = deltaMs;
		}

		boolean shouldRespondTo(TimeSyncHolder holder, HeartBeat heartbeat) {
			long now = System.currentTimeMillis();
			return (now-holder.lastSync)>deltaMs;
		}

		void fill(SampleInfo sampleInfo, TimeSyncHolder holder) {
			super.fill(sampleInfo, holder);
			holder.lastSync = System.currentTimeMillis();
		}
	}

}