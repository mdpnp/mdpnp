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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;

import org.mdpnp.devices.ASCIIByte;
import org.mdpnp.devices.io.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class BioPatch {

    private final InputStream in;
    private final OutputStream out;
    private final Logger log = LoggerFactory.getLogger(BioPatch.class);

    public static final int SET_GENERAL_DATA_PACKET_TRANSMIT_STATE = 0x14;
    public static final int GENERAL_DATA_PACKET = 0x20;
    
    public BioPatch(InputStream in, OutputStream out) {
        this.in = in;
        this.out = new BufferedOutputStream(out);
    }

    protected void receiveGeneralDataPacket(int sequenceNumber, long timeofday, Integer heartrate, Float respirationRate, Float skinTemperature) {
        
    }
    
    protected void receiveGeneralDataPacket(ByteBuffer buffer) {
        int sequenceNumber = buffer.get();
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
        
        int heartRate = 0xFFFF & buffer.getShort();
        int respirationRate = 0xFFFF & buffer.getShort();
        int skinTemp = buffer.getShort();
        
        receiveGeneralDataPacket(
                sequenceNumber, 
                calendar.getTimeInMillis(), 
                65535==heartRate?null:heartRate, 
                65535==respirationRate?null:(respirationRate/10.0f), 
                -32768==skinTemp?null:(skinTemp/10.0f));
    }
    
    private final void consume(ByteBuffer buffer, short messageId, short bytes) throws IOException {
        log.warn("messageId=0x"+Integer.toHexString(messageId)+" bytes="+bytes);
        int newPosition = buffer.position() + bytes;
        switch(messageId) {
        case GENERAL_DATA_PACKET:
            receiveGeneralDataPacket(buffer);
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
    private final Calendar calendar = Calendar.getInstance();

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
    
    protected void send(int messageId, byte [] payload, int off, int len) throws IOException {
        out.write(ASCIIByte.STX);
        out.write(messageId);
        out.write(len);
        out.write(payload, off, len);
        out.write(crcBlock(0, payload, off, len));
        out.write(ASCIIByte.ETX);
        out.flush();
    }
    
    protected void sendSetGeneralDataPacketTransmitState(boolean on) throws IOException {
        xmitBuffer[0] = (byte)(on?1:0);
        send(SET_GENERAL_DATA_PACKET_TRANSMIT_STATE, xmitBuffer, 0, 1);
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
            log.warn("Data:"+HexUtil.dump(buffer));
            consume(buffer);
        } finally {
            buffer.compact();
        }
        

        return true;
    }
}
