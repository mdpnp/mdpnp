package org.mdpnp.apps.testapp.hl7;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class HL7Application extends JComponent implements LineEmitterListener, StartStopListener {
    private final JTextArea text = new JTextArea();
    private final JTextField host = new JTextField(40);
    private final JTextField port = new JTextField(6);
    private final JButton startStop = new JButton("Start");
    
    protected static final Logger log = LoggerFactory.getLogger(HL7Application.class);
    
    private HL7Emitter model;
    
    public HL7Application() {
        setLayout(new BorderLayout());
        text.setEditable(false);
        JScrollPane scroll = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);
        final JPanel controls = new JPanel(new FlowLayout());
        controls.add(new JLabel("Host:"));
        controls.add(host);
        controls.add(new JLabel("Port:"));
        controls.add(port);
        controls.add(startStop);
        add(controls, BorderLayout.SOUTH);
        
        startStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if("Start".equals(startStop.getText())) {
                    startStop.setEnabled(false);
                    int portNumber = port.getText().isEmpty() ? 0 : Integer.parseInt(port.getText()); 
                    model.start(host.getText(), portNumber);
                    
                } else {
                    startStop.setEnabled(false);
                    model.stop();
                    
                }
            }
            
        });
    }
    
    public void setModel(HL7Emitter model) {
        if(null == model) {
            if(null != this.model) {
                this.model.removeLineEmitterListener(this);
                this.model.removeStartStopListener(this);
                this.model = null;
            }
        } else {
            if(null != this.model) {
                if(this.model.equals(model)) {
                    // NO OP
                } else {
                    this.model.removeLineEmitterListener(this);
                    this.model.removeStartStopListener(this);
                    this.model = model;
                    this.model.addLineEmitterListener(this);
                    this.model.addStartStopListener(this);
                }
            } else {
                this.model = model;
                this.model.addLineEmitterListener(this);
                this.model.addStartStopListener(this);
            }
        }
    }
    
    public void stop() {
        if(model != null) {
            model.stop();
            model = null;
           
        }
    }

    @Override
    public void newLine(String line) {
        Document document = text.getDocument();
        
        try {
            line = line.replaceAll("\\r", "\n");
            document.insertString(0, line+"\n", null);
            if(document.getLength()>MAX_CHARS) {
                document.remove(MAX_CHARS, document.getLength()-MAX_CHARS);
            }
        } catch (BadLocationException e) {
            log.error("", e);
        }
    }
    private static final int MAX_CHARS = 8000;

    @Override
    public void started() {
        try {
            text.getDocument().remove(0, text.getDocument().getLength());
        } catch (BadLocationException e) {
            log.error("", e);
        }
        startStop.setText("Stop");
        startStop.setEnabled(true);
    }

    @Override
    public void stopped() {
        startStop.setText("Start");
        startStop.setEnabled(true);
    }
}
