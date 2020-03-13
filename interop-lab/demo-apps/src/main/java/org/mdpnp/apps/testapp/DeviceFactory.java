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
import org.mdpnp.devices.denver.mseries.MSeriesScale;
import org.mdpnp.devices.draeger.medibus.*;
import org.mdpnp.devices.fluke.prosim68.DemoProsim68;
import org.mdpnp.devices.ge.serial.DemoGESerial;
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
import org.mdpnp.devices.simulation.ibp.SimInvasivePressure;
import org.mdpnp.devices.simulation.multi.SimMultiparameter;
import org.mdpnp.devices.simulation.nibp.DemoSimulatedBloodPressure;
import org.mdpnp.devices.simulation.pulseox.EightSecFixedAvgSimPulseOximeter;
import org.mdpnp.devices.simulation.pulseox.FourSecFixedAvgSimPulseOximeter;
import org.mdpnp.devices.simulation.pulseox.FourSecNoSoftAvgSimPulseOximeter;
import org.mdpnp.devices.simulation.pulseox.InitialEightSecIceSettableAvgSimPulseOximeter;
import org.mdpnp.devices.simulation.pulseox.InitialEightSecOperSettableAvgSimPulseOximeter;
import org.mdpnp.devices.simulation.pulseox.SimPulseOximeter;
import org.mdpnp.devices.simulation.pump.SimControllablePump;
import org.mdpnp.devices.simulation.pump.SimInfusionPump;
import org.mdpnp.devices.simulation.temp.SimThermometer;
import org.mdpnp.devices.zephyr.biopatch.DemoBioPatch;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.support.AbstractApplicationContext;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

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
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "PO (Legacy)", "PO_Simulator", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);

            return new SimPulseOximeter(subscriber, publisher, eventLoop);
        }
    }
    
    /**
     * This pulse oximeter is "fully fixed" - the averaging time cannot be set either
     * by the operator, or through software
     * 
     * @author Simon
     *
     */
    public static class FourSecFullyFixedAvePO_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "PO (4s Fixed Average)", "4S_FF_AVG_PO_Simulator", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);

            return new FourSecFixedAvgSimPulseOximeter(subscriber, publisher, eventLoop);
        }
    }
    
    /**
     * This pulse oximeter is "fully fixed" - the averaging time cannot be set either
     * by the operator, or through software
     * 
     * @author Simon
     *
     */
    public static class FourSecFullyFixedNoSoftAvePO_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "PO (4s Fixed Average No Soft Avg)", "4S_FF_NO_SOFT_AVG_PO_Simulator", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);

            return new FourSecNoSoftAvgSimPulseOximeter(subscriber, publisher, eventLoop);
        }
    }
    
    /**
     * This pulse oximeter is "fully fixed" - the averaging time cannot be set either
     * by the operator, or through software
     * 
     * @author Simon
     *
     */
    public static class EightSecFullyFixedAvePO_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "PO (8s Fixed Average)", "8S_FF_AVG_PO_Simulator", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);

            return new EightSecFixedAvgSimPulseOximeter(subscriber, publisher, eventLoop);
        }
    }
    
    /**
     * This pulse oximeter has a software configurable averaging rate
     *  
     * @author Simon
     *
     */
    public static class EightSecIceSettableFixedAvePO_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "PO (8s ICE Settable Average)", "8S_IS_AVG_PO_Simulator", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);

            return new InitialEightSecIceSettableAvgSimPulseOximeter(subscriber, publisher, eventLoop);
        }
    }
    
    /**
     * This pulse oximeter has a software configurable averaging rate
     *  
     * @author Simon
     *
     */
    public static class EightSecOperSettableFixedAvePO_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "PO (8s Oper Settable Average)", "8S_OS_AVG_PO_Simulator", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);

            return new InitialEightSecOperSettableAvgSimPulseOximeter(subscriber, publisher, eventLoop);
        }
    }

    public static class NIBP_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "Noninvasive Blood Pressure", "NIBP_Simulator", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoSimulatedBloodPressure(subscriber, publisher, eventLoop);
        }
    }
    
    public static class IBP_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "Invasive Blood Pressure", "IBP_Simulator", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new SimInvasivePressure(subscriber, publisher, eventLoop);
        }
    }    

    public static class ECG_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "ElectroCardioGram", "ECG_Simulator", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new SimElectroCardioGram(subscriber, publisher, eventLoop);
        }
    }

    public static class CO2_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "Capnometer", "CO2_Simulator", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new SimCapnometer(subscriber, publisher, eventLoop);
        }
    }

    public static class Temp_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "Temperature Probe", "Temp_Simulator", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new SimThermometer(subscriber, publisher, eventLoop);
        }
    }

    public static class Pump_SimulatorProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "Infusion Pump", "Pump_Simulator", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new SimInfusionPump(subscriber, publisher, eventLoop);
        }
    }

    public static class FlukeProsim68Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Fluke", "Prosim 6/8" , "FlukeProsim68", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoProsim68(subscriber, publisher, eventLoop);
        }
    }

    public static class BernoulliProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Network, "CardioPulmonaryCorp", "Bernoulli", "Bernoulli", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoBernoulli(subscriber, publisher, eventLoop);
        }
    }

    public static class Ivy450CProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Ivy", "450C Monitor", "Ivy450C", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoIvy450C(subscriber, publisher, eventLoop);
        }
    }

    public static class NoninProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Nonin", "Bluetooth Pulse Oximeter", "Nonin", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoNoninPulseOx(subscriber, publisher, eventLoop);
        }
    }

    public static class IntellivueEthernetProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Network, "Philips", "Intellivue (LAN)", "IntellivueEthernet", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoEthernetIntellivue(subscriber, publisher, eventLoop);
        }
    }

    public static class IntellivueSerialProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Philips", "Intellivue (MIB/RS232)", "IntellivueSerial", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoSerialIntellivue(subscriber, publisher, eventLoop);
        }
    }

    public static class Capnostream20Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Oridion", "Capnostream20", "Capnostream20", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoCapnostream20(subscriber, publisher, eventLoop);
        }
    }


    public static class NellcorN595Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Nellcor", "N-595", "NellcorN595", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoN595(subscriber, publisher, eventLoop);
        }
    }

    public static class MasimoRadical7Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Masimo", "Radical-7", "MasimoRadical7", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoRadical7(subscriber, publisher, eventLoop);
        }
    }

    public static class SymbiqProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "Hospira", "Symbiq", "Symbiq", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoSymbiq(subscriber, publisher, eventLoop);
        }
    }

    public static class MultiparameterProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Simulated, "ICE", "Multiparameter Monitor", "Multiparameter", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new SimMultiparameter(subscriber, publisher, eventLoop);
        }
    }

    public static class DraegerApolloProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "Apollo", new String[] {"DraegerApollo", "Dr\u00E4gerApollo" }, 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoApollo(subscriber, publisher, eventLoop);
        }
    }

    public static class DraegerEvitaXLProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "EvitaXL", new String[] { "DraegerEvitaXL", "Dr\u00E4gerEvitaXL" }, 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoEvitaXL(subscriber, publisher, eventLoop);

        }
    }

    public static class DraegerV500Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500", new String[] { "DraegerV500", "Dr\u00E4gerV500" }, 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoV500(subscriber, publisher, eventLoop);

        }
    }

    public static class DraegerV500_38400Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500", new String[] { "DraegerV500_38400", "Dr\u00E4gerV500_38400" }, 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoV500_38400(subscriber, publisher, eventLoop);

        }
    }

    public static class DraegerEvita4Provider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Dr\u00E4ger", "Evita4", new String[] { "DraegerEvita4", "Dr\u00E4gerEvita4" }, 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoEvita4(subscriber, publisher, eventLoop);

        }
    }

    public static class BioPatchProvider extends SpringLoadedDriver {

        @Override
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "Zephyr", "BioPatch", "BioPatch", 1);
        }

        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = context.getBean("eventLoop", EventLoop.class);
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoBioPatch(subscriber, publisher, eventLoop);

        }
    }
    
    public static class GESerialProvider extends SpringLoadedDriver {
        public DeviceType getDeviceType(){
            return new DeviceType(ice.ConnectionType.Serial, "GE", "Dash", "GESerial", 1);
        }

        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
            EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new DemoGESerial(subscriber, publisher, eventLoop);

        }        
    }
    
    public static class MSeriesScaleProvider extends SpringLoadedDriver {

		@Override
		public DeviceType getDeviceType() {
			return new DeviceType(ice.ConnectionType.Serial,"Denver", "MSeries", "MSeries", 1);
		}

		@Override
		public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
			EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
            Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
            Publisher publisher = context.getBean("publisher", Publisher.class);
            return new MSeriesScale(subscriber, publisher, eventLoop);
		}
    	
    }

    public static class ControllablePump_SimulatorProvider extends SpringLoadedDriver {
    	
    	@Override
        public DeviceType getDeviceType() {
                return new DeviceType(ice.ConnectionType.Simulated,"ICE", "Controllable Pump", "Controllable_PUmp", 1);
        }
    	
        @Override
        public AbstractDevice newInstance(AbstractApplicationContext context) throws Exception {
                EventLoop eventLoop = (EventLoop)context.getBean("eventLoop");
                Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
                Publisher publisher = context.getBean("publisher", Publisher.class);
                return new SimControllablePump(subscriber, publisher, eventLoop);
        }
    	
    	
    }


}
