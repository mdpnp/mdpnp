package org.mdpnp.apps.testapp;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class LoginPanel extends JPanel {
    private JTextField clinicianId;

    public JTextField getClinicianId() {
        return clinicianId;
    }

    public LoginPanel() {
        super();
        setOpaque(false);

        JLabel l = new JLabel("Clinician Id:");

        clinicianId = new JTextField(30);
        clinicianId.setBorder(new LineBorder(Color.black, 2));
        clinicianId.setAlignmentY(0.5f);
        clinicianId.setAlignmentX(0.5f);

        add(l);
        add(clinicianId);
    }


}
