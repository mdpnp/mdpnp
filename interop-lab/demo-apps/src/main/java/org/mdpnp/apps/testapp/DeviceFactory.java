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
package org.mdpnp.apps.testapp;

import java.io.IOException;

import org.mdpnp.apps.testapp.Configuration.DeviceType;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.cpc.bernoulli.DemoBernoulli;
import org.mdpnp.devices.draeger.medibus.DemoApollo;
import org.mdpnp.devices.draeger.medibus.DemoEvita4;
import org.mdpnp.devices.draeger.medibus.DemoEvitaXL;
import org.mdpnp.devices.draeger.medibus.DemoV500;
import org.mdpnp.devices.draeger.medibus.DemoV500_38400;
import org.mdpnp.devices.fluke.prosim68.DemoProsim68;
import org.mdpnp.devices.hospira.symbiq.DemoSymbiq;
import org.mdpnp.devices.ivy._450c.DemoIvy450C;
import org.mdpnp.devices.masimo.radical.DemoRadical7;
import org.mdpnp.devices.nellcor.pulseox.DemoN595;
import org.mdpnp.devices.nonin.pulseox.DemoNoninPulseOx;
import org.mdpnp.devices.oridion.capnostream.DemoCapnostream20;
import org.mdpnp.devices.philips.intellivue.DemoEthernetIntellivue;
import org.mdpnp.devices.philips.intellivue.DemoSerialIntellivue;
import org.mdpnp.devices.simulation.co2.SimCapnometer;
import org.mdpnp.devices.simulation.ecg.SimElectroCardioGram;
import org.mdpnp.devices.simulation.multi.SimMultiparameter;
import org.mdpnp.devices.simulation.nibp.DemoSimulatedBloodPressure;
import org.mdpnp.devices.simulation.pulseox.SimPulseOximeter;
import org.mdpnp.devices.simulation.pump.SimInfusionPump;
import org.mdpnp.devices.simulation.temp.SimThermometer;
import org.mdpnp.rtiapi.data.EventLoop;

/**
 * @author Jeff Plourde
 *
 */
public class DeviceFactory {
    public static final AbstractDevice buildDevice(DeviceType type, int domainId, EventLoop eventLoop) throws NoSuchFieldException,
            SecurityException, IOException {
        switch (type) {
        case Nonin:
            return new DemoNoninPulseOx(domainId, eventLoop);
        case NellcorN595:
            return new DemoN595(domainId, eventLoop);
        case MasimoRadical7:
            return new DemoRadical7(domainId, eventLoop);
        case PO_Simulator:
            return new SimPulseOximeter(domainId, eventLoop);
        case NIBP_Simulator:
            return new DemoSimulatedBloodPressure(domainId, eventLoop);
        case IntellivueEthernet:
            return new DemoEthernetIntellivue(domainId, eventLoop);
        case IntellivueSerial:
            return new DemoSerialIntellivue(domainId, eventLoop);
        case Dr\u00E4gerApollo:
        case DraegerApollo:
            return new DemoApollo(domainId, eventLoop);
        case Dr\u00E4gerEvitaXL:
        case DraegerEvitaXL:
            return new DemoEvitaXL(domainId, eventLoop);
        case Dr\u00E4gerV500:
        case DraegerV500:
            return new DemoV500(domainId, eventLoop);
        case Dr\u00E4gerV500_38400:
        case DraegerV500_38400:
            return new DemoV500_38400(domainId, eventLoop);
        case Dr\u00E4gerEvita4:
        case DraegerEvita4:
            return new DemoEvita4(domainId, eventLoop);
        case Bernoulli:
            return new DemoBernoulli(domainId, eventLoop);
        case Capnostream20:
            return new DemoCapnostream20(domainId, eventLoop);
        case Symbiq:
            return new DemoSymbiq(domainId, eventLoop);
        case ECG_Simulator:
            return new SimElectroCardioGram(domainId, eventLoop);
        case CO2_Simulator:
            return new SimCapnometer(domainId, eventLoop);
        case Temp_Simulator:
            return new SimThermometer(domainId, eventLoop);
        case Pump_Simulator:
            return new SimInfusionPump(domainId, eventLoop);
        case Ivy450C:
            return new DemoIvy450C(domainId, eventLoop);
        case FlukeProsim68:
            return new DemoProsim68(domainId, eventLoop);
        case Multiparameter:
            return new SimMultiparameter(domainId, eventLoop);
        default:
            throw new RuntimeException("Unknown type:" + type);
        }
    }

}
