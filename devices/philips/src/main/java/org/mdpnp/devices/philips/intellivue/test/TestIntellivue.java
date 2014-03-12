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
package org.mdpnp.devices.philips.intellivue.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mdpnp.devices.philips.intellivue.Intellivue;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class TestIntellivue extends JPanel {

    private class MyIntellivue extends Intellivue {

        public MyIntellivue() throws IOException {
            super();
        }

    }

    private final MyIntellivue intellivue;

    private static final String errorText(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private final void show(Throwable t) {
        t.printStackTrace();
        JOptionPane.showConfirmDialog(this, errorText(t), t.getMessage(), JOptionPane.OK_OPTION);
    }

    public TestIntellivue() throws IOException {
        this.intellivue = new MyIntellivue();
        setLayout(new BorderLayout());
        JPanel controls = new JPanel();
        final JTextField address = new JTextField("192.168.1.199");
        JButton connect = new JButton("Connect");
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // try {
                // intellivue.connect(InetAddress.getByName(address.getText()).getHostAddress());
                // } catch (UnknownHostException e1) {
                // show(e1);
                // } catch (IOException e1) {
                // show(e1);
                // }
            }
        });
        JButton listen = new JButton("Listen");
        listen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // try {
                // intellivue.listenForConnectIndication();
                // } catch (IOException e1) {
                // show(e1);
                // }
            }
        });
        JButton disconnect = new JButton("Disconnect");
        disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // try {
                // intellivue.disconnect();
                // } catch (IOException e1) {
                // show(e1);
                // }
            }
        });
        controls.add(address);
        controls.add(connect);
        controls.add(listen);
        controls.add(disconnect);
        add(controls, BorderLayout.NORTH);
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Test Intellivue");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TestIntellivue ti = new TestIntellivue();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(ti, BorderLayout.CENTER);
        frame.setSize(640, 480);
        frame.setVisible(true);

    }
}
