package org.mdpnp.apps.testapp.dataqualitymonitor;

import java.util.Date;
import java.util.Objects;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.SampleArrayFx;

public class DataQualityMetric implements Comparable<DataQualityMetric> {
	private String deviceId;
	private String metricId;
	private Date presentationDate;
	private NumericFx numeric;
	private SampleArrayFx sampleArray;
	private int sampleCount;
	private double frequency;

	public DataQualityMetric() {
	}

	public DataQualityMetric(String deviceId, String metricId, Date presentationDate, NumericFx numeric,
			SampleArrayFx sampleArray, int sampleCount, double frequency) {
		super();
		this.deviceId = deviceId;
		this.metricId = metricId;
		this.presentationDate = presentationDate;
		this.numeric = numeric;
		this.sampleArray = sampleArray;
		this.sampleCount =  sampleCount;
		this.frequency = frequency;
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

	public Date getPresentationDate() {
		return presentationDate;
	}

	public void setPresentationDate(Date presentationDate) {
		this.presentationDate = presentationDate;
	}

	public NumericFx getNumeric() {
		return numeric;
	}

	public void setNumeric(NumericFx numeric) {
		this.numeric = numeric;
	}

	public SampleArrayFx getSampleArray() {
		return sampleArray;
	}

	public void setSampleArray(SampleArrayFx sampleArray) {
		this.sampleArray = sampleArray;
	}
	
	public int getSampleCount() {
		return sampleCount;
	}

	public void setSampleCount(int sampleCount) {
		this.sampleCount = sampleCount;
	}

	public double getFrequency() {
		return frequency;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	@Override
	public int hashCode() {
		return Objects.hash(deviceId, frequency, metricId, numeric, presentationDate, sampleArray, sampleCount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DataQualityMetric)) {
			return false;
		}
		DataQualityMetric other = (DataQualityMetric) obj;
		return Objects.equals(deviceId, other.deviceId) && frequency == other.frequency
				&& Objects.equals(metricId, other.metricId) && Objects.equals(numeric, other.numeric)
				&& Objects.equals(presentationDate, other.presentationDate)
				&& Objects.equals(sampleArray, other.sampleArray) && sampleCount == other.sampleCount;
	}

	@Override
	public int compareTo(DataQualityMetric other) {
		return this.getPresentationDate().compareTo(other.getPresentationDate());
	}
}