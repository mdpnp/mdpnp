package org.mdpnp.apps.testapp.rbs;

import com.rti.dds.publication.Publisher;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.pca.PCAConfig;
import org.mdpnp.apps.testapp.pca.PCAPanel;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelImpl;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 */
public class RBSApplicationFactory implements IceApplicationProvider {

    private final AppType RBS =
            new  AppType("Rule-Based Safety", "NORBS", RBSConfig.class.getResource("rules-safety.png"), 0.75, false);

    @Override
    public AppType getAppType() {
        return RBS;

    }

    @Override
    public IceApp create(ApplicationContext parentContext) throws IOException {

        final DeviceListModel deviceListModel = (DeviceListModel) parentContext.getBean("deviceListModel");
        final Publisher publisher = parentContext.getBean("publisher", Publisher.class);
        final EventLoop eventLoop = parentContext.getBean("eventLoop", EventLoop.class);
        final ObservableList<NumericFx> numericList = parentContext.getBean("numericList", ObservableList.class);

        FXMLLoader loader = new FXMLLoader(RBSPanel.class.getResource("RBSPanel.fxml"));
        Parent ui = loader.load();

        final RBSPanel rbsPanel = loader.getController();

        final VitalModel vitalModel = new VitalModelImpl(deviceListModel, numericList) {

            @Override
            protected String getStatusOKMessage() {
                return "No Abnormal Conditions Detected";
            }

            @Override
            protected Advisory evaluateVital(Vital vital) {

                Advisory a;
                if (vital.isRequired() && vital.isEmpty()) {
                    a = new Advisory(State.Alarm, vital, null, "vital is missing");
                }
                else {
                    a = super.evaluateVital(vital);
                    if (State.Normal == vital.getModelStateTransitionCondition()) {
                        if (a == null) {
                            a = new Advisory(State.Alarm, vital, null, "normal");
                        } else if (a.state == State.Alarm) {
                            // we are not interested in the 'out-of-range-condition' since this vital
                            // triggers the alarm only when it in the 'normal' range.
                            a = null;
                        }
                    }
                }
                return a;
            }

            @Override
            protected State evaluateAdvisories(Map<String, Advisory> advisories) {
                //
                // Warnings do not count for this application. We only care when all
                // conditions become red.
                //
                if (!isEmpty() && advisories.size() == size()) {
                    for (Advisory a : advisories.values()) {
                        if (a.state != State.Alarm)
                            return State.Normal;
                    }
                    return State.Alarm;
                }
                return State.Normal;
            }
        };
        vitalModel.start(publisher, eventLoop);

        return new IceApp() {

            @Override
            public AppType getDescriptor() {
                return RBS;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                rbsPanel.setModel(vitalModel);
            }

            @Override
            public void stop() {
                rbsPanel.setModel(null);
                vitalModel.clear();
            }

            @Override
            public void destroy() {
                vitalModel.stop();
            }
        };
    }
}
