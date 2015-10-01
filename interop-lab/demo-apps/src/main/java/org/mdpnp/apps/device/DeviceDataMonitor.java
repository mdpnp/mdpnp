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
package org.mdpnp.apps.device;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import org.mdpnp.apps.fxbeans.InfusionStatusFx;
import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Tracks the data instances associated with one device.
 * 
 * @author Jeff Plourde
 *
 */
public class DeviceDataMonitor {
    private final DeviceListModel deviceListModel;
    private final NumericFxList numericList;
    private final SampleArrayFxList sampleArrayList;
    private final InfusionStatusFxList infusionStatusList;
    
    private final Device device;
    private final ObservableList<NumericFx> numeric;
    private final ObservableList<SampleArrayFx> sampleArray;
    private final ObservableList<InfusionStatusFx> infusionStatus;

    private static final Logger log = LoggerFactory.getLogger(DeviceDataMonitor.class);
    private final String udi; 

    public String getUniqueDeviceIdentifier() {
        return udi;
    }

    public DeviceDataMonitor(final String udi, final DeviceListModel deviceListModel,
            final NumericFxList numericList, final SampleArrayFxList sampleArrayList,
            final InfusionStatusFxList infusionStatusList) {
        this.udi = udi;
        this.deviceListModel = deviceListModel;
        this.device = deviceListModel.getByUniqueDeviceIdentifier(udi);
        this.numericList = numericList;
        this.sampleArrayList = sampleArrayList;
        this.infusionStatusList = infusionStatusList;
        this.numeric = new FilteredList<>(numericList, new Predicate<NumericFx>() {
            @Override
            public boolean test(NumericFx t) {
                return udi.equals(t.getUnique_device_identifier());
            }
        });
        this.sampleArray = new FilteredList<>(sampleArrayList, new Predicate<SampleArrayFx>() {
            @Override
            public boolean test(SampleArrayFx t) {
                return udi.equals(t.getUnique_device_identifier());
            }
        });
        this.infusionStatus = new FilteredList<>(infusionStatusList, new Predicate<InfusionStatusFx>() {

            @Override
            public boolean test(InfusionStatusFx t) {
                return udi.equals(t.getUnique_device_identifier());
            }
            
        });
    }

    
    public void stop() {

    }
    
    public Device getDevice() {
        return device;
    }
    
    public NumericFxList getNumericList() {
        return numericList;
    }
    
    public SampleArrayFxList getSampleArrayList() {
        return sampleArrayList;
    }
    
    public ObservableList<NumericFx> getNumericModel() {
        return numeric;
    }
    
    public ObservableList<SampleArrayFx> getSampleArrayModel() {
        return sampleArray;
    }
    
    public ObservableList<InfusionStatusFx> getInfusionStatusModel() {
        return infusionStatus;
    }

}
