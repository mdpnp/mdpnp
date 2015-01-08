package org.mdpnp.apps.testapp;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.DeviceDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DeviceFactoryTest {

    private final static Logger log = LoggerFactory.getLogger(DeviceFactoryTest.class);

    @Test
    public void testLocateDrivers()
    {
        DeviceDriverProvider.DeviceType[] all = DeviceFactory.getAvailableDevices();
        Assert.assertNotNull(all);
        Assert.assertNotEquals(all.length, 0);

        for(DeviceDriverProvider.DeviceType dt : all) {
            log.info(dt.getModel() + " " + dt.getManufacturer() + " " + dt.getAlias());
        }
    }

    @Test
    public void testBackwardsCompatibility()
    {
        DeviceDriverProvider.DeviceType[] all = DeviceFactory.getAvailableDevices();
        for(DeviceDriverProvider.DeviceType dt : all) {
            OldDeviceTypeEnum v = OldDeviceTypeEnum.valueOf(dt.getAlias());
            Assert.assertNotNull(v);
            Assert.assertEquals(v.connectionType,dt.getConnectionType());
            Assert.assertEquals(v.manufacturer,  dt.getManufacturer());
            Assert.assertEquals(v.model,         dt.getModel());
        }

        OldDeviceTypeEnum[] old = OldDeviceTypeEnum.values();
        for(OldDeviceTypeEnum dt : old) {
            DeviceDriverProvider dp = DeviceFactory.getDeviceDriverProvider(dt.name());
            Assert.assertNotNull(dp);
        }
    }


    // OLD enum for BackwardsCompatibility test copied from Configuration class
    //
    private enum OldDeviceTypeEnum {
        PO_Simulator(ice.ConnectionType.Simulated, "Simulated", "Pulse Oximeter"),
        NIBP_Simulator(ice.ConnectionType.Simulated, "Simulated", "Noninvasive Blood Pressure"),
        ECG_Simulator(ice.ConnectionType.Simulated, "Simulated", "ElectroCardioGram"),
        CO2_Simulator(ice.ConnectionType.Simulated, "Simulated", "Capnometer"),
        Temp_Simulator(ice.ConnectionType.Simulated, "Simulated", "Temperature Probe"),
        Pump_Simulator(ice.ConnectionType.Simulated, "Simulated", "Infusion Pump"),
        FlukeProsim68(ice.ConnectionType.Serial, "Fluke", "Prosim 6/8"),
        Bernoulli(ice.ConnectionType.Network, "CardioPulmonaryCorp", "Bernoulli"),
        Ivy450C(ice.ConnectionType.Serial, "Ivy", "450C Monitor"),
        Nonin(ice.ConnectionType.Serial, "Nonin", "Bluetooth Pulse Oximeter"),
        IntellivueEthernet(ice.ConnectionType.Network, "Philips", "Intellivue (LAN)"),
        IntellivueSerial(ice.ConnectionType.Serial, "Philips", "Intellivue (MIB/RS232)"),
        //Dr\u00E4gerApollo(ice.ConnectionType.Serial, "Dr\u00E4ger", "Apollo"),
        //Dr\u00E4gerEvitaXL(ice.ConnectionType.Serial, "Dr\u00E4ger", "EvitaXL"),
        //Dr\u00E4gerV500(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500"),
        //Dr\u00E4gerV500_38400(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500"),
        //Dr\u00E4gerEvita4(ice.ConnectionType.Serial, "Dr\u00E4ger", "Evita4"),
        Capnostream20(ice.ConnectionType.Serial, "Oridion", "Capnostream20"),
        NellcorN595(ice.ConnectionType.Serial, "Nellcor", "N-595"),
        MasimoRadical7(ice.ConnectionType.Serial, "Masimo", "Radical-7"),
        Symbiq(ice.ConnectionType.Simulated, "Hospira", "Symbiq"),
        Multiparameter(ice.ConnectionType.Simulated, "Simulated", "Multiparameter Monitor"),
        DraegerApollo(ice.ConnectionType.Serial, "Dr\u00E4ger", "Apollo"),
        DraegerEvitaXL(ice.ConnectionType.Serial, "Dr\u00E4ger", "EvitaXL"),
        DraegerV500(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500"),
        DraegerV500_38400(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500"),
        DraegerEvita4(ice.ConnectionType.Serial, "Dr\u00E4ger", "Evita4"),
        PB840(ice.ConnectionType.Serial, "Puritan Bennett", "840"),
        BioPatch(ice.ConnectionType.Serial, "Zephyr", "BioPatch");

        private final ice.ConnectionType connectionType;
        private final String manufacturer, model;

        private OldDeviceTypeEnum(ice.ConnectionType connectionType, String manufacturer, String model) {
            this.connectionType = connectionType;
            this.manufacturer = manufacturer;
            this.model = model;
        }

        public ice.ConnectionType getConnectionType() {
            return connectionType;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public String getModel() {
            return model;
        }

        @Override
        public String toString() {
            return manufacturer + " " + model;
        }
    }
}
