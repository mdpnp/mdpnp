package org.mdpnp.apps.testapp.dataqualitymonitor;

import java.util.Objects;

public class DataQualityDisplayWrapper {
	private String deviceModel;
	private String deviceId;
	private String metricId;
	private Double accuracy;
	private Double completeness;
	private Double consistency;
	private Double credibility;
	private Double currentness;

	public DataQualityDisplayWrapper() {
		// TODO Auto-generated constructor stub
	}

	public DataQualityDisplayWrapper(String deviceModel, String deviceId, String metricId, Double accuracy,
			Double completeness, Double consistency, Double credibility, Double currentness) {
		super();
		this.deviceModel = deviceModel;
		this.deviceId = deviceId;
		this.metricId = metricId;
		this.accuracy = accuracy;
		this.completeness = completeness;
		this.consistency = consistency;
		this.credibility = credibility;
		this.currentness = currentness;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}

	public Double getCompleteness() {
		return completeness;
	}

	public void setCompleteness(Double completeness) {
		this.completeness = completeness;
	}

	public Double getConsistency() {
		return consistency;
	}

	public void setConsistency(Double consistency) {
		this.consistency = consistency;
	}

	public Double getCredibility() {
		return credibility;
	}

	public void setCredibility(Double credibility) {
		this.credibility = credibility;
	}

	public Double getCurrentness() {
		return currentness;
	}

	public void setCurrentness(Double currentness) {
		this.currentness = currentness;
	}

	@Override
	public int hashCode() {
		return Objects.hash(deviceId, metricId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DataQualityDisplayWrapper)) {
			return false;
		}
		DataQualityDisplayWrapper other = (DataQualityDisplayWrapper) obj;
		return Objects.equals(deviceId, other.deviceId) && Objects.equals(metricId, other.metricId);
	}

	@Override
	public String toString() {
		return "DataQualityDisplayWrapper [deviceModel=" + deviceModel + ", deviceId=" + deviceId + ", metricId="
				+ metricId + ", accuracy=" + accuracy + ", completeness=" + completeness + ", consistency="
				+ consistency + ", credibility=" + credibility + ", currentness=" + currentness + "]";
	}
}
