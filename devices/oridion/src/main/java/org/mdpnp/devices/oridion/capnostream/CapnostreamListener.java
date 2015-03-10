/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices.oridion.capnostream;

import java.util.Date;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.oridion.capnostream.Capnostream.CO2Units;
import org.mdpnp.devices.oridion.capnostream.Capnostream.PulseOximetry;

public interface CapnostreamListener {
    void deviceIdSoftwareVersion(String softwareVersion, Date softwareReleaseDate, PulseOximetry pulseOximetry, String revision, String number);

    void numerics(DeviceClock.Reading sampleTime, int etCO2, int FiCO2, int respiratoryRate, int spo2, int pulserate, int slowStatus, int CO2ActiveAlarms, int SpO2,
            int extendedCO2Status, int etCo2AlarmHigh, int etCo2AlarmLow, int rrAlarmHigh, int rrAlarmLow, int fico2AlarmHigh, int spo2AlarmHigh,
            int spo2AlarmLow, int pulseAlarmHigh, int pulseAlarmLow, CO2Units units, int extendedCO2Status2);

    void co2Wave(DeviceClock.Reading sampleTime, int messageNumber, double co2, int status);
}
