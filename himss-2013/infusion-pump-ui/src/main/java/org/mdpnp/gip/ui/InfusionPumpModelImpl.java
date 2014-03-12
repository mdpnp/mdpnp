package org.mdpnp.gip.ui;


/**
 * @author Jeff Plourde
 *
 */
public class InfusionPumpModelImpl implements InfusionPumpModel {
	private final DrugModel drug = new DrugModel();
	private final PatientModel patient = new PatientModel();
	private final InfusionModel infusion = new InfusionModel();
	
	public InfusionPumpModelImpl() {

	}
	
	@Override
	public PatientModel getPatient() {
		return this.patient;
	}
	@Override
	public DrugModel getDrug() {
		return drug;
	}
	
	@Override
	public InfusionModel getInfusion() {
		return infusion;
	}

	
}
