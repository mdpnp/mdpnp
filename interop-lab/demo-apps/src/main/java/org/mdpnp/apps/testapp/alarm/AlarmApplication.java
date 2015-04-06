package org.mdpnp.apps.testapp.alarm;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import org.mdpnp.apps.fxbeans.AlertFx;
import org.mdpnp.apps.fxbeans.AlertFxList;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.Subscriber;

public class AlarmApplication {    
    protected static final Logger log = LoggerFactory.getLogger(AlarmApplication.class);
    
    
    @FXML protected TableView<AlertFx> patientTable, technicalTable; 

    private final AlertFxList patientList, technicalList;
    
    public AlarmApplication() {
        patientList = new AlertFxList(ice.PatientAlertTopic.VALUE);
        patientList.setKeepHistory(true);
        technicalList = new AlertFxList(ice.TechnicalAlertTopic.VALUE);
        technicalList.setKeepHistory(true);
    }
    
    
    
    public void start(EventLoop eventLoop, Subscriber subscriber) {
        patientTable.setItems(patientList);
        technicalTable.setItems(technicalList);
        
        patientList.start(subscriber, eventLoop, null, null, QosProfiles.ice_library, QosProfiles.state);
        technicalList.start(subscriber, eventLoop, null, null, QosProfiles.ice_library, QosProfiles.state);
    }

    public void stop() {
        patientList.stop();
        technicalList.stop();
    }


}
