package org.mdpnp.apps.testapp.diag;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class DiagnosticApplication extends JComponent  {
    
    protected static final Logger log = LoggerFactory.getLogger(DiagnosticApplication.class);
        
    private final JTable numericTable, patientAlertTable, technicalAlertTable;
    
    public DiagnosticApplication() {
        setLayout(new BorderLayout());
        add(new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(numericTable = new JTable()),
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                        new JScrollPane(patientAlertTable = new JTable()),
                        new JScrollPane(technicalAlertTable = new JTable()))
                ), BorderLayout.CENTER);
    }
    
    
    public void stop() {
        
    }

    public void setModel(Diagnostic diagnostic) {
        numericTable.setModel(diagnostic.getNumericModel());
        patientAlertTable.setModel(diagnostic.getPatientAlertModel());
        technicalAlertTable.setModel(diagnostic.getTechnicalAlertModel());
        
    }
}

