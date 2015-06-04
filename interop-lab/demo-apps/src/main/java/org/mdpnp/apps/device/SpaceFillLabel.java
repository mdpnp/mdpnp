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
package org.mdpnp.apps.device;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * @author Jeff Plourde
 *
 */
public class SpaceFillLabel extends JLabel {
    public SpaceFillLabel() {
        this("");
    }

    public SpaceFillLabel(String text) {
        super(text);
        enableEvents(ComponentEvent.COMPONENT_EVENT_MASK);
    }

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        super.processComponentEvent(e);
        switch (e.getID()) {

        case ComponentEvent.COMPONENT_RESIZED:
            resizeFontToFill(this);
            break;
        }
    }

    @Override
    public void setText(String text) {
        String existingText = getText();
        boolean b = text != null && existingText != null && !text.equals(existingText);
        super.setText(text);
        if (b) {
            resizeFontToFill(this);
        }
    }

    protected static float maxFontSize(JLabel label) {
        Font labelFont = label.getFont();
        if (null == labelFont) {
            return -1.0f;
        }
        FontMetrics fontMetrics = label.getFontMetrics(labelFont);
        if (fontMetrics == null) {
            return -1.0f;
        }

        String labelText = label.getText();

        int stringWidth = fontMetrics.stringWidth(labelText);
        int stringHeight = fontMetrics.getHeight();
        int componentWidth = label.getWidth();
        int componentHeight = label.getHeight();

        // Find out how much the font can grow in width.
        double widthRatio = (double) componentWidth / (double) stringWidth;
        double heightRatio = 1.0 * componentHeight / stringHeight;

        double smallerRatio = Math.min(widthRatio, heightRatio) - 0.1f;

        return (float) (labelFont.getSize2D() * smallerRatio);
    }

    public static ComponentListener attachResizeFontToFill(JComponent component, final JLabel... label) {
        ComponentListener cl;
        component.addComponentListener(cl = new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                resizeFontToFill(label);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }

        });
        return cl;
    }

    public static void resizeFontToFill(JLabel... label) {
        float fontSize = Float.MAX_VALUE;

        for (JLabel l : label) {
            fontSize = Math.min(fontSize, maxFontSize(l));
        }
        if (fontSize > 0.0f) {

            for (JLabel l : label) {
                Font f = l.getFont();
                if (f != null) {
                    l.setFont(f.deriveFont(fontSize));
                }
            }
        }
    }
}
