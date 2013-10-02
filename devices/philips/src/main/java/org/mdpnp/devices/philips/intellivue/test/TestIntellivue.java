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
//                try {
//                    intellivue.connect(InetAddress.getByName(address.getText()).getHostAddress());
//                } catch (UnknownHostException e1) {
//                    show(e1);
//                } catch (IOException e1) {
//                    show(e1);
//                }
            }
        });
        JButton listen = new JButton("Listen");
        listen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                try {
//                    intellivue.listenForConnectIndication();
//                } catch (IOException e1) {
//                    show(e1);
//                }
            }
        });
        JButton disconnect = new JButton("Disconnect");
        disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                try {
//                    intellivue.disconnect();
//                } catch (IOException e1) {
//                    show(e1);
//                }
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
