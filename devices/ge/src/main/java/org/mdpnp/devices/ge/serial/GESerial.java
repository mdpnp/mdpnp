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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.io.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class GESerial {

    private final InputStream in;
    private final OutputStream out;
    private final Logger log = LoggerFactory.getLogger(GESerial.class);
    
    private static final short INVALID = -32768;
    private static final short MISSING = -32767;
    private static final short PAR_DRAW = -32766;
    private static final short PAR_FLUSH = -32765;
    private static final short PAR_ZERO = -32764;
    private static final short PAR_CAL = -32763;
    private static final short NO_BP_PULSE = -32762;
    private static final short SENSOR_FAIL = -32761;
    private static final byte INVALID_BYTE = -128;
    private static final byte MISSING_BYTE = -127;
    
    private static final byte ALARM_ACTIVE = 0;
    private static final byte ALARM_SILENCE = 1;
    private static final byte ALARM_PAUSE = 2;
    private static final byte ALARM_OFF = 3;
    private static final byte ALARM_VOLUME_OFF = 4;
    private static final byte ALARM_PAUSE_DISP_OFF = 5;
    
    private static final byte ALARM_LEVEL_STATUS_ONLY = 0;
    private static final byte ALARM_LEVEL_SYSTEM_MESSAGE = 1;
    private static final byte ALARM_LEVEL_SYSTEM_ADVISORY = 2;
    private static final byte ALARM_LEVEL_SYSTEM_WARNING = 3;
    private static final byte ALARM_LEVEL_MESSAGE = 4;
    private static final byte ALARM_LEVEL_ADVISORY = 5;
    private static final byte ALARM_LEVEL_WARNING = 6;
    private static final byte ALARM_LEVEL_CRISES = 7;

    // use network byte order
    private static final byte[] request_packet = new byte[] {
        0x40,  0x0,  0x0,  0x0, 0x0, 0x0, // dst_addr 6
         0x0,  0x0,  0x0,  0x0, 0x0, 0x0,// src_addr, 12
        0x00, (byte) 202, // fun_code 14
        0x00, 35, // sub_code subfunction code 16
        0x00, 0x00, // version version of bed_msg 18
        0x00, 0x00, // seq_num response sequence number 20
        0x00, 0x00, // req_res request response flag 22
        0x00, 0x00, // proc_id requestors process id 24 
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, // origin location name 56
        0x00, 0x00, // return status 58 
        0x00, 0x00, // data count 60
        0,0,0,0,0,0,0,0,0,0, // 70
        0,0,0,0,0,0,0,0,0,0, // 80
        0,0,0,0,0,0,0,0,0,0, //90
        0,0,0,0,0,0,0,0,0,0, // 100
        0,0,0,0 //104
        // IS THERE A CRC here?
    };
    
    public GESerial(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void sendRequestParameters() throws IOException {
        out.write(request_packet);
//        System.err.println("Wrote " + request_packet.length + " bytes");
        out.flush();
    }
    
    private final byte[] _buffer = new byte[65536];
    private final ByteBuffer buffer = ByteBuffer.wrap(_buffer).order(ByteOrder.BIG_ENDIAN);
    
    
    protected boolean receive() throws IOException {
        int b = in.read(buffer.array(), buffer.position(), buffer.remaining());
//        System.err.println("At " + System.currentTimeMillis() + " received " + b + " bytes");
        if(b < 0) {
            return false;
        } else {
            buffer.position(buffer.position()+b);
            
        }
        
        try {
            buffer.flip();
            if(buffer.hasRemaining()) {
//                log.warn("Data:"+HexUtil.dump(buffer));
            }
            consume(buffer);
        } finally {
            buffer.compact();
        }

        return true;
    }
    
   
    protected void updateParameter(DeviceClock.Reading sampleTime, int partype, int parcode, int index, short value) {
        switch(value) {
        case INVALID:
        case MISSING:
        case PAR_DRAW:
        case PAR_FLUSH:
        case PAR_ZERO:
        case PAR_CAL:
        case NO_BP_PULSE:
        case SENSOR_FAIL:
            // For now we're not differentiating
            receiveNumeric(sampleTime, partype, parcode, index, null);
            break;
        default:
            receiveNumeric(sampleTime, partype, parcode, index, value);
            break;
        }
    }
    
    protected void receiveNumeric(DeviceClock.Reading sampleTime, int partype, int parcode, int index, Short value) {
        
    }
    
    private final byte[] dst_addr = new byte[6];
    private final byte[] src_addr = new byte[6];
    private final byte[] oln = new byte[32];
    
    
    private final DeviceClock deviceClock = new DeviceClock.WallClock();
    
    @SuppressWarnings("unused")
    private final void consume(ByteBuffer buffer) throws IOException {
        try {
//            System.err.println(buffer.remaining() + " bytes in " + HexUtil.dump(buffer));
            
            DeviceClock.Reading sampleTime = deviceClock.instant();
            
            // In case of error or incomplete message
            buffer.mark();
            
            if(buffer.remaining()<66) {
//                System.err.println("Insufficient bytes for bedmessage" + buffer.remaining());
                return;
            }
            
            buffer.get(dst_addr);
            buffer.get(src_addr);
            
            // Malhereusement this protocol is not framed so like what to do, eh?
            // I guess just ditch if the message isn't entirely there
            short fun_code = buffer.getShort(); // so much fun . .. code
            short sub_code = buffer.getShort(); // sub_code
            short version = buffer.getShort();
            short seq_num = buffer.getShort();
            short req_res = buffer.getShort();
            short proc_id = buffer.getShort();
            buffer.get(oln);
            short return_status = buffer.getShort();
            short data_count = buffer.getShort();
            
            if(buffer.remaining() < (data_count+2)) {
                return;
            }
            
            if(fun_code != 201 || sub_code != 20) {
//                System.err.println("Unknown message type " + fun_code + " " + sub_code);
                buffer.position(buffer.position()+data_count+2);
                return;
            }
            
//            if(buffer.remaining()<6*data_count) {
//                System.err.println("Insufficient bytes for data " + buffer.remaining());
//                return;
//            }
            
//            for(short i = 0; i < data_count; i++) {
                byte alarm_state = buffer.get();
                byte alarm_level = buffer.get();
                byte audio_alarm_level = buffer.get();
                byte patient_admission = buffer.get();
                byte number_of_parameters = buffer.get();
                byte graph_status_msg = buffer.get();
                
                if(buffer.remaining()<(number_of_parameters*67)) {
//                    System.err.println("Insufficient bytes for parameter " + buffer.remaining());
                    return;
                }
                
                for(byte j = 0; j < number_of_parameters; j++) {
                    // struct PAR_UPD
                    byte upar_func_code = buffer.get();
                    byte uparcode = buffer.get();
                    int upar_status = 0xFFFF & buffer.getShort();
                    short upar_val0 = buffer.getShort();
                    short upar_val1 = buffer.getShort();
                    short upar_val2 = buffer.getShort();
                    // struct EXTENDED_PAR_UPD
                    byte epar_func_code = buffer.get();
                    byte eparcode = buffer.get();
                    short epar_val0 = buffer.getShort();
                    short epar_val1 = buffer.getShort();
                    short epar_val2 = buffer.getShort();
                    short epar_val3 = buffer.getShort();
                    short epar_val4 = buffer.getShort();
                    short epar_val5 = buffer.getShort();
                    // struct SETUP_N_LIM
                    byte spar_func_code = buffer.get();
                    byte sparcode = buffer.get();
                    short flag0 = buffer.getShort();
                    short flag1 = buffer.getShort();
                    short lo_limit0 = buffer.getShort();
                    short hi_limit0 = buffer.getShort();
                    short lo_limit1 = buffer.getShort();
                    short hi_limit1 = buffer.getShort();
                    short lo_limit2 = buffer.getShort();
                    short hi_limit2 = buffer.getShort();
                    short extra_limit = buffer.getShort();
                    // struct PAR_MSSG_S
                    byte mpar_func_code = buffer.get();
                    byte mparcode = buffer.get();
                    byte attribute0 = buffer.get();
                    byte msg_index0 = buffer.get();
                    byte attribute1 = buffer.get();
                    byte msg_index1 = buffer.get();
                    byte attribute2 = buffer.get();
                    byte msg_index2 = buffer.get();
                    int value = 0xFFFF & buffer.getShort();
                    // struct MORE_SETUP
                    byte opar_func_code = buffer.get();
                    byte oparcode = buffer.get();
                    short val0 = buffer.getShort();
                    short val1 = buffer.getShort();
                    short val2 = buffer.getShort();
                    short val3 = buffer.getShort();
                    
                    
                    // 
                    byte par_type = buffer.get();
                    byte parcode = buffer.get();
                    byte pos = buffer.get();
                    byte acq_port = buffer.get();
                    
                    
                    // Process this here.
                    updateParameter(sampleTime, par_type, parcode, 0, upar_val0);
                    updateParameter(sampleTime, par_type, parcode, 1, upar_val1);
                    updateParameter(sampleTime, par_type, parcode, 2, upar_val2);
                    
                }
                short crc = buffer.getShort();
//            }
            
            buffer.mark();
        } finally {
            buffer.reset();
        }
    }    
}
