package org.mdpnp.gip.ui;

/**
 * Note from jplourde on Apr 9, 2013
 * MDCF components were enabled for the HIMSS 2013 demo
 * but they were built on a pre-release build of MDCF       
 */
public class InfusionPumpModelMDCFAdapter /*extends InfusionPump*/ {
    /*
	private final InfusionPumpModel model;
	
	public InfusionPumpModelMDCFAdapter(InfusionPumpModel model) {
		this.model = model;
	}
	
	
	@Override
	protected String buildMessage() {
		drugName = model.getDrug().getName();
		drugConcUnits = model.getDrug().getConcentration().getMass().getUnits().getAbbreviatedName() + "/" + model.getDrug().getConcentration().getVolume().getUnits().getAbbreviatedName();
		// TODO dose units vs. rate units ... figure it out
		doseUnits = model.getInfusion().getRate().getUnits().getAbbreviatedName();
		vtbiUnits = model.getInfusion().getVolumeToBeInfused().getUnits().getAbbreviatedName();
		rateUnits = model.getInfusion().getRate().getUnits().getAbbreviatedName();
		durationUnits = model.getInfusion().getDuration().getUnits().getName();
		Double rateValue = model.getInfusion().getRate().getValue();
		Double vtbiValue = model.getInfusion().getVolumeToBeInfused().getValue();
		Double concValue = model.getDrug().getConcentration().getValue();
		
		// TODO there is no null concept in our parent, so behaviour is a bit undefined
		if(null != rateValue) {
			rateVal = (float)(double)rateValue;
		}
		if(null != vtbiValue) {
			vtbiVal = (float)(double)vtbiValue;
		}
		if(null != concValue) {
			drugConcVal = (float)(double)(concValue);
		}

		// TODO fix this stupidity
		doseVal = drugConcVal * vtbiVal / (vtbiVal / rateVal);
		durationVal = vtbiVal / rateVal;
		percentComplete = 0f;

		String s = super.buildMessage();
		return s;
	}
	*/
}
