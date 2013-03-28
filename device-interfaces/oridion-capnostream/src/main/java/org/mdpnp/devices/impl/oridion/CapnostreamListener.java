package org.mdpnp.devices.impl.oridion;

public interface CapnostreamListener {
	void deviceIdSoftwareVersion(String s);
	void numerics(long date, int etCO2, int FiCO2, int respiratoryRate, int spo2, int pulserate);
	void co2Wave(int messageNumber, double co2, int status);
}
