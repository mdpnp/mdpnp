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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.ScheduledFuture;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.mdpnp.guis.waveform.WaveformCanvas;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformRenderer;
import org.mdpnp.guis.waveform.WaveformSource;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class SwingWaveformPanel extends javax.swing.JComponent implements WaveformCanvas, WaveformPanel, SwingAnimatable {
    private final WaveformRenderer renderer = new WaveformRenderer();
    private WaveformSource source;
    private int paintImage = 0;
    private Graphics[] graphics;
    private Image[] image;
    private Extent extent;
    private final JPopupMenu popup;

    private static class ExtentImpl extends org.mdpnp.guis.waveform.ExtentImpl {
        public ExtentImpl(Dimension dim) {
            super(0, (int) dim.getWidth(), 0, (int) dim.getHeight());
        }
    }

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
    
    private long timeDomain = 10000L;
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

                    final JLabel valueLabel = new JLabel(Long.toString(timeDomain / 1000) + " seconds");

                    final JSlider slider = new JSlider();
                    slider.setMaximum(5 * 60);
                    // slider.setSnapToTicks(true);
                    slider.setPaintTicks(true);
                    slider.setPaintLabels(true);
                    slider.setMajorTickSpacing(60);
                    // slider.setMinorTickSpacing(1000);
                    slider.setValue((int) (long) timeDomain / 1000);

                    slider.addChangeListener(new ChangeListener() {

                        @Override
                        public void stateChanged(ChangeEvent arg0) {
                            timeDomain = slider.getValue() * 1000L;
                            valueLabel.setText(Long.toString(timeDomain / 1000) + " seconds");
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
        enableEvents(ComponentEvent.COMPONENT_RESIZED | MouseEvent.MOUSE_PRESSED | MouseEvent.MOUSE_RELEASED);

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

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        super.processComponentEvent(e);
        if (e.getID() == ComponentEvent.COMPONENT_RESIZED || e.getID() == ComponentEvent.COMPONENT_SHOWN) {
            Dimension d = e.getComponent().getSize();
            this.extent = new ExtentImpl(d);
            int width = (int) d.getWidth();
            int height = (int) d.getHeight();
            this.image = new Image[] {createImage(width, height), createImage(width, height)};
            this.graphics = new Graphics[] {image[0].getGraphics(), image[1].getGraphics()};

            for(Graphics g : graphics) {
                if (g instanceof Graphics2D) {
                    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    ((Graphics2D) g).setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                }
                g.setColor(getBackground());
                g.fillRect(0, 0, width, height);
            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (extent == null) {
            extent = new ExtentImpl(getSize());
        }
        if (null != image) {
            int width = extent.getMaxX() - extent.getMinX();
            int height = extent.getMaxY() - extent.getMinY();
            
            g.drawImage(image[paintImage], 0, 0, width, height, 0, 0, width, height, this);
        }
    }

    private final Color secondaryColor = new Color(255, 0, 0, 200);

    @Override
    public void drawSecondaryLine(int x0, int y0, int x1, int y1) {
        int height = extent.getMaxY() - extent.getMinY();
        graphics[(paintImage+1)%2].setColor(secondaryColor);
        graphics[(paintImage+1)%2].drawLine(x0, height - y0, x1, height - y1);
    }

    @Override
    public void drawLine(int x0, int y0, int x1, int y1) {
        int height = extent.getMaxY() - extent.getMinY();
        graphics[(paintImage+1)%2].setColor(getForeground());
        graphics[(paintImage+1)%2].drawLine(x0, height - y0, x1, height - y1);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        graphics[(paintImage+1)%2].setColor(getBackground());
        graphics[(paintImage+1)%2].fillRect(x, y, width, height);
    }

    @Override
    public Extent getExtent() {
        return extent;
    }

    public WaveformRenderer getRenderer() {
        return renderer;
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public void setOutOfTrack(boolean outOfTrack) {
    }

    @Override
    public void start() {
        SwingAnimatorSingleton.reference(this);
    }

    @Override
    public void stop() {
        SwingAnimatorSingleton.release(this);
    }

    @Override
    public void run() {
        if(null != source && null != renderer && isShowing()) {
            long now = System.currentTimeMillis();
            graphics[(paintImage+1)%2].setColor(getBackground());
            graphics[(paintImage+1)%2].fillRect(extent.getMinX(), extent.getMinY(), extent.getMaxX(), extent.getMaxY());
            renderer.render(source, this, now-2000L-timeDomain, now-2000L);
            paintImage = (paintImage+1)%2;
            repaint();
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
