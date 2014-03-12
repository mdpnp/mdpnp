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

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

/**
 * @author Jeff Plourde
 *
 */
public enum Language implements OrdinalEnum.ShortType {
    Unspecified(0), English(1), German(2), French(3), Italian(4), Spanish(5), Dutch(6), Swedish(7), Finnish(8), Norwegian(9), Danish(10), Japanese(11), RepublicOfChina(
            12), PeoplesRepublicOfChina(13), Portuguese(14), Russian(15), Byelorussian(16), Ukrainian(17), Croatian(18), Serbian(19), Macedonian(20), Bulgarian(
            21), Greek(22), Polish(23), Czech(24), Slovak(25), Slovenian(26), Hungarian(27), Romanian(28), Turkish(29), Latvian(30), Lithuanian(31), Estonian(
            32), Korean(33);

    private final short x;

    private Language(int x) {
        this((short) x);
    }

    private Language(short x) {
        this.x = x;
    }

    private static final Map<Short, Language> map = OrdinalEnum.buildShort(Language.class);

    public short asShort() {
        return x;
    }

    public static Language valueOf(int x) {
        return valueOf((short) x);
    }

    public static Language valueOf(short x) {
        return map.get(x);
    }

}
