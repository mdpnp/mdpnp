package org.mdpnp.devices.masimo.radical;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mdpnp.devices.io.ASCIIFieldDelegate;

public class MasimoRadical7 extends ASCIIFieldDelegate {

	private Integer heartRate, spo2, perfusionIndex, spco, spmet, desat, pidelta, pvi;
	private String guid;
	private Date lastPoint;
	
	public MasimoRadical7() throws IOException,
			NoSuchFieldException, SecurityException {
		super(MasimoRadical7.class.getResource("masimo-radical-7.spec"));
	}
	
	private static final Pattern number = Pattern.compile("^(\\d+)$");	
	@SuppressWarnings("unused")
	private static final String filter(String val) {
		if(null == val) {
			return null;
		}
		Matcher m1 = number.matcher(val);
		if(m1.matches()) {
			return m1.group(1);
		} else {
			return null;
		}
	}
	
	public void firePulseOximeter() {
		// Fired when all fields have been set
	}

	public Integer getHeartRate() {
		return heartRate;
	}

	public Integer getSpO2() {
		return spo2;
	}
	
	public Integer getDesat() {
		return desat;
	}

	public Integer getPerfusionIndex() {
		return perfusionIndex;
	}
	
	public Integer getPIDelta() {
		return pidelta;
	}
	
	public Integer getPlethVariabilityIndex() {
		return pvi;
	}
	
	public Integer getSpCO() {
		return spco;
	}
	
	public Integer getSpMet() {
		return spmet;
	}
	public Date getTimestamp() {
		return lastPoint;
	}
	public String getUniqueId() {
		return guid;
	}
}
