package org.mdpnp.apps.testapp.export;

public class Value {
    private final String uniqueDeviceIdentifier, metricId;
    private final int instanceId;
    private long devTime;
    private double value;
    
    public Value(final String uniqueDeviceIdentifier, final String metricId, final int instanceId) {
        this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
        this.metricId = metricId;
        this.instanceId = instanceId;
    }
    
    public void updateFrom(long devTime, double value) {
        this.devTime = devTime;
        this.value = value;
    }
    public String getUniqueDeviceIdentifier() {
        return uniqueDeviceIdentifier;
    }
    public String getMetricId() {
        return metricId;
    }
    public long getDevTime() {
        return devTime;
    }
    public int getInstanceId() {
        return instanceId;
    }
    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Value {");
        sb.append("devTime=").append(devTime);
        sb.append(", uid='").append(uniqueDeviceIdentifier).append('\'');
        sb.append(", metricId='").append(metricId).append('\'');
        sb.append(", instanceId=").append(instanceId);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
