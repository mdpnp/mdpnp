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
package org.mdpnp.apps.testapp.sim;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ice.GlobalSimulationObjective;
import ice.GlobalSimulationObjectiveDataReader;
import ice.GlobalSimulationObjectiveDataWriter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import javafx.stage.FileChooser;

import org.mdpnp.devices.simulation.GlobalSimulationObjectiveListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.EventLoop.ConditionHandler;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.InstanceHandleSeq;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.OwnershipQosPolicy;
import com.rti.dds.infrastructure.OwnershipStrengthQosPolicy;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicData;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

/**
 * @author Jeff Plourde
 *
 */
public class ScenarioSimControl implements InitializingBean
{
    private static final Logger log = LoggerFactory.getLogger(ScenarioSimControl.class);

    @FXML protected VBox main;
    @FXML Button selectFile;
    @FXML Label fileNameLabel;
    @FXML TextArea pendingMetrics;
    @FXML TextArea publishedMetrics;
    @FXML TextField speedField;
    
    private File selectedFile;
    
    /**
     * A list of ECG rhythm values that are known to VitalsBridge.  We keep these in an ArrayList, so that when one
     * is specified in the script, we can look up the index for it, and then publish the index as a numeric metric.
     * The VitalsBridge receiving device can reverse the process to revert back to the String value that is required
     * in order to publish the proprietary command to the VitalsBridge.<br/><br/>
     * 
     * Needless to say, that requires that the VitalsBridge has exactly the same order of elements.  Needless to say,
     * that means that these should be stored in a common class.  Needless to say, they are not.  Yet.
     */
    private ArrayList<String> vitalsBridgeECG=new ArrayList<>();
    
    private String VB_ECG_METRIC="VB_ECG_RHYTHM";
    
    static final class NumericValue {
        public final String name, metricId;
        public final float lowerBound, upperBound, initialValue, increment;

        public NumericValue(final String name, final String metricId, final float lowerBound, final float upperBound, final float initialValue,
                final float increment) {
            this.name = name;
            this.metricId = metricId;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.initialValue = initialValue;
            this.increment = increment;
        }
    }
    
    /*
     * All the things that Fluke knows about.
     if (rosetta.MDC_PULS_RATE.VALUE.equals(data.metric_id)) {
                pulseRate = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            } else if (rosetta.MDC_PULS_OXIM_SAT_O2.VALUE.equals(data.metric_id)) {
                saturation = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            } else if (rosetta.MDC_RESP_RATE.VALUE.equals(data.metric_id)) {
                respRate = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            } else if (rosetta.MDC_PRESS_BLD_SYS.VALUE.equals(data.metric_id)) {
                invasiveSystolic = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            } else if (rosetta.MDC_PRESS_BLD_DIA.VALUE.equals(data.metric_id)) {
                invasiveDiastolic = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            } else if (rosetta.MDC_PRESS_CUFF_DIA.VALUE.equals(data.metric_id)) {
                noninvasiveDiastolic = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            } else if (rosetta.MDC_PRESS_CUFF_SYS.VALUE.equals(data.metric_id)) {
                noninvasiveSystolic = GlobalSimulationObjectiveListener.toDoubleNumber(data);
            }
     */
    
    
    private final Subscriber subscriber;
    private final Publisher publisher;
    private final EventLoop eventLoop;
    private Topic topic;
    private ice.GlobalSimulationObjectiveDataWriter writer;
    private GlobalSimulationObjective objective;
    private InstanceHandle_t handle;
    private IntegerProperty maxObservedOwnershipStrength = new SimpleIntegerProperty(this, "maxObservedOwnershipStrength", -1);
    private IntegerProperty currentOwnershipStrength = new SimpleIntegerProperty(this, "currentOwnershipStrength", 0);

    //private final UIControl[] controls = new UIControl[numericValues.length];
    
    public ScenarioSimControl(EventLoop eventLoop, Subscriber subscriber, Publisher publisher) {
        this.eventLoop = eventLoop;
        this.subscriber = subscriber;
        this.publisher = publisher;
        
        populateVitalsBridgeMetrics();
    }
    
    private final void populateVitalsBridgeMetrics() {
    	String[] vitals=new String[] {
    			"Ignore",
    			"AFib",
    			"Aflutter4to1",
    			"Aflutter3to1",
    			"Aflutter2to1",
    			"Asystole",
    			"AVBlock1stDegree",
    			"AVBlock2ndDegreeType1_3to2",
    			"AVBlock2ndDegreeType1_4to3",
    			"AVBlock2ndDegreeType1_5to4",
    			"AVBlock2ndDegreeType2_3to2",
    			"AVBlock2ndDegreeType2_4to3",
    			"AVBlock2ndDegreeType2_5to4",
    			"AVBlock3rdDegree",
    			"BundleBranchBlockRight",
    			"BundleBranchBlockLeft",
    			"EctopicAtrial",
    			"HyperkalemiaBase",
    			"HyperkalemiaMild",
    			"HyperkalemiaModerate",
    			"HyperkalemiaSevere",
    			"Idioventricular",
    			"LVH_1",
    			"LVH_2",
    			"LVHStressed",
    			"NormalSinus",
    			"Paced1",
    			"Paced2",
    			"STElevationInferiorAMIBaseline",
    			"STElevationInferiorAMIMild",
    			"STElevationInferiorAMIModerate",
    			"STElevationInferiorAMISevere",
    			"STElevationAnteriorAMIBaseline",
    			"STElevationAnteriorAMIMild",
    			"STElevationAnteriorAMIMModerate",
    			"STElevationAnteriorAMISevere",
    			"STElevationAnteriorAMILate",
    			"STDepressionIschemia",
    			"STDepressionPostIschemia",
    			"TorsadeDePointes",
    			"VFib",
    			"VentricularStandstill"
    	};
    	vitalsBridgeECG.addAll(Arrays.asList(vitals));
    }

    /**
     * initialize the UI. we do not wrap it in the generic init()-like API, but use the spring's interface to
     * emphasize that this is a creation-time call that should be invoked on the same thread as the constructor
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {


        ice.GlobalSimulationObjectiveTypeSupport.register_type(publisher.get_participant(),
                                                               ice.GlobalSimulationObjectiveTypeSupport.get_type_name());

        topic = TopicUtil.findOrCreateTopic(publisher.get_participant(), ice.GlobalSimulationObjectiveTopic.VALUE,
                                         ice.GlobalSimulationObjectiveTypeSupport.class);
        
        writer = (GlobalSimulationObjectiveDataWriter) publisher.create_datawriter_with_profile(topic,
                                                                                                  QosProfiles.ice_library,
                                                                                                  QosProfiles.state,
                                                                                                  null,
                                                                                                  StatusKind.STATUS_MASK_NONE);
        objective = (GlobalSimulationObjective) ice.GlobalSimulationObjective.create();
        
        handle = writer.register_instance(objective);
        
        selectFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser=new FileChooser();
				fileChooser.setTitle("Choose scenario file");
				selectedFile=fileChooser.showOpenDialog(main.getScene().getWindow());
				fileNameLabel.setText(selectedFile.getAbsolutePath());
			}
        	
        });
    }
    
    private ArrayList<TimeAndMetric> timesAndMetric=new ArrayList<>();
    
    int published;
    
    int PENDING_DISPLAY=10;
    
    public void runSequence() {
    	float[] speed=new float[] {1};
    	try {
    		try {
    			speed[0]=Float.parseFloat(speedField.getText());
    		} catch (NumberFormatException nfe) {}
			List<String> allLines=Files.readAllLines(selectedFile.toPath());
			timesAndMetric.removeAll(timesAndMetric);
			published=0;
			String nextLine;
			for(int i=0;i<allLines.size();i++) {
				nextLine=allLines.get(i);
				if(nextLine.startsWith("#")) continue;
				String[] parts=nextLine.split(",");
				TimeAndMetric tr=new TimeAndMetric();
				tr.interval=Long.parseLong(parts[0]);
				tr.metric=parts[1];
				if(tr.metric.equals(VB_ECG_METRIC)) {
					//Special handling for ECG Metric rhythm.  Look up the index of the value
					tr.value=vitalsBridgeECG.indexOf(parts[2]);
					if(tr.value==-1) {
						log.warn("Unknown ECG RHYTHM "+parts[2]+" in control script");
						continue;
					}
				} else {
					tr.value=Float.parseFloat(parts[2]);
				}
				timesAndMetric.add(tr);
			}
			
			//Now we have a full ArrayList of times and rates.
			//We need to make this a separate runnable, because
			//otherwise the sleeps cause the GUI to hang.
			Thread setterThread=new Thread() {
				public void run() {
					updateDisplays();
					for(int i=0;i<timesAndMetric.size();i++) {
						TimeAndMetric tam=timesAndMetric.get(i);
						try {
							//Adjust the sleep interval according to the speed factor.
							Thread.sleep((long)(tam.interval/speed[0]));
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						}
						//Now we've slept that long, set the rate...
						publishMetric(tam);
						published++;
						updateDisplays();
					}
				}
			};
			setterThread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * The objective of this is to have the next N pending transactions displaying in the pending list.
     * It's easier to just let the list of published metrics scroll on... 
     */
    private void updateDisplays() {
    	/*
    	 * For the pending metrics, anything in the metrics list with an index less than the published
    	 * field has already been published.  Anything afterwards is still pending.
    	 */
    	int end = timesAndMetric.size() < PENDING_DISPLAY ? timesAndMetric.size() : PENDING_DISPLAY;
    	StringBuilder sb=new StringBuilder();
    	for(int i=published;i<timesAndMetric.size();i++) {
    		sb.append(timesAndMetric.get(i));
    	}
    	pendingMetrics.setText(sb.toString());
    	if(published>0) {
    		publishedMetrics.appendText(timesAndMetric.get(published-1).toString());
    	}
    }
    
    private void publishMetric(TimeAndMetric tam) {
    	objective.metric_id=tam.metric;
    	if(tam.metric.equals(VB_ECG_METRIC)) {
    		System.err.println("publishing metric for "+VB_ECG_METRIC+" with index "+tam.value);
    	}
    	objective.value=tam.value;
    	writer.write(objective, handle);
    	System.err.println("publishing "+tam.metric+" with value "+objective.value);
    }
    
    class TimeAndMetric {
		/**
		 * How long to sleep before asking for the given rate
		 */
		long interval;
		
		/**
		 * The metric to use.
		 */
		String metric;
		/**
		 * The value to ask for.
		 */
		float value;
		
		@Override
		public String toString() {
			return String.format("%d\t%s\t%f%n", interval, metric, value);
		}
    }
    
    public void shutDown() {

        publisher.delete_datawriter(writer);
        publisher.get_participant().delete_topic(topic);

//        ice.GlobalSimulationObjectiveTypeSupport.unregister_type(publisher.get_participant(), ice.GlobalSimulationObjectiveTypeSupport.get_type_name());
    }
}
