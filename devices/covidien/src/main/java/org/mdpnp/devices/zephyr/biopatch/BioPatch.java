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
package org.mdpnp.devices.zephyr.biopatch;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;

import org.mdpnp.devices.ASCIIByte;
import org.mdpnp.devices.DeviceClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Jeff Plourde
 *
 */
public class BioPatch {

    private final InputStream in;
    private final OutputStream out;
    private final BioPatchClock deviceClock;

    private final Logger log = LoggerFactory.getLogger(BioPatch.class);

    public static final int SET_GENERAL_DATA_PACKET_TRANSMIT_STATE = 0x14;
    public static final int SET_BREATHING_PACKET_TRANSMIT_STATE = 0x15;
    public static final int SET_ECG_WAVEFORM_PACKET_TRANSMIT_STATE = 0x16;
    
    public static final int GENERAL_DATA_PACKET = 0x20;
    public static final int BREATHING_DATA_PACKET = 0x21;
    public static final int ECG_DATA_PACKET = 0x22;
    
    public static final int LIFESIGN = 0x23;
    public static final int GET_SERIAL_NUMBER = 0x0B;
    
    public BioPatch(DeviceClock referenceClock, InputStream in, OutputStream out) {
        this.deviceClock = new BioPatchClock(referenceClock);
        this.in = in;
        this.out = new BufferedOutputStream(out);
    }

    protected void receiveGeneralDataPacket(DeviceClock.Reading timeofday, int sequenceNumber, Integer heartrate, Float respirationRate, Float skinTemperature) {

    }
    
    protected void receiveGetSerialNumber(String s) {
        
    }
    
    protected void receiveGetSerialNumber(ByteBuffer buffer, int bytes) throws UnsupportedEncodingException {
        receiveGetSerialNumber(new String(buffer.array(), buffer.position(), bytes, "ASCII"));
    }
    
    protected void receiveBreathingDataPacket(ByteBuffer buffer) {
        
    }
    
    
    protected void receiveECGDataPacket(DeviceClock.Reading timeofday, Number[] values) {
        
    }
    
    protected void receiveECGDataPacket(ByteBuffer buffer, int bytes) {
        int sequenceNumber = buffer.get();
        bytes--;
        DeviceClock.Reading sampleTime = deviceClock.instant(buffer);
        bytes-=8;

        // meant to be 63 samples in 79 bytes.. unpacking 10-bit samples
        Number[] values = new Number[63];
        int anchor = buffer.position();
        
        if(bytes<79) {
            log.warn("Insufficient bytes for packed ECG:" + bytes);
            return;
        }
        
        
        for(int i = 0; i < 63; i++) {
            int pkg = i / 4;
            
            switch(i%4) {
            case 0:
                
                values[i] = (0xFF & buffer.get(anchor + 5*pkg)) + (0x300&((int)buffer.get(anchor+5*pkg+1)<<8));
                break;
            case 1:
                values[i] = (0x3F & (buffer.get(anchor + 5*pkg+1)>>2)) + (0x3C0&((int)buffer.get(anchor+5*pkg+2)<<6));
                
                break;
            case 2:
                values[i] = (0x0F & (buffer.get(anchor + 5*pkg+2)>>4)) + (0x3F0 &((int)buffer.get(anchor+5*pkg+3)<<4));
                
                break;
            case 3:
                values[i] = (0x03 & (buffer.get(anchor + 5*pkg+3)>>6)) + (0x3FC &((int)buffer.get(anchor+5*pkg+4)<<2));
//                System.err.println(HexUtil.dump(ByteBuffer.wrap(buffer.array(), anchor+5*pkg+3, 2))+" => "+values[i]);
                break;
            }
        }

        receiveECGDataPacket(sampleTime, values);
//        log.warn(Arrays.toString(values));
    }
    

    
    protected void receiveGeneralDataPacket(ByteBuffer buffer) {
        int sequenceNumber = buffer.get();
        DeviceClock.Reading sampleTime = deviceClock.instant(buffer);

        int heartRate = 0xFFFF & buffer.getShort();
        int respirationRate = 0xFFFF & buffer.getShort();
        int skinTemp = buffer.getShort();
        
        receiveGeneralDataPacket(
                sampleTime,
                sequenceNumber,
                65535==heartRate?null:heartRate, 
                65535==respirationRate?null:(respirationRate/10.0f), 
                -32768==skinTemp?null:(skinTemp/10.0f));
    }
    
    private final void consume(ByteBuffer buffer, short messageId, short bytes) throws IOException {
//        log.warn("messageId=0x"+Integer.toHexString(messageId)+" bytes="+bytes);
        int newPosition = buffer.position() + bytes;
        switch(messageId) {
        case GENERAL_DATA_PACKET:
            receiveGeneralDataPacket(buffer);
            break;
        case GET_SERIAL_NUMBER:
            receiveGetSerialNumber(buffer, bytes);
            break;
        case ECG_DATA_PACKET:
            receiveECGDataPacket(buffer, bytes);
            break;
        case BREATHING_DATA_PACKET:
            receiveBreathingDataPacket(buffer);
            break;
        }
        
        buffer.position(newPosition);
        short crc = (short) (0xFF & buffer.get());
        short etx = (short) (0xFF & buffer.get());
    }

    private final void consume(ByteBuffer buffer) throws IOException {
        try {
            // In case of error or incomplete message
            buffer.mark();
            
            short messageId, bytes;
            
            while(buffer.remaining() >= 5) {
                switch(0xFF & buffer.get()) {
                case ASCIIByte.STX:
                    messageId = (short) (0xFF & buffer.get());
                    bytes = (short) (0xFF & buffer.get());
                    if(buffer.remaining()>=(bytes+2)) {
                        consume(buffer, messageId, bytes);
                        buffer.mark();
                    }
                    break;
                    
                }
            }
            
            
        } finally {
            buffer.reset();
        }
    }

    private final byte[] _buffer = new byte[65536];
    private final ByteBuffer buffer = ByteBuffer.wrap(_buffer).order(ByteOrder.LITTLE_ENDIAN);
    private final byte[] xmitBuffer = new byte[4096];


    protected final static int crcByte(int crc, int b) {
        crc = crc ^ b;
        for(int i = 0; i < 8; i++) {
            if(0 != (crc & 1)) {
                crc = (crc>>1)^0x8C;
            } else {
                crc >>= 1;
            }
        }
        return crc;
    }
    
    protected final static int crcBlock(int crc, byte[] bytes, int off, int len) {
        for(int i = 0; i < len; i++) {
            crc = crcByte(crc, 0xFF & bytes[off+i]);
        }
        return crc;
    }
    
    private final byte[] EMPTY = new byte[0];
    
    // synchro only for now
    protected synchronized void send(int messageId, byte [] payload, int off, int len) throws IOException {
        out.write(ASCIIByte.STX);
        out.write(messageId);
        out.write(len);
        out.write(payload, off, len);
        out.write(crcBlock(0, payload, off, len));
        out.write(ASCIIByte.ETX);
        out.flush();
    }
    
    protected void sendGetSerialNumber() throws IOException {
        send(GET_SERIAL_NUMBER, xmitBuffer, 0, 0);
    }
    
    protected void sendSetBoolean(int message_id, boolean on) throws IOException {
        xmitBuffer[0] = (byte)(on?1:0);
        send(message_id, xmitBuffer, 0, 1);
    }
    
    protected void sendSetBreathingPacketTransmitState(boolean on) throws IOException {
        sendSetBoolean(SET_BREATHING_PACKET_TRANSMIT_STATE, on);
    }
    
    protected void sendSetECGWaveformPacketTransmitState(boolean on) throws IOException {
        sendSetBoolean(SET_ECG_WAVEFORM_PACKET_TRANSMIT_STATE, on);
    }
    
    protected void sendSetGeneralDataPacketTransmitState(boolean on) throws IOException {
        sendSetBoolean(SET_GENERAL_DATA_PACKET_TRANSMIT_STATE, on);
    }
    
    protected void sendLifesign() throws IOException {
        send(LIFESIGN, EMPTY, 0, 0);
    }
    
    protected boolean receive() throws IOException {
        int b = in.read(buffer.array(), buffer.position(), buffer.remaining());
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

    static class BioPatchClock implements DeviceClock {

        private final DeviceClock ref;

        private final Calendar calendar = Calendar.getInstance();

        BioPatchClock(DeviceClock ref) {
            this.ref = ref;

        }

        @Override
        public Reading instant() {
            return ref.instant();
        }

        // The device has no timezone setting ... so we're getting milliseconds since the epoch in local time
        // which is an unusual value to get... and hence we need to do something unusual to cope with it

        public Reading instant(ByteBuffer buffer) {
            long currentTime = timeFromTimestamp(calendar, buffer);
            Reading deviceTime = new DeviceClock.ReadingImpl(currentTime);
            return new CombinedReading(instant(), deviceTime);
        }

        static synchronized long timeFromTimestamp(Calendar calendar, ByteBuffer buffer) {
            int year = buffer.getShort();
            int month = buffer.get();
            int day = buffer.get();
            int millisecond = buffer.getInt();

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, millisecond / 3600000);
            millisecond %= 3600000;
            calendar.set(Calendar.MINUTE, millisecond / 60000);
            millisecond %= 60000;
            calendar.set(Calendar.SECOND, millisecond / 1000);
            millisecond %= 1000;
            calendar.set(Calendar.MILLISECOND, millisecond);
            return calendar.getTimeInMillis();
        }

    }

}
