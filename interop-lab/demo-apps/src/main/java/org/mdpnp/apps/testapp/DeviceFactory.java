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

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.DeviceDriverProvider.SpringLoadedDriver;
import org.mdpnp.devices.cpc.bernoulli.DemoBernoulli;
import org.mdpnp.devices.draeger.medibus.*;
import org.mdpnp.devices.fluke.prosim68.DemoProsim68;
import org.mdpnp.devices.hospira.symbiq.DemoSymbiq;
import org.mdpnp.devices.ivy._450c.DemoIvy450C;
import org.mdpnp.devices.masimo.radical.DemoRadical7;
import org.mdpnp.devices.nellcor.pulseox.DemoN595;
import org.mdpnp.devices.nonin.pulseox.DemoNoninPulseOx;
import org.mdpnp.devices.oridion.capnostream.DemoCapnostream20;
import org.mdpnp.devices.philips.intellivue.DemoSerialIntellivue;
import org.mdpnp.devices.simulation.co2.SimCapnometer;
import org.mdpnp.devices.simulation.ecg.SimElectroCardioGram;
import org.mdpnp.devices.simulation.multi.SimMultiparameter;
import org.mdpnp.devices.simulation.nibp.DemoSimulatedBloodPressure;
import org.mdpnp.devices.simulation.pulseox.SimPulseOximeter;
import org.mdpnp.devices.simulation.pump.SimInfusionPump;
import org.mdpnp.devices.simulation.temp.SimThermometer;
import org.mdpnp.devices.zephyr.biopatch.DemoBioPatch;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.*;

public class DeviceFactory {

    static DeviceDriverProvider[] getAvailableDevices() {
        ServiceLoader<DeviceDriverProvider> l = ServiceLoader.load(DeviceDriverProvider.class);

        Collection<DeviceDriverProvider> all = new TreeSet<DeviceDriverProvider>(new Comparator<DeviceDriverProvider>() {
            @Override
            public int compare(DeviceDriverProvider o1, DeviceDriverProvider o2) {
                return o1.getDeviceType().toString().compareTo(o2.getDeviceType().toString());
            }
        });

        final Iterator<DeviceDriverProvider> iter = l.iterator();
        while (iter.hasNext()) {
            all.add(iter.next());
        }

        DeviceDriverProvider[] arr = all.toArray(new DeviceDriverProvider[all.size()]);
        return arr;
    }

    static DeviceDriverProvider getDeviceDriverProvider(String alias) {
        ServiceLoader<DeviceDriverProvider> l = ServiceLoader.load(DeviceDriverProvider.class);
        final Iterator<DeviceDriverProvider> iter = l.iterator();
        while (iter.hasNext()) {
            DeviceDriverProvider ddp = iter.next();
            if(alias.equals(ddp.getDeviceType().getAlias()))
                return ddp;
        }
        throw new IllegalArgumentException("Cannot resolve '" + alias + " to a known device");
    }


    public static class PO_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "Pulse Oximeter", "PO_Simulator");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new SimPulseOximeter(domainId, eventLoop);
        }
    }

    public static class NIBP_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "Noninvasive Blood Pressure", "NIBP_Simulator");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoSimulatedBloodPressure(domainId, eventLoop);
        }
    }

    public static class ECG_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "ElectroCardioGram", "ECG_Simulator");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new SimElectroCardioGram(domainId, eventLoop);
        }
    }

    public static class CO2_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "Capnometer", "CO2_Simulator");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new SimCapnometer(domainId, eventLoop);
        }
    }

    public static class Temp_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "Temperature Probe", "Temp_Simulator");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new SimThermometer(domainId, eventLoop);
        }
    }

    public static class Pump_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "Infusion Pump", "Pump_Simulator");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new SimInfusionPump(domainId, eventLoop);
        }
    }

    public static class FlukeProsim68Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Fluke", "Prosim 6/8" , "FlukeProsim68");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoProsim68(domainId, eventLoop);
        }
    }

    public static class BernoulliProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Network, "CardioPulmonaryCorp", "Bernoulli", "Bernoulli");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoBernoulli(domainId, eventLoop);
        }
    }

    public static class Ivy450CProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Ivy", "450C Monitor", "Ivy450C");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoIvy450C(domainId, eventLoop);
        }
    }

    public static class NoninProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Nonin", "Bluetooth Pulse Oximeter", "Nonin");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoNoninPulseOx(domainId, eventLoop);
        }
    }

    public static class IntellivueEthernetProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Network, "Philips", "Intellivue (LAN)", "IntellivueEthernet");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoSerialIntellivue(domainId, eventLoop);
        }
    }

    public static class IntellivueSerialProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Philips", "Intellivue (MIB/RS232)", "IntellivueSerial");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoSerialIntellivue(domainId, eventLoop);
        }
    }

    public static class Capnostream20Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Oridion", "Capnostream20", "Capnostream20");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoCapnostream20(domainId, eventLoop);
        }
    }


    public static class NellcorN595Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Nellcor", "N-595", "NellcorN595");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoN595(domainId, eventLoop);
        }
    }

    public static class MasimoRadical7Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Masimo", "Radical-7", "MasimoRadical7");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoRadical7(domainId, eventLoop);
        }
    }

    public static class SymbiqProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Hospira", "Symbiq", "Symbiq");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoSymbiq(domainId, eventLoop);
        }
    }

    public static class MultiparameterProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "Multiparameter Monitor", "Multiparameter");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new SimMultiparameter(domainId, eventLoop);
        }
    }

    public static class DraegerApolloProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "Apollo", new String[] {"DraegerApollo", "Dr\u00E4gerApollo" });
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoApollo(domainId, eventLoop);
        }
    }

    public static class DraegerEvitaXLProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "EvitaXL", new String[] { "DraegerEvitaXL", "Dr\u00E4gerEvitaXL" });
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoEvitaXL(domainId, eventLoop);

        }
    }

    public static class DraegerV500Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500", new String[] { "DraegerV500", "Dr\u00E4gerV500" });
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoV500(domainId, eventLoop);

        }
    }

    public static class DraegerV500_38400Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500", new String[] { "DraegerV500_38400", "Dr\u00E4gerV500_38400" });
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoV500_38400(domainId, eventLoop);

        }
    }

    public static class DraegerEvita4Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "Evita4", new String[] { "DraegerEvita4", "Dr\u00E4gerEvita4" });
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoEvita4(domainId, eventLoop);

        }
    }

    public static class BioPatchProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Zephyr", "BioPatch", "BioPatch");
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoBioPatch(domainId, eventLoop);

        }
    }

}
