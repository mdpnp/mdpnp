package org.mdpnp.apps.testapp.networkmonitor;

import java.util.Date;
import java.util.Objects;

public class NetworkQualityMetric implements Comparable<NetworkQualityMetric> {
	private String deviceId;
	private Date presentationDate;
	private long delta;

	public NetworkQualityMetric() {
	}

	public NetworkQualityMetric(String deviceId, Date presentationDate, long delta) {
		super();
		this.deviceId = deviceId;
		this.presentationDate = presentationDate;
		this.delta = delta;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Date getPresentationDate() {
		return presentationDate;
	}

	public void setPresentationDate(Date presentationDate) {
		this.presentationDate = presentationDate;
	}

	public long getDelta() {
		return delta;
	}

	public void setDelta(long delta) {
		this.delta = delta;
	}

	@Override
	public int hashCode() {
		return Objects.hash(delta, deviceId, presentationDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NetworkQualityMetric)) {
			return false;
		}
		NetworkQualityMetric other = (NetworkQualityMetric) obj;
		return delta == other.delta && Objects.equals(deviceId, other.deviceId)
				&& Objects.equals(presentationDate, other.presentationDate);
	}

	@Override
	public String toString() {
		return "NetworkQualityMetric [deviceId=" + deviceId + ", presentationDate=" + presentationDate + ", delta="
				+ delta + "]";
	}

	@Override
	public int compareTo(NetworkQualityMetric other) {
		return this.getPresentationDate().compareTo(other.getPresentationDate());
	}
}
