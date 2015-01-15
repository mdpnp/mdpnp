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
package org.mdpnp.devices.ge.serial;

import ice.ConnectionState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.FlowControl;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class DemoGESerial extends AbstractDelegatingSerialDevice<GESerial> {

    private static final Logger log = LoggerFactory.getLogger(DemoGESerial.class);

    public DemoGESerial(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop, GESerial.class);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "GE";
        deviceIdentity.model = "Serial Device";
        writeDeviceIdentity();
    }


    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState, String transitionNote) {
        super.stateChanged(newState, oldState, transitionNote);
        if (ConnectionState.Connected.equals(oldState) && !ConnectionState.Connected.equals(newState)) {

        }
        
        if (ice.ConnectionState.Connected.equals(newState) && !ice.ConnectionState.Connected.equals(oldState)) {
            startRequestParameters();
        }
        if (!ice.ConnectionState.Connected.equals(newState) && ice.ConnectionState.Connected.equals(oldState)) {
            stopRequestParameters();
        }

    }

    private class MyGESerial extends GESerial {

        public MyGESerial(InputStream in, OutputStream out) {
            super(in, out);
        }
        

        String numericParamLookup(int partype, int parcode, int index) {
            switch(partype) {
            case 8:
                switch(parcode) {
                    case 34:
                        switch(index) {
                            case 0:
                                return rosetta.MDC_TTHOR_RESP_RATE.VALUE;
                            default:
                                break;
                        }
                    default:
                        break;
                }
                break;
            case 1:
            case 45:
                switch(index) {
                    case 0:
                        return rosetta.MDC_ECG_HEART_RATE.VALUE;
                    default:
                        break;
                }
                break;
            case 2:
                switch(parcode) {
                    case 77:
                        switch(index) {
                            case 0:
                                return rosetta.MDC_PRESS_BLD_MEAN.VALUE;
                            case 1:
                                return rosetta.MDC_PRESS_BLD_SYS.VALUE;
                            case 2:
                                return rosetta.MDC_PRESS_BLD_DIA.VALUE;
                        }
                    break;
                }
                break;
            case 11:
            case 28:
            case 43:
            case 44:
                switch(parcode) {
                    case 45:
                        switch(index) {
                            case 0:
                                return rosetta.MDC_PULS_OXIM_SAT_O2.VALUE;
                            case 1:
                                return rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE;
                            default:
                                break;
                        }
                        break;
                }
                
                break;
            case 10:
            case 57:
                switch(index) {
                    case 0:
                        return rosetta.MDC_PRESS_CUFF_MEAN.VALUE;
                    case 1:
                        return rosetta.MDC_PRESS_CUFF_SYS.VALUE;
                    case 2:
                        return rosetta.MDC_PRESS_CUFF_DIA.VALUE;
                }
            case 64:
                switch(parcode) {
                    case 132:
                        break;
//                        switch(index) {
//                            case 0:
//                                if(NULL != value && *value != SHRT_MIN) {
//                                   *value = 0xFF & *value;
//                                }
//
//                                return rosetta.MDC_EEG_SIGNAL_QUALITY_INDEX;
//                            case 1:
//                                if(NULL != value && *value != SHRT_MIN) {
//                                   *value = 0xFF & (*value >> 8);
//                                }
//                                return rosetta.MDC_EEG_BISPECTRAL_INDEX;
//                            case 2:
//                                if(NULL != value && *value != SHRT_MIN) {
//                                   *value = 0xFF & (*value >> 8);
//                                }
//                                return rosetta.MDC_EMG_ELEC_POTL_MUSCL.VALUE;
//                            default:
//                                break;
//                        }
                    default:
                        break;
                }
            default:
                break;
            }
            return "GE_TYPE_"+partype+"_CODE_"+parcode+"_IDX_"+index;
        }
        
        @Override
        protected void receiveNumeric(int partype, int parcode, int index, Short value) {
            reportConnected("data received");
            int param = ((partype << 16) & 0x00FF0000) | ((parcode << 8) & 0x0000FF00) | (index & 0x000000FF);
            String metric_id = numericParamLookup(partype, parcode, index);
            if(null != metric_id) {
//                System.err.println("Updating " + metric_id + " to " + value);
                numerics.put(param, numericSample(numerics.get(param), null==value?null:(int)value, metric_id, rosetta.MDC_DIM_DIMLESS.VALUE, null));
            }
        }
    }

    
    protected Map<Integer, InstanceHolder<ice.Numeric>> numerics = new HashMap<Integer, InstanceHolder<ice.Numeric>>();
    
    @Override
    protected MyGESerial buildDelegate(int idx, InputStream in, OutputStream out) {
        return new MyGESerial(in, out);
    }

    
    @Override
    public void doInitCommands(int idx) throws IOException {
        super.doInitCommands(idx);
        getDelegate().sendRequestParameters();
        // come up with something later ... request id or whatever
        // For now we will report connected when data arrives 
    }

    @Override
    protected boolean delegateReceive(int idx, GESerial delegate) throws IOException {
        return delegate.receive();
    }

    @Override
    protected long getConnectInterval(int idx) {
        return 500L;
    }

    @Override
    protected long getNegotiateInterval(int idx) {
        return 2500L;
    }

    @Override
    protected long getMaximumQuietTime(int idx) {
        return 3000L;
    }

    @Override
    protected String iconResourceName() {
        return "dash.png";
    }

    @Override
    public SerialProvider getSerialProvider(int idx) {
        SerialProvider serialProvider = super.getSerialProvider(idx);
        serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One, FlowControl.None);
        return serialProvider;
    }
    
    private class RequestParameters implements Runnable {
        public void run() {
            if (ice.ConnectionState.Connected.equals(getState())) {
                try {
                    getDelegate().sendRequestParameters();
                } catch (IOException e) {
                    log.warn("Error sending request parameters", e);
                }
            }
        }
    }
    
    private ScheduledFuture<?> requestParameters;

    private synchronized void stopRequestParameters() {
        if (null != requestParameters) {
            requestParameters.cancel(false);
            requestParameters = null;
            log.trace("Canceled request parameters");
        } else {
            log.trace("request parameters canceled");
        }
    }

    private synchronized void startRequestParameters() {
        if (null == requestParameters) {
            requestParameters = executor.scheduleWithFixedDelay(new RequestParameters(), 2000L, 2000L, TimeUnit.MILLISECONDS);
            log.trace("Scheduled request parameters");
        } else {
            log.trace("request parameters request already scheduled");
        }
    }
}
