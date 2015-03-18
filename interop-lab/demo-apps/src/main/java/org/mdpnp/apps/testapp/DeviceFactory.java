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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.TreeSet;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.cpc.bernoulli.DemoBernoulli;
import org.mdpnp.devices.draeger.medibus.DemoApollo;
import org.mdpnp.devices.draeger.medibus.DemoEvita4;
import org.mdpnp.devices.draeger.medibus.DemoEvitaXL;
import org.mdpnp.devices.draeger.medibus.DemoV500;
import org.mdpnp.devices.draeger.medibus.DemoV500_38400;
import org.mdpnp.devices.fluke.prosim68.DemoProsim68;
import org.mdpnp.devices.ge.serial.DemoGESerial;
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
import org.springframework.context.ApplicationContext;


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

    public static class PO_SimulatorProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "Pulse Oximeter", "PO_Simulator");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new SimPulseOximeter(domainId, eventLoop);
        }
    }

    public static class NIBP_SimulatorProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "Noninvasive Blood Pressure", "NIBP_Simulator");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoSimulatedBloodPressure(domainId, eventLoop);
        }
    }

    public static class ECG_SimulatorProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "ElectroCardioGram", "ECG_Simulator");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new SimElectroCardioGram(domainId, eventLoop);
        }
    }

    public static class CO2_SimulatorProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "Capnometer", "CO2_Simulator");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new SimCapnometer(domainId, eventLoop);
        }
    }

    public static class Temp_SimulatorProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "Temperature Probe", "Temp_Simulator");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new SimThermometer(domainId, eventLoop);
        }
    }

    public static class Pump_SimulatorProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "Infusion Pump", "Pump_Simulator");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new SimInfusionPump(domainId, eventLoop);
        }
    }

    public static class FlukeProsim68Provider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Fluke", "Prosim 6/8" , "FlukeProsim68");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoProsim68(domainId, eventLoop);
        }
    }

    public static class BernoulliProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Network, "CardioPulmonaryCorp", "Bernoulli", "Bernoulli");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoBernoulli(domainId, eventLoop);
        }
    }

    public static class Ivy450CProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Ivy", "450C Monitor", "Ivy450C");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoIvy450C(domainId, eventLoop);
        }
    }

    public static class NoninProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Nonin", "Bluetooth Pulse Oximeter", "Nonin");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoNoninPulseOx(domainId, eventLoop);
        }
    }

    public static class IntellivueEthernetProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Network, "Philips", "Intellivue (LAN)", "IntellivueEthernet");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoSerialIntellivue(domainId, eventLoop);
        }
    }

    public static class IntellivueSerialProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Philips", "Intellivue (MIB/RS232)", "IntellivueSerial");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoSerialIntellivue(domainId, eventLoop);
        }
    }

    public static class Capnostream20Provider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Oridion", "Capnostream20", "Capnostream20");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoCapnostream20(domainId, eventLoop);
        }
    }


    public static class NellcorN595Provider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Nellcor", "N-595", "NellcorN595");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoN595(domainId, eventLoop);
        }
    }

    public static class MasimoRadical7Provider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Masimo", "Radical-7", "MasimoRadical7");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoRadical7(domainId, eventLoop);
        }
    }

    public static class SymbiqProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Hospira", "Symbiq", "Symbiq");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoSymbiq(domainId, eventLoop);
        }
    }

    public static class MultiparameterProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Simulated", "Multiparameter Monitor", "Multiparameter");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new SimMultiparameter(domainId, eventLoop);
        }
    }

    public static class DraegerApolloProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "Apollo", new String[] {"DraegerApollo", "Dr\u00E4gerApollo" });
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoApollo(domainId, eventLoop);
        }
    }

    public static class DraegerEvitaXLProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "EvitaXL", new String[] { "DraegerEvitaXL", "Dr\u00E4gerEvitaXL" });
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoEvitaXL(domainId, eventLoop);

        }
    }

    public static class DraegerV500Provider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500", new String[] { "DraegerV500", "Dr\u00E4gerV500" });
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoV500(domainId, eventLoop);

        }
    }

    public static class DraegerV500_38400Provider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500", new String[] { "DraegerV500_38400", "Dr\u00E4gerV500_38400" });
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoV500_38400(domainId, eventLoop);

        }
    }

    public static class DraegerEvita4Provider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "Evita4", new String[] { "DraegerEvita4", "Dr\u00E4gerEvita4" });
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoEvita4(domainId, eventLoop);

        }
    }

    public static class BioPatchProvider implements DeviceDriverProvider {

        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Zephyr", "BioPatch", "BioPatch");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoBioPatch(domainId, eventLoop);

        }
    }
    
    public static class GESerialProvider implements DeviceDriverProvider {
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "GE", "Dash", "GESerial");
        }

        public AbstractDevice create(ApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            int domainId = (Integer)context.getBean("domainId");
            return new DemoGESerial(domainId, eventLoop);

        }        
    }

}
