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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class DemoPanel extends JPanel implements Runnable {
    private final PartitionChooser partitionChooser = new PartitionChooser();
    private final ImageIcon ice_cubes = new ImageIcon(DemoPanel.class.getResource("blue_ice_cubes.jpg"));
    private final ImageIcon mdpnp = new ImageIcon(DemoPanel.class.getResource("mdpnp-small.png"));

    private final JLabel bedLabel = new JLabel("Intensive Care 15");
    private final JLabel patientLabel = new JLabel("John Doe");
    public final static Color darkBlue = new Color(51, 0, 101);
    public final static Color lightBlue = new Color(1, 153, 203);

    private final JPanel header = new JPanel();
    private final JPanel wholeFooter = new JPanel();
    private final JPanel topFooter = new JPanel();
    private final JPanel bottomFooter = new JPanel();
    private final JLabel status = new JLabel("  ");
    private final JButton back = new JButton("Exit App");
    private final JLabel time = new JLabel("HH:mm:ss");
    private final JLabel version = new JLabel(" ");
    private final JLabel udi = new JLabel(" ");

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final JPanel content = new JPanel();

    public JPanel getContent() {
        return content;
    }

    public JLabel getUdi() {
        return udi;
    }

    public JLabel getBedLabel() {
        return bedLabel;
    }

    public JLabel getPatientLabel() {
        return patientLabel;
    }

    public JButton getBack() {
        return back;
    }
    
    public PartitionChooser getPartitionChooser() {
        return partitionChooser;
    }

    private void buildHeader() {
        
        header.setLayout(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(Color.white);
        add(header, BorderLayout.NORTH);

        JLabel label = new JLabel(mdpnp);
        label.setOpaque(false);
        header.add(label, BorderLayout.WEST);

        label.setHorizontalAlignment(SwingConstants.LEFT);
        bedLabel.setFont(Font.decode("verdana-bold-25"));

        bedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bedLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        bedLabel.setForeground(darkBlue);
        bedLabel.setOpaque(false);
        header.add(bedLabel, BorderLayout.CENTER);

        header.add(patientLabel, BorderLayout.EAST);
        patientLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        patientLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        patientLabel.setForeground(darkBlue);
        patientLabel.setFont(bedLabel.getFont());
        patientLabel.setOpaque(false);
        partitionChooser.setSize(320, 240);
        patientLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                partitionChooser.setLocationRelativeTo(patientLabel);
                partitionChooser.setVisible(true);
            }
        });
        // SpaceFillLabel.attachResizeFontToFill( header, bedLabel,
        // patientLabel);

    }

    private void buildContent() {
        // JTable table = new JTable(new Object[][] { {"Tomato", "Orange"},
        // {"Banana", "Cantaloupe"} }, new Object[] {"First", "Second"});
        // table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
        // {
        // @Override
        // public Component getTableCellRendererComponent(JTable table,
        // Object value, boolean isSelected, boolean hasFocus,
        // int row, int column) {
        // Component c = super.getTableCellRendererComponent(table, value,
        // isSelected, hasFocus,
        // row, column);
        // ((JComponent)c).setOpaque(false);
        // ((JComponent)c).setBorder(new EmptyBorder(0,0,0,0));
        //
        // // c.setBackground(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        // return c;
        // }
        // });
        // table.getTableHeader().setOpaque(false);
        // table.setGridColor(new Color(1.0f,1.0f,1.0f,1.0f));
        // table.setShowGrid(false);
        //
        // table.setIntercellSpacing(new Dimension(0,0));
        //
        // JScrollPane scroll = new JScrollPane(table);
        // scroll.getViewport().setOpaque(false);
        // scroll.setViewportBorder(new EmptyBorder(0,0,0,0));
        // scroll.setBorder(new EmptyBorder(0,0,0,0));
        //
        // scroll.setOpaque(false);
        // table.setOpaque(false);
        // content.add(scroll);
        content.setOpaque(false);
        add(content, BorderLayout.CENTER);
    }

    private void buildFooter() {
        wholeFooter.setLayout(new GridLayout(2, 1));
        wholeFooter.add(topFooter);
        wholeFooter.add(bottomFooter);
        wholeFooter.setOpaque(false);

        topFooter.setLayout(new BorderLayout());
        bottomFooter.setLayout(new BorderLayout());
        topFooter.setOpaque(false);
        bottomFooter.setOpaque(false);
        topFooter.setForeground(darkBlue);
        bottomFooter.setForeground(darkBlue);

        status.setForeground(darkBlue);
        status.setFont(Font.decode("verdana-15"));
        topFooter.add(back, BorderLayout.WEST);
        topFooter.add(status, BorderLayout.CENTER);
        topFooter.add(time, BorderLayout.EAST);

        time.setForeground(darkBlue);
        time.setOpaque(false);
        time.setFont(Font.decode("verdana-15"));
        version.setForeground(darkBlue);
        version.setOpaque(false);
        version.setFont(Font.decode("Courier-10"));
        udi.setForeground(darkBlue);
        udi.setFont(Font.decode("Courier-10"));
        udi.setOpaque(false);

        bottomFooter.add(udi, BorderLayout.EAST);
        bottomFooter.add(version, BorderLayout.WEST);

        add(wholeFooter, BorderLayout.SOUTH);
        revalidate();
    }

    // private static final Color transparentWhite = new Color(1.0f, 1.0f, 1.0f,
    // 0.8f);
    public DemoPanel() {
        setLayout(new BorderLayout());
        setForeground(darkBlue);
        enableEvents(ComponentEvent.COMPONENT_EVENT_MASK);
        setBackground(new Color(1.0f, 1.0f, 1.0f, 0.8f));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        buildHeader();
        buildContent();
        buildFooter();

        this.timeFuture = executor.scheduleAtFixedRate(this, 1000L - (System.currentTimeMillis() % 1000L) + 10L, 1000L, TimeUnit.MILLISECONDS);

        setChildrenOpaque(this, false);
        setOpaque(true);
    }

    public static void setChildrenOpaque(Component c, boolean opaque) {
        if (c instanceof Container) {
            for (Component co : ((Container) c).getComponents()) {
                setChildrenOpaque(co, opaque);
            }
        }
        if (c instanceof JComponent) {
            ((JComponent) c).setOpaque(opaque);
        }
        if (c instanceof JScrollPane) {
            ((JScrollPane) c).getViewport().setOpaque(opaque);
        }
        if (c instanceof JTable) {
            ((JTable) c).getTableHeader().setOpaque(opaque);
        }
        if (c instanceof JList) {

        }
    }

    private int img_src_x1, img_src_y1, img_src_x2, img_src_y2;
    private ScheduledFuture<?> timeFuture;

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        super.processComponentEvent(e);
        switch (e.getID()) {
        case ComponentEvent.COMPONENT_SHOWN:
            this.timeFuture = executor.scheduleAtFixedRate(this, 1000L - (System.currentTimeMillis() % 1000L) + 10L, 1000L, TimeUnit.MILLISECONDS);
            break;
        case ComponentEvent.COMPONENT_HIDDEN:
            this.timeFuture.cancel(false);
            break;
        case ComponentEvent.COMPONENT_RESIZED:
            Dimension size = getSize();
            double img_width = ice_cubes.getImage().getWidth(this);
            double img_height = ice_cubes.getImage().getHeight(this);

            if (img_width > 0 && img_height > 0) {
                double scr_width = size.getWidth();
                double scr_height = size.getHeight();

                // if the screen is bigger than the image
                if (scr_width >= img_width) {
                    img_src_x1 = 0;
                    img_src_x2 = (int) img_width;
                } else {
                    img_src_x1 = (int) ((img_width - scr_width) / 2.0);
                    img_src_x2 = (int) (img_src_x1 + scr_width);
                }
                if (scr_height >= img_height) {
                    img_src_y1 = 0;
                    img_src_y2 = (int) img_height;
                } else {
                    img_src_y1 = (int) ((img_height - scr_height) / 2.0);
                    img_src_y2 = (int) (img_src_y1 + scr_height);
                }
            }
            break;
        }

    }

    public JLabel getVersion() {
        return version;
    }

    @Override
    protected void paintComponent(Graphics g) {

        g.drawImage(ice_cubes.getImage(), 0, 0, getWidth(), getHeight(), img_src_x1, img_src_y1, img_src_x2, img_src_y2, null);
        // g.drawImage(mdpnp.getImage(), 0, 0, null);
        super.paintComponent(g);

        // super.paintComponent(g);
    }

    private final Date date = new Date();
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void run() {
        date.setTime(System.currentTimeMillis());
        this.time.setText(timeFormat.format(date));
    }
}
