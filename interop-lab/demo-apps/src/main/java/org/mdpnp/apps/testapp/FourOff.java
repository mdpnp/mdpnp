package org.mdpnp.apps.testapp;

import java.awt.Dimension;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

public class FourOff {
    public static void main(String[] args) {
        JFrame window = new JFrame("Test Webcam Panel");
        Webcam webcam = Webcam.getDefault();
        webcam.setCustomViewSizes(new Dimension[] {new Dimension(1600,1200)});
        webcam.setViewSize(new Dimension(1600,1200));
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPS(25.0);
        window.add(panel);
        window.pack();
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
