package org.mdpnp.devices;

public interface AbstractDeviceMBean {
    String[] getPartition();
    void setPartition(String[] partition);
    void addPartition(String partition);
    void removePartition(String partition);
    String getUniqueDeviceIdentifier();
    String getManufacturer();
    String getModel();
}
