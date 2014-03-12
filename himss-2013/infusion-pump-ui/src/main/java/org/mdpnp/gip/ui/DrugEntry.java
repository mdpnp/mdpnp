package org.mdpnp.gip.ui;


/**
 * @author Jeff Plourde
 *
 */
public class DrugEntry {
	private final String name;
	
	private Double amount, diluent, concentration;
	private Double lowerHardLimit, lowerSoftLimit, startingRate, upperSoftLimit, upperHardLimit, volumeToBeInfused;
	private Double bolusLowerHardLimit, bolusLowerSoftLimit, bolusAmount, bolusUpperSoftLimit, bolusUpperHardLimit;
	private String amountUnits, diluentUnits, concentrationUnits, doseMode;
	
	public DrugEntry(String name, Double amount, String amountUnits, Double diluent, String diluentUnits, Double concentration, String concentrationUnits,
					 String doseMode, Double lowerHardLimit, Double lowerSoftLimit, Double startingRate, Double upperSoftLimit, Double upperHardLimit, Double volumeToBeInfused,
					 Double bolusLowerHardLimit, Double bolusLowerSoftLimit, Double bolusAmount, Double bolusUpperSoftLimit, Double bolusUpperHardLimit) {
		this.name = name;
		this.amount = amount;
		this.amountUnits = amountUnits;
		this.diluent = diluent;
		this.diluentUnits = diluentUnits;
		this.concentration = concentration;
		this.concentrationUnits = concentrationUnits;
		this.doseMode = doseMode;
		this.lowerHardLimit = lowerHardLimit;
		this.lowerSoftLimit = lowerSoftLimit;
		this.startingRate = startingRate;
		this.upperSoftLimit = upperSoftLimit;
		this.upperHardLimit = upperHardLimit;
		this.volumeToBeInfused = volumeToBeInfused;
		this.bolusLowerHardLimit = bolusLowerHardLimit;
		this.bolusLowerSoftLimit = bolusLowerSoftLimit;
		this.bolusAmount = bolusAmount;
		this.bolusUpperSoftLimit = bolusUpperSoftLimit;
		this.bolusUpperHardLimit = bolusUpperHardLimit;
	}
	public DrugEntry(String name, Double amount, String amountUnits, Double diluent, String diluentUnits, Double concentration, String concentrationUnits,
			 String doseMode, Double lowerHardLimit, Double lowerSoftLimit, Double startingRate, Double upperSoftLimit, Double upperHardLimit, Double volumeToBeInfused) {
		this(name, amount, amountUnits, diluent, diluentUnits, concentration, concentrationUnits, doseMode, lowerHardLimit, lowerSoftLimit, startingRate, upperSoftLimit, upperHardLimit, volumeToBeInfused,
				null, null, null, null, null);
	}
	public DrugEntry(String name) {
		this(name, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null);
	}
	@Override
	public String toString() {
		return getName();
//		return Util.toString(this);
	}
	public String getName() {
		return name;
	}
	public Double getAmount() {
		return amount;
	}
	public Double getDiluent() {
		return diluent;
	}
	public Double getConcentration() {
		return concentration;
	}
	public Double getLowerHardLimit() {
		return lowerHardLimit;
	}
	public Double getLowerSoftLimit() {
		return lowerSoftLimit;
	}
	public Double getStartingRate() {
		return startingRate;
	}
	public Double getUpperSoftLimit() {
		return upperSoftLimit;
	}
	public Double getUpperHardLimit() {
		return upperHardLimit;
	}
	public Double getVolumeToBeInfused() {
		return volumeToBeInfused;
	}
	public Double getBolusLowerHardLimit() {
		return bolusLowerHardLimit;
	}
	public Double getBolusLowerSoftLimit() {
		return bolusLowerSoftLimit;
	}
	public Double getBolusAmount() {
		return bolusAmount;
	}
	public Double getBolusUpperSoftLimit() {
		return bolusUpperSoftLimit;
	}
	public Double getBolusUpperHardLimit() {
		return bolusUpperHardLimit;
	}
	public String getAmountUnits() {
		return amountUnits;
	}
	public String getDiluentUnits() {
		return diluentUnits;
	}
	public String getConcentrationUnits() {
		return concentrationUnits;
	}
	public String getDoseMode() {
		return doseMode;
	}
	
}
