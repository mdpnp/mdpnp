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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
    private final ImageIcon ice_cubes = new ImageIcon(DemoPanel.class.getResource("blue_ice_cubes.jpg"));
    private final ImageIcon mdpnp = new ImageIcon(DemoPanel.class.getResource("mdpnp-small.png"));

    private final JLabel bedLabel = new JLabel("Intensive Care 15");
    public final static Color darkBlue = new Color(51, 0, 101);
    public final static Color lightBlue = new Color(1, 153, 203);

    private final JPanel header = new JPanel();
    private final JPanel wholeFooter = new JPanel();
    private final JButton back = new JButton("Exit App");
    private final JButton changePartition = new JButton("Change Partition...");
    private final JButton createAdapter = new JButton("Create a local ICE Device Adapter...");
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

    public JButton getBack() {
        return back;
    }
    
    public JButton getChangePartition() {
        return changePartition;
    }
    
    public JButton getCreateAdapter() {
        return createAdapter;
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

        header.add(time, BorderLayout.EAST);
        time.setHorizontalAlignment(SwingConstants.RIGHT);
        time.setHorizontalTextPosition(SwingConstants.RIGHT);
        time.setForeground(darkBlue);
        time.setFont(bedLabel.getFont());
        time.setOpaque(true);
    }

    private void buildContent() {

        content.setOpaque(false);
        add(content, BorderLayout.CENTER);
    }

    private void buildFooter() {
        wholeFooter.setLayout(new GridBagLayout());
        wholeFooter.setOpaque(false);
        wholeFooter.setForeground(darkBlue);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        
        gbc.weightx = 1;
        gbc.weighty = 1;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        
        wholeFooter.add(back, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy++;
        wholeFooter.add(version, gbc);

        
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        wholeFooter.add(changePartition, gbc);
        
        gbc.gridy++;
        wholeFooter.add(createAdapter, gbc);
        
        Font font = Font.decode("Courier-12");
        
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.BOTH;
        JLabel url = new JLabel("http://www.openice.info");
        url.setForeground(darkBlue);
        url.setOpaque(false);
        url.setFont(font.deriveFont(18f));
        url.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        url.setHorizontalTextPosition(SwingConstants.RIGHT);
        url.setHorizontalAlignment(SwingConstants.RIGHT);
        url.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
               if (e.getClickCount() > 0) {
                   if (Desktop.isDesktopSupported()) {
                         Desktop desktop = Desktop.getDesktop();
                         try {
                             URI uri = new URI("http://www.openice.info");
                             desktop.browse(uri);
                         } catch (IOException ex) {

                         } catch (URISyntaxException ex) {
                             
                         }
                 }
               }
            }
         });
        wholeFooter.add(url, gbc);
        version.setForeground(darkBlue);
        version.setOpaque(false);
        version.setFont(font);
        udi.setForeground(darkBlue);
        udi.setFont(font);
        udi.setOpaque(false);
        udi.setHorizontalTextPosition(SwingConstants.RIGHT);
        udi.setHorizontalAlignment(SwingConstants.RIGHT);

        gbc.gridy++;
        wholeFooter.add(udi, gbc);

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

//        super.paintComponent(g);
    }

    private final Date date = new Date();
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void run() {
        date.setTime(System.currentTimeMillis());
        this.time.setText(timeFormat.format(date));
    }
}
