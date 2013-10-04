package org.mdpnp.clinicalscenarios.server.scenario;

import com.googlecode.objectify.annotation.Embed;

@SuppressWarnings("serial")
@Embed
public class EquipmentEntry implements java.io.Serializable {
    private String deviceType;
    private String manufacturer;
    private String model;
    private String rosettaId;
    
    //TICKET-140
    private String problemDescription; //is a general and brief description of the device problem
    private Boolean trainingRelatedProblem; //problem related to insuficient /inapropiate training
    private Boolean instructionsRelatedProblem; //outdated or confusing manuals
    private Boolean confusingDeviceUsage; //confusing interfaces
    private Boolean softwareRelatedProblem;
    private Boolean hardwareRelatedProblem;
    
    //getters and setters
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
	public String getProblemDescription() {
		return problemDescription;
	}
	public void setProblemDescription(String problemDescription) {
		this.problemDescription = problemDescription;
	}
	public Boolean getTrainingRelatedProblem() {
		return trainingRelatedProblem;
	}
	public void setTrainingRelatedProblem(Boolean trainingRelatedProblem) {
		this.trainingRelatedProblem = trainingRelatedProblem;
	}
	public Boolean getInstructionsRelatedProblem() {
		return instructionsRelatedProblem;
	}
	public void setInstructionsRelatedProblem(Boolean instructionsRelatedProblem) {
		this.instructionsRelatedProblem = instructionsRelatedProblem;
	}
	public Boolean getConfusingDeviceUsage() {
		return confusingDeviceUsage;
	}
	public void setConfusingDeviceUsage(Boolean confusingDeviceUsage) {
		this.confusingDeviceUsage = confusingDeviceUsage;
	}
	public Boolean getSoftwareRelatedProblem() {
		return softwareRelatedProblem;
	}
	public void setSoftwareRelatedProblem(Boolean softwareRelatedProblem) {
		this.softwareRelatedProblem = softwareRelatedProblem;
	}
	public Boolean getHardwareRelatedProblem() {
		return hardwareRelatedProblem;
	}
	public void setHardwareRelatedProblem(Boolean hardwareRelatedProblem) {
		this.hardwareRelatedProblem = hardwareRelatedProblem;
	}
    
}
