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
package org.mdpnp.guis.waveform.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledFuture;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformRenderer;
import org.mdpnp.guis.waveform.WaveformSource;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class SwingWaveformPanel extends JComponent implements WaveformPanel, SwingAnimatable {
    private final WaveformRenderer renderer = new WaveformRenderer();
    private final SwingWaveformCanvas canvas = new SwingVectorWaveformCanvas(this);
    private WaveformSource source;
    private BufferedImage offscreenBuffer = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);


    private final JPopupMenu popup;

    public void setSource(WaveformSource source) {
        this.source = source;
    }

    @Override
    public WaveformSource getSource() {
        return source;
    }
    
    public SwingWaveformPanel() {
        this(null);
    }

    private JFrame cacheFrame;
    
    protected JCheckBoxMenuItem overwriteMode = new JCheckBoxMenuItem("Overwrite", true);
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();

    }
    

    public SwingWaveformPanel(WaveformSource source) {

        this.popup = new JPopupMenu("Options");
        popup.add(overwriteMode);
        overwriteMode.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                renderer.setOverwrite(overwriteMode.isSelected());
            }
            
        });
        final JMenuItem cacheItem = new JMenuItem("Set Time Domain");
        cacheItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (null == cacheFrame) {
                    cacheFrame = new JFrame("Set Time Domain (seconds)");
                    cacheFrame.getContentPane().setLayout(new BorderLayout());

                    final JLabel valueLabel = new JLabel(Long.toString(canvas.getTimeDomain() / 1000) + " seconds");

                    final JSlider slider = new JSlider();
                    slider.setMaximum(5 * 60);
                    // slider.setSnapToTicks(true);
                    slider.setPaintTicks(true);
                    slider.setPaintLabels(true);
                    slider.setMajorTickSpacing(60);
                    // slider.setMinorTickSpacing(1000);
                    slider.setValue((int) (long) canvas.getTimeDomain() / 1000);

                    slider.addChangeListener(new ChangeListener() {

                        @Override
                        public void stateChanged(ChangeEvent arg0) {
                            canvas.setTimeDomain(slider.getValue() * 1000L);
                            valueLabel.setText(Long.toString(canvas.getTimeDomain() / 1000) + " seconds");
                        }

                    });
                    cacheFrame.getContentPane().add(slider, BorderLayout.CENTER);
                    cacheFrame.getContentPane().add(valueLabel, BorderLayout.SOUTH);
                    cacheFrame.setSize(640, 120);

                }
                cacheFrame.setLocationRelativeTo(SwingWaveformPanel.this);
                cacheFrame.setVisible(true);
            }
        });


        this.popup.add(cacheItem);
        this.popup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                overwriteMode.setSelected(renderer.getOverwrite());
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        JMenuItem aboutPanel = new JMenuItem(SwingWaveformPanel.class.getSimpleName());
        this.popup.add(aboutPanel);

        setSource(source);
        enableEvents(MouseEvent.MOUSE_PRESSED | MouseEvent.MOUSE_RELEASED | ComponentEvent.COMPONENT_RESIZED);

    }

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        super.processComponentEvent(e);
        switch(e.getID()) {
        case ComponentEvent.COMPONENT_RESIZED:

            Dimension dim = getSize();
//            System.err.println("Component resized:"+dim);
            offscreenBuffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
            break;
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        switch (e.getID()) {
        case MouseEvent.MOUSE_PRESSED:
        case MouseEvent.MOUSE_RELEASED:
            if (e.isPopupTrigger()) {
                popup.show(this, e.getX(), e.getY());
            } else {
                renderer.rescaleValue();
            }
        }
    }

    public WaveformRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void setOutOfTrack(boolean outOfTrack) {
    }

    @Override
    public void start() {
        SwingAnimatorSingleton.getInstance().reference(this);
    }

    @Override
    public void stop() {
        SwingAnimatorSingleton.getInstance().release(this);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(offscreenBuffer, 0, 0, this);
    }

    
    
    @Override
    public void run() {
        try {
            if(null != source && null != renderer) {
                
                Graphics2D graphics = offscreenBuffer.createGraphics();
                canvas.run(renderer, graphics);
                graphics.dispose();
                repaint();
            }
        } catch(Throwable t) {
            t.printStackTrace();
        }
        
    }

    private ScheduledFuture<?> future;
    @Override
    public void setScheduledFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    @Override
    public ScheduledFuture<?> getScheduledFuture() {
        return future;
    }
}
