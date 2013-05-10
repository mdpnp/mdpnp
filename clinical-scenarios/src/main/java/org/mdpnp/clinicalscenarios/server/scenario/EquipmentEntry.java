package org.mdpnp.clinicalscenarios.server.scenario;

import com.googlecode.objectify.annotation.Embed;

@SuppressWarnings("serial")
@Embed
public class EquipmentEntry implements java.io.Serializable {
    private String deviceType;
    private String manufacturer;
    private String model;
    private String rosettaId;
    public String getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getRosettaId() {
        return rosettaId;
    }
    public void setRosettaId(String rosettaId) {
        this.rosettaId = rosettaId;
    }
    
}
