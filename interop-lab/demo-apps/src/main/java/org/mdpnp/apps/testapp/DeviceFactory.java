package org.mdpnp.apps.testapp;

import java.io.IOException;

import org.mdpnp.apps.testapp.Configuration.DeviceType;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.cpc.bernoulli.DemoBernoulli;
import org.mdpnp.devices.draeger.medibus.DemoApollo;
import org.mdpnp.devices.draeger.medibus.DemoEvitaXL;
import org.mdpnp.devices.draeger.medibus.DemoV500;
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
import org.mdpnp.devices.simulation.nibp.DemoSimulatedBloodPressure;
import org.mdpnp.devices.simulation.pulseox.MultiSimPulseOximeter;
import org.mdpnp.devices.simulation.pulseox.SimPulseOximeter;
import org.mdpnp.devices.simulation.pump.SimInfusionPump;
import org.mdpnp.devices.simulation.temp.SimThermometer;

public class DeviceFactory {
    public static final AbstractDevice buildDevice(DeviceType type, int domainId, EventLoop eventLoop)
            throws NoSuchFieldException, SecurityException, IOException {
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
            return new DemoApollo(domainId, eventLoop);
        case Dr\u00E4gerEvitaXL:
            return new DemoEvitaXL(domainId, eventLoop);
        case Dr\u00E4gerV500:
            return new DemoV500(domainId, eventLoop);
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
        case MultiPO_Simulator:
            return new MultiSimPulseOximeter(domainId, eventLoop);
        default:
            throw new RuntimeException("Unknown type:" + type);
        }
    }

}
