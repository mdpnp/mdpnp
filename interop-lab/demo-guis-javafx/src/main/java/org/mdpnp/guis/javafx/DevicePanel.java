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
package org.mdpnp.guis.javafx;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import org.mdpnp.rtiapi.data.DeviceDataMonitor;

public abstract class DevicePanel extends BorderPane  {

    public DevicePanel() {
        getStylesheets().add(getClass().getResource("demo-guis-javafx.css").toExternalForm());
//        super();
//        setOpaque(false);
    }

//    public DevicePanel(LayoutManager layout) {
//        super(layout);
//        setOpaque(false);
//    }

//    public static void setBackground(Container c, Color bg) {
//        for (Component comp : c.getComponents()) {
//            comp.setBackground(bg);
//            if (comp instanceof Container) {
//                setBackground((Container) comp, bg);
//            }
//        }
//    }
//
//    public static void setForeground(Container c, Color fg) {
//        for (Component comp : c.getComponents()) {
//            comp.setForeground(fg);
//            if (comp instanceof Container) {
//                setForeground((Container) comp, fg);
//            }
//        }
//    }

    protected void setInt(Integer i, Label label, String alt) {
        label.setText(null == i ? alt : Integer.toString(i));
    }

    protected void setInt(Number n, Label label, String alt) {
        setInt(null == n ? null : n.intValue(), label, alt);
    }

    protected final void setInt(ice.Numeric sample, String metric_id, Label label, String def) {
        if (sample.metric_id.equals(metric_id)) {
            setInt(sample.value, label, def);
            if (!label.isVisible()) {
                label.setVisible(true);
            }
        }
    }

//    @Override
//    public void setForeground(Color fg) {
//        super.setForeground(fg);
//        setForeground(this, fg);
//    }
//
//    @Override
//    public void setBackground(Color bg) {
//        super.setBackground(bg);
//        setBackground(this, bg);
//    }

    public void destroy() {
    }
    
    protected DeviceDataMonitor deviceMonitor;
    
    public void set(DeviceDataMonitor deviceMonitor) {
        this.deviceMonitor = deviceMonitor;
//        flowWave = new SampleArrayWaveformSource(sampleArrayReader, new ice.Sample)
    }

    protected static final BorderPane label(String label, Node c) {
        return labelTop(label, c);
    }

    protected static final BorderPane labelTop(String label, Node c) {
        BorderPane pan = new BorderPane(c);
        pan.setTop(new Label(label));
        return pan;
    }
    protected static final BorderPane labelBottom(String label, Node c) {
        BorderPane pan = new BorderPane(c);
        pan.setBottom(new Label(label));
        return pan;
    }
    protected static final BorderPane labelRight(String label, Node c) {
        BorderPane pan = new BorderPane(c);
        pan.setRight(new Label(label));
        return pan;
    }
    protected static final BorderPane labelLeft(String label, Node c) {
        BorderPane pan = new BorderPane(c);
        pan.setLeft(new Label(label));
        return pan;
    }
}
