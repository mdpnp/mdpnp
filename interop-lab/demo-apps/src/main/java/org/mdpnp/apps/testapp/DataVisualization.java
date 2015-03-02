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

import java.util.concurrent.ScheduledExecutorService;

import javax.swing.*;

import org.mdpnp.apps.testapp.pca.PCAConfig;
import org.mdpnp.apps.testapp.pca.VitalModelContainer;
import org.mdpnp.apps.testapp.pca.VitalMonitoring;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class DataVisualization extends JSplitPane {
//    private final PCAConfig pcaConfig;
    private final VitalModelContainer vitalMonitoring;

    public DataVisualization(ScheduledExecutorService executor,
                             ice.InfusionObjectiveDataWriter objectiveWriter,
                             VitalModelContainer vmc) {

        super(JSplitPane.HORIZONTAL_SPLIT,
                null, // new JScrollPane(new PCAConfig(executor, objectiveWriter)),
                (JComponent)vmc);

//        this.pcaConfig = (PCAConfig) ((JScrollPane) getLeftComponent()).getViewport().getComponent(0);
        this.vitalMonitoring = (VitalModelContainer) getRightComponent();
        setDividerLocation(0.2);
        setDividerSize(10);
    }

    public void setModel(VitalModel vitalModel, InfusionStatusInstanceModel pumpModel) {
//        pcaConfig.setModel(vitalModel, pumpModel);
        vitalMonitoring.setModel(vitalModel);
    }
}
