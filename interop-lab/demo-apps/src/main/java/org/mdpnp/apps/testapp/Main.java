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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 * 
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws Exception {
        // TODO this should be external
        System.setProperty("java.net.preferIPv4Stack", "true");

        boolean cmdline = args.length > 0 ? true : false;

        Configuration runConf = Configuration.getInstance(args);

        if (null == runConf) {
            if(cmdline)
                Configuration.help(Main.class, System.out);
            return;
        }

        RtConfig.loadAndSetIceQos();

        switch (runConf.getApplication()) {
        case ICE_Device_Interface:
            if(null == runConf.getDeviceFactory()) {
                log.error("Unknown device type was specified");
                System.exit(-1);
            }
            new DeviceAdapter().start(runConf.getDeviceFactory(), runConf.getDomainId(), runConf.getAddress(), !cmdline);
            break;
        case ICE_Supervisor:
            IceAppsContainer.start(runConf.getDomainId());
            break;
        case ICE_ParticipantOnly:
            ParticipantOnly.start(runConf.getDomainId(), cmdline);
            break;
        }

        log.trace("This is the end of Main");
    }

}
