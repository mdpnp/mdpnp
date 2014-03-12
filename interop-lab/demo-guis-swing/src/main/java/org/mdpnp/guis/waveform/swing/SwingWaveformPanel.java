/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.mdpnp.guis.waveform.AbstractNestedWaveformSource;
import org.mdpnp.guis.waveform.CachingWaveformSource;
import org.mdpnp.guis.waveform.EvenTempoWaveformSource;
import org.mdpnp.guis.waveform.NestedWaveformSource;
import org.mdpnp.guis.waveform.TestWaveformSource;
import org.mdpnp.guis.waveform.WaveformCanvas;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformRenderer;
import org.mdpnp.guis.waveform.WaveformSource;
import org.mdpnp.guis.waveform.WaveformSourceListener;

@SuppressWarnings("serial")
public class SwingWaveformPanel extends javax.swing.JComponent implements WaveformCanvas, WaveformSourceListener,
        WaveformPanel {
    private WaveformRenderer renderer;
    private WaveformSource source;
    private Graphics graphics;
    private Extent extent;
    private final JPopupMenu popup;
    private boolean dct = false;
    private SwingDCTSource dct_source;

    @Override
    public EvenTempoWaveformSource evenTempoSource() {
        return AbstractNestedWaveformSource.source(EvenTempoWaveformSource.class, this.source);
    }

    public CachingWaveformSource cachingSource() {
        return AbstractNestedWaveformSource.source(CachingWaveformSource.class, this.source);
    }

    private final static class WaveformSourceTableModel extends AbstractTableModel implements TableModel,
            WaveformSourceListener {
        private final WaveformSource source;

        public WaveformSourceTableModel(WaveformSource source) {
            this.source = source;
        }

        @Override
        public void waveform(WaveformSource source) {
            fireTableDataChanged();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Integer.class;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
            case 0:
                return "Index";
            case 1:
                return "Value";
            default:
                return null;
            }
        }

        @Override
        public int getRowCount() {
            return source.getMax();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
            case 0:
                return rowIndex;
            case 1:
                return source.getValue(rowIndex);
            default:
                return null;
            }
        }

        @Override
        public void reset(WaveformSource source) {
        }
    }

    private static class ExtentImpl extends org.mdpnp.guis.waveform.ExtentImpl {
        public ExtentImpl(Dimension dim) {
            super(0, (int) dim.getWidth(), 0, (int) dim.getHeight());
        }
    }

    public void setRawSource(WaveformSource source) {
        if (null != this.source) {
            this.source.removeListener(dct_source);
            this.source.removeListener(this);
        }
        this.source = source;
        if (null != this.source) {
            this.source.addListener(this);
            this.renderer = new WaveformRenderer(this.source);
            if (dct) {
                this.dct_source = new SwingDCTSource(this.source);
                this.renderer.addOtherSource(255, 0, 0, 255, dct_source);
            }
            if(null != dataTable2) {
                dataTable2.setModel(new WaveformSourceTableModel(SwingWaveformPanel.this.source));
            }
        } else {
            this.renderer = null;
            this.dct_source = null;
        }
    }

    private boolean evenTempo = true;
    private boolean caching = true;

    public void setEvenTempo(boolean evenTempo) {
        this.evenTempo = evenTempo;

    }

    public void setCaching(boolean caching) {
        this.caching = caching;
    }

    public void setSource(WaveformSource source) {
        if (null == source) {
            setRawSource(null);
        } else {
            if (caching) {
                source = new CachingWaveformSource(source, 10000L);
            }
            if (evenTempo) {
                source = new EvenTempoWaveformSource(source);
            }
            setRawSource(source);
        }
    }

    public SwingWaveformPanel() {
        this(null);
    }

    private JFrame dataFrame, cacheFrame, coeffFrame, dataFrame2;
    private JTable dataTable, dataTable2;

    public SwingWaveformPanel(WaveformSource source) {
        this.popup = new JPopupMenu("Options");

        final JMenuItem dctItm = new JCheckBoxMenuItem("Enable DCT");
        dctItm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (dct ^ dctItm.isSelected()) {
                    dct = dctItm.isSelected();
                    WaveformSource src = SwingWaveformPanel.this.source;
                    while (src instanceof NestedWaveformSource) {
                        src = ((NestedWaveformSource) src).getTarget();
                    }
                    setSource(null);
                    setSource(src);
                }

            }

        });
        this.popup.add(dctItm);
        JMenuItem coeff = new JMenuItem("Control Coefficients");
        coeff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (null == coeffFrame) {
                    coeffFrame = new JFrame("Coefficient Control");
                    coeffFrame.getContentPane().setLayout(new BorderLayout());
                    final JLabel valueLabel = new JLabel(Integer.toString(dct_source.getMaxCoeff()));

                    final JSlider slider = new JSlider();
                    slider.setMaximum(dct_source.getMax());
                    slider.setValue(dct_source.getMaxCoeff());
                    slider.addChangeListener(new ChangeListener() {

                        @Override
                        public void stateChanged(ChangeEvent arg0) {
                            dct_source.setMaxCoeff(slider.getValue());
                            valueLabel.setText(Integer.toString(slider.getValue()));
                        }

                    });
                    coeffFrame.getContentPane().add(slider, BorderLayout.CENTER);
                    coeffFrame.getContentPane().add(valueLabel, BorderLayout.SOUTH);
                    coeffFrame.setSize(640, 480);

                }
                coeffFrame.setLocationRelativeTo(SwingWaveformPanel.this);
                coeffFrame.setVisible(true);
            }
        });
        this.popup.add(coeff);
        JMenuItem data = new JMenuItem("Show Data");
        this.popup.add(data);
        data.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (null == dataFrame) {
                    dataFrame = new JFrame("Waveform Data");
                    dataTable = new JTable(dct_source);
                    dataFrame.getContentPane().add(new JScrollPane(dataTable));
                    dataFrame.setSize(640, 480);
                }
                dataFrame.setLocationRelativeTo(SwingWaveformPanel.this);
                dataFrame.setVisible(true);
            }
        });

        final JMenuItem cacheItem = new JMenuItem("Set Time Domain");
        cacheItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (null == cacheFrame) {
                    final CachingWaveformSource cachesource = cachingSource();
                    cacheFrame = new JFrame("Set Time Domain (seconds)");
                    cacheFrame.getContentPane().setLayout(new BorderLayout());
                    final JLabel valueLabel = new JLabel(Long.toString(cachesource.getFixedTimeDomain() / 1000)
                            + " seconds");

                    final JSlider slider = new JSlider();
                    slider.setMaximum(5 * 60);
                    // slider.setSnapToTicks(true);
                    slider.setPaintTicks(true);
                    slider.setPaintLabels(true);
                    slider.setMajorTickSpacing(60);
                    // slider.setMinorTickSpacing(1000);
                    slider.setValue((int) (long) cachesource.getFixedTimeDomain() / 1000);

                    slider.addChangeListener(new ChangeListener() {

                        @Override
                        public void stateChanged(ChangeEvent arg0) {
                            cachesource.setFixedTimeDomain(slider.getValue() * 1000);
                            valueLabel.setText(Long.toString(cachesource.getFixedTimeDomain() / 1000) + " seconds");
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

        final JCheckBoxMenuItem caching = new JCheckBoxMenuItem("Cache History");
        caching.setSelected(this.caching);
        caching.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(SwingWaveformPanel.this.caching ^ caching.isSelected()) {
                    setCaching(caching.isSelected());
                    WaveformSource src = SwingWaveformPanel.this.source;
                    while (src instanceof NestedWaveformSource) {
                        src = ((NestedWaveformSource) src).getTarget();
                    }
                    setSource(null);
                    setSource(src);
                }
            }

        });
        this.popup.add(caching);

        final JCheckBoxMenuItem tempoing = new JCheckBoxMenuItem("Even Tempo");
        tempoing.setSelected(evenTempo);
        tempoing.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(evenTempo ^ tempoing.isSelected()) {
                    setEvenTempo(tempoing.isSelected());
                    WaveformSource src = SwingWaveformPanel.this.source;
                    while (src instanceof NestedWaveformSource) {
                        src = ((NestedWaveformSource) src).getTarget();
                    }
                    setSource(null);
                    setSource(src);
                }
            }

        });
        this.popup.add(tempoing);

        this.popup.add(cacheItem);
        this.popup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                WaveformSource source = SwingWaveformPanel.this.source;
                if (null == source) {
                    sampleRate.setText("");
                } else {
                    sampleRate.setText("" + source.getMillisecondsPerSample() + " ms/sample");
                }
                if (null == cachingSource()) {
                    cacheItem.setVisible(false);
                } else {
                    cacheItem.setVisible(true);
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        JMenuItem realdata = new JMenuItem("Show Real Data");
        this.popup.add(realdata);
        realdata.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (null == dataFrame2) {
                    dataFrame = new JFrame("Waveform Data");
                    dataFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    dataTable2 = new JTable(new WaveformSourceTableModel(SwingWaveformPanel.this.source));
                    dataFrame.getContentPane().add(new JScrollPane(dataTable2));
                    dataFrame.setSize(640, 480);
                    dataFrame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            SwingWaveformPanel.this.source.removeListener((WaveformSourceListener) dataTable2.getModel());
                            dataTable2.getModel().removeTableModelListener(dataTable2);
                            super.windowClosing(e);
                        }
                    });
                }
                dataFrame.setLocationRelativeTo(SwingWaveformPanel.this);
                dataFrame.setVisible(true);
            }
        });
        JMenuItem aboutPanel = new JMenuItem(SwingWaveformPanel.class.getSimpleName());
        this.popup.add(aboutPanel);
        this.popup.add(sampleRate);

        setSource(source);
        enableEvents(ComponentEvent.COMPONENT_RESIZED | MouseEvent.MOUSE_PRESSED | MouseEvent.MOUSE_RELEASED);

    }

    private final JMenuItem sampleRate = new JMenuItem();

    private Image image;

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
    protected synchronized void processComponentEvent(ComponentEvent e) {
        super.processComponentEvent(e);
        if (e.getID() == ComponentEvent.COMPONENT_RESIZED || e.getID() == ComponentEvent.COMPONENT_SHOWN) {
            Dimension d = e.getComponent().getSize();
            extent = new ExtentImpl(d);
            int width = (int) d.getWidth();
            int height = (int) d.getHeight();
            image = createImage(width, height);
            this.graphics = image.getGraphics();

            if (this.graphics instanceof Graphics2D) {
                ((Graphics2D) this.graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D) this.graphics).setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                ((Graphics2D) this.graphics).setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
            }

            this.graphics.setColor(getBackground());
            this.graphics.fillRect(0, 0, width, height);
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        if (extent == null) {
            extent = new ExtentImpl(getSize());
        }

        if (null != image) {
            int width = extent.getMaxX() - extent.getMinX();
            int height = extent.getMaxY() - extent.getMinY();
            g.drawImage(image, 0, 0, width, height, 0, 0, width, height, this);
        }
    }

    private final Color secondaryColor = new Color(255, 0, 0, 200);

    @Override
    public synchronized void drawSecondaryLine(int x0, int y0, int x1, int y1) {
        int height = extent.getMaxY() - extent.getMinY();
        graphics.setColor(secondaryColor);
        graphics.drawLine(x0, height - y0, x1, height - y1);
    }

    @Override
    public synchronized void drawLine(int x0, int y0, int x1, int y1) {
        int height = extent.getMaxY() - extent.getMinY();
        graphics.setColor(getForeground());
        graphics.drawLine(x0, height - y0, x1, height - y1);
    }

    @Override
    public synchronized void clearRect(int x, int y, int width, int height) {
        graphics.setColor(getBackground());
        graphics.fillRect(x, y, width, height);
    }

    @Override
    public Extent getExtent() {
        return extent;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TEST");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        WaveformSource source = new TestWaveformSource();

        final SwingWaveformPanel panel = new SwingWaveformPanel(new CachingWaveformSource(source, 15000L));

        frame.getContentPane().add(panel);
        frame.setSize(640, 480);
        frame.setVisible(true);

    }

    private WaveformRenderer.Rect rect = new WaveformRenderer.Rect();

    @Override
    public void waveform(WaveformSource source) {
        if (extent == null) {
            extent = new ExtentImpl(getSize());
        }
        WaveformRenderer renderer = this.renderer;
        if (null != renderer && null != graphics) {
            renderer.render(this, rect);
        }
        repaint();
    }

    @Override
    public void reset(WaveformSource source) {
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
    }

    @Override
    public void stop() {
    }
}
