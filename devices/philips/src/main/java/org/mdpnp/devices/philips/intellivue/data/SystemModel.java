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
package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

/**
 * @author Jeff Plourde
 *
 */
public class SystemModel implements Value {
    private final VariableLabel manufacturer = new VariableLabel();
    private final VariableLabel modelNumber = new VariableLabel();

    public VariableLabel getManufacturer() {
        return manufacturer;
    }

    public VariableLabel getModelNumber() {
        return modelNumber;
    }

    public void setManufacturer(java.lang.String manufacturer) {
        this.manufacturer.setString(manufacturer);
    }

    public void setModelNumber(java.lang.String modelNumber) {
        this.modelNumber.setString(modelNumber);
    }

    @Override
    public java.lang.String toString() {
        return "[manufacturer=" + manufacturer + ",modelNumber=" + modelNumber + "]";
    }

    @Override
    public void format(ByteBuffer bb) {
        manufacturer.format(bb);
        modelNumber.format(bb);
    }

    @Override
    public void parse(ByteBuffer bb) {
        manufacturer.parse(bb);
        modelNumber.parse(bb);
    }
}
