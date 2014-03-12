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
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.philips.intellivue.util.Util;

/**
 * @author Jeff Plourde
 *
 */
public class TextIdList implements Value {

    private final List<TextId> list = new ArrayList<TextId>();

    public boolean contains(long textId) {
        for (TextId tid : list) {
            if (textId == tid.getTextId()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(Label[] labels) {
        for (Label l : labels) {
            if (!contains(l.asLong())) {
                return false;
            }
        }
        return true;
    }

    public List<TextId> getList() {
        return list;
    }

    public void addTextId(long textId) {
        TextId tid = new TextId();
        tid.setTextId(textId);
        list.add(tid);
    }

    @Override
    public void format(ByteBuffer bb) {
        Util.PrefixLengthShort.write(bb, list);
    }

    @Override
    public void parse(ByteBuffer bb) {
        Util.PrefixLengthShort.read(bb, list, true, TextId.class);
    }

    @Override
    public java.lang.String toString() {
        return list.toString();
    }

}
