package org.mdpnp.clinicalscenarios.client.scenario;

import org.mdpnp.clinicalscenarios.server.scenario.EquipmentEntry;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value=EquipmentEntry.class)
public interface EquipmentEntryProxy extends ValueProxy {
    String getDeviceType();
    void setDeviceType(String deviceType);
    String getManufacturer();
    void setManufacturer(String manufacturer);
    String getModel();
    void setModel(String model);
    String getRosettaId();
    void setRosettaId(String rosettaId);
    
    public String getProblemDescription();
	public void setProblemDescription(String problemDescription);
	
	public Boolean getTrainingRelatedProblem();
	public void setTrainingRelatedProblem(Boolean trainingRelatedProblem);
	
	public Boolean getInstructionsRelatedProblem();
	public void setInstructionsRelatedProblem(Boolean instructionsRelatedProblem);
	
	public Boolean getConfusingDeviceUsage();
	public void setConfusingDeviceUsage(Boolean confusingDeviceUsage);
	
	public Boolean getSoftwareRelatedProblem();
	public void setSoftwareRelatedProblem(Boolean softwareRelatedProblem);
	
	public Boolean getHardwareRelatedProblem();
	public void setHardwareRelatedProblem(Boolean hardwareRelatedProblem);
}
