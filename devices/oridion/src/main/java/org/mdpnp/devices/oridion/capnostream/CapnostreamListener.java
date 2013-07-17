package org.mdpnp.devices.oridion.capnostream;

import java.util.Date;

import org.mdpnp.devices.oridion.capnostream.Capnostream.CO2Units;
import org.mdpnp.devices.oridion.capnostream.Capnostream.PulseOximetry;

public interface CapnostreamListener {
	void deviceIdSoftwareVersion(String softwareVersion, 
            Date softwareReleaseDate, PulseOximetry pulseOximetry, String revision, String number);
	void numerics(long date, int etCO2, int FiCO2,
            int respiratoryRate, int spo2, int pulserate, int slowStatus,
            int CO2ActiveAlarms, int SpO2, int extendedCO2Status, int etCo2AlarmHigh, int etCo2AlarmLow, int rrAlarmHigh, int rrAlarmLow, int fico2AlarmHigh, int spo2AlarmHigh, int spo2AlarmLow, int pulseAlarmHigh, int pulseAlarmLow, CO2Units units, int extendedCO2Status2);
	void co2Wave(int messageNumber, double co2, int status);
}
