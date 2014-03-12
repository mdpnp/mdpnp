package org.mdpnp.gip.ui;

/**
 * @author Jeff Plourde
 *
 */
public class DrugModel extends AbstractModel<DrugListener> implements ConcentrationListener {
	private String name;
	private final Concentration concentration = new Concentration();
	
	public DrugModel() {
		concentration.addListener(this);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Concentration getConcentration() {
		return concentration;
	}
	@Override
	protected void doFireEvent(Object event, DrugListener listener) {
		listener.drugChanged(this);
	}

	@Override
	public void concentrationChanged(Concentration c) {
		fireEvent();
	}
	
}
