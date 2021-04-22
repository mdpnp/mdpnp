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

		if (numeric != null) {
			this.numeric = copyNumericFx(numeric);
		}

		if (sampleArray != null) {
			this.sampleArray = copySampleArrayFx(sampleArray);
		}

		this.sampleCount = sampleCount;
		this.frequency = frequency;
	}

	private NumericFx copyNumericFx(NumericFx numeric) {
		NumericFx num = new NumericFx();
		num.setDelta(numeric.getDelta());
		num.setDevice_time(numeric.getDevice_time());
		num.setInstance_id(numeric.getInstance_id());
		num.setMetric_id(numeric.getMetric_id());
		num.setPresentation_time(numeric.getPresentation_time());
		num.setReception_timestamp(numeric.getReception_timestamp());
		num.setSource_timestamp(numeric.getSource_timestamp());
		num.setSQI_accuracy(numeric.getSQI_accuracy());
		num.setSQI_accuracy_duration(numeric.getSQI_accuracy_duration());
		num.setSQI_completeness(numeric.getSQI_completeness());
		num.setSQI_frequency(numeric.getSQI_frequency());
		num.setSQI_precision(numeric.getSQI_precision());
		num.setUnique_device_identifier(numeric.getUnique_device_identifier());
		num.setUnit_id(numeric.getUnit_id());
		num.setValue(numeric.getValue());
		num.setVendor_metric_id(numeric.getVendor_metric_id());
		return num;
	}

	private SampleArrayFx copySampleArrayFx(SampleArrayFx sampleArray) {
		SampleArrayFx sample = new SampleArrayFx();
		sample.setDelta(sampleArray.getDelta());
		sample.setDevice_time(sampleArray.getDevice_time());
		sample.setFrequency(sampleArray.getFrequency());
		sample.setInstance_id(sampleArray.getInstance_id());
		sample.setMetric_id(sampleArray.getMetric_id());
		sample.setPresentation_time(sampleArray.getPresentation_time());
		sample.setReception_timestamp(sampleArray.getReception_timestamp());
		sample.setSource_timestamp(sampleArray.getSource_timestamp());
		sample.setSQI_accuracy(sampleArray.getSQI_accuracy());
		sample.setSQI_accuracy_duration(sampleArray.getSQI_accuracy_duration());
		sample.setSQI_completeness(sampleArray.getSQI_completeness());
		sample.setSQI_frequency(sampleArray.getSQI_frequency());
		sample.setSQI_precision(sampleArray.getSQI_precision());
		sample.setUnique_device_identifier(sampleArray.getUnique_device_identifier());
		sample.setUnit_id(sampleArray.getUnit_id());
		sample.setVendor_metric_id(sampleArray.getVendor_metric_id());
		sample.valuesProperty().setValue(sampleArray.getValues());
		return sample;
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
