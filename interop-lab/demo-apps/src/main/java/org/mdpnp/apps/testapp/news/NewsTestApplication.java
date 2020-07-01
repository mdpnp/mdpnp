package org.mdpnp.apps.testapp.news;

import com.google.common.eventbus.Subscribe;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.Time_t;
import himss.AssessmentEntry;
import himss.PatientAssessment;
import himss.PatientAssessmentDataWriter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Background;
import javafx.util.Duration;
import org.mdpnp.apps.testapp.PartitionChooserModel;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalSign;
import org.mdpnp.devices.DeviceIdentityBuilder;
import org.mdpnp.devices.DomainClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class NewsTestApplication implements Initializable, NewsTestApplicationFactory.WithVitalModel, NewsTestApplicationFactory.WithPatientAssessmentDataWriter {
    protected static final Logger log = LoggerFactory.getLogger(NewsTestApplication.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    
    /**
     * Could we just change the names in the scores.xml to match the labels in VitalSign?
     */
    HashMap<String, String> vitalSignsToScore;
    
    /**
     * The current patient score
     */
    private int currentScore;


    @FXML
    ComboBox<ObservationType> observationCodes;
    @FXML
    TextField observationEffectiveTime;
    @FXML
    ChartApplication vitalSignsController;
    @FXML
    RadioButton timeNow;
    @FXML
    RadioButton timeAsOf;
    @FXML
    Pane observationControls;
    @FXML
    Label scoreLabel;

    Timeline datePickUpdate;
    PatientAssessmentDataWriter patientAssessmentWriter;

    InstanceHandle_t handle_t;
    PatientAssessment data = new PatientAssessment();
    
    ArrayList<Score> scoreList=new ArrayList<>();	//Later, map this to Metrics... 

    public NewsTestApplication() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        List<ObservationType> list = readObservationTypes();
        observationCodes.setItems(FXCollections.observableArrayList(list));
        //Also read some score data.  Should this live in a bean, or be CSV instead?
        readScoresFromXML();
        //Map vital signs to scores...
        configureVitalSignsToScore();

        datePickUpdate = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	currentScore=0;
                //System.err.println("NTA.handle called, score starts at "+currentScore);
                if(timeNow.isSelected()) {
                    String v = dateFormat.format(new Date());
                    observationEffectiveTime.setText(v);
                }
                //Use this event loop to also handle the score calculation - maybe every 5 seconds?
                //We have access to the scores from here, and access to the vital values.
                if(vitalSignsController.vitalModel!=null) {
                	for(int i=0;i<vitalSignsController.vitalModel.size();i++) {
        	        	Vital v=vitalSignsController.vitalModel.get(i);
        	        	//System.err.println("Processing vitalModel element "+i+ " with size "+v.size());
        	        	//Keep track of which value in the list of v is the "worst"
        	        	int indexOfWorst=0;
        	        	int worstScore=0;
        	        	if(v.size()==0) {
        	        		//Probably the device(s) have been assigned to a patient and that patient
        	        		//is not the active partition as chosen in the "Select a patient" drop down.
        	        		//Use a score of "-1" to indicate to the chart that the data is no longer valid.
        	        		//System.err.println("v.size() is 0 for i="+i+" but vitalSignsController.vitalModel.size is "+vitalSignsController.vitalModel.size());
        	        		pushScoreToChartApp(v.getMetricIds()[0], -1);
        	        	}
        	        	for(int j=0;j<v.size();j++) {
        	        		if(j==0) {
        	        			/*
        	        			 * When doing the first metric for the given chart,
        	        			 * reset the score for the current chart to 0.  Can't
        	        			 * do this outside the "int j" loop because if no metrics
        	        			 * have been published yet then v.size() is 0 anyway and
        	        			 * that gives ArrayIndexOutOfBounds.  So it happens here,
        	        			 * conditional on it being the first metric on the chart.
        	        			 */
        	        			pushScoreToChartApp(v.get(0).getMetricId(),0);
        	        		}
        	        		//System.err.println("Vital v value is "+v.get(j).getMetricId()+" - "+v.get(j).getValue());
        	        		VitalSign vitalSign=VitalSign.lookupByMetricId(v.get(j).getMetricId());
        	        		String scoreForMetric=vitalSignsToScore.get(vitalSign.label);
        	        		if(scoreForMetric!=null) {
        	        			//System.err.println("Need to do a score for "+scoreForMetric);
        	        			for(Score score : scoreList) {
        	        				if(score.name.equals(scoreForMetric)) {
        	        					float f=v.get(j).getValue();
        	        					for(int k=0;k<score.ranges.size();k++) {
        	        						Range range=score.ranges.get(k);
        	        						if(f>=range.lower && f<=range.upper) {
        	        							if(range.score>worstScore) {
        	        								worstScore=range.score;
	        	        							//System.err.println(vitalSign.label+" has value "+f+" in range "+range.lower+" "+range.upper+", set worstScore to "+worstScore);
	        	        							//System.err.println("Pushing "+v.get(j).getMetricId()+" with score "+range.score+" to chart");
	        	        							pushScoreToChartApp(v.get(j).getMetricId(),range.score);
        	        							}
        	        							break;	//Out of the loop across ranges
        	        						}
        	        					}
        	        				}
        	        			}
        	        		} else {
        	        			//System.err.println("No Score for "+vitalSign.label);
        	        		}
        	        	}
        	        	currentScore+=worstScore;
                        //System.err.println("Added "+worstScore+" to give currentScore of "+currentScore);
                	}
                       //System.err.println("final currentScore is "+currentScore);
                	scoreLabel.setText(String.valueOf(currentScore));
                	setCorrectColorForScore();
                }
            }
        }));
        datePickUpdate.setCycleCount(Timeline.INDEFINITE);
        datePickUpdate.play();
        
    }
    
    private void setCorrectColorForScore() {
    	if(currentScore>=0 && currentScore<2) {
    		scoreLabel.textFillProperty().set(javafx.scene.paint.Color.GREEN);
    		return;
    	}
    	if(currentScore>=2 && currentScore<4) {
    		scoreLabel.textFillProperty().set(javafx.scene.paint.Color.YELLOW);
    		return;
    	}
    	if(currentScore>=4 && currentScore<6) {
    		scoreLabel.textFillProperty().set(javafx.scene.paint.Color.ORANGE);
    		return;
    	}
    	scoreLabel.textFillProperty().set(javafx.scene.paint.Color.RED);
    	
    }
    
    
    private void pushScoreToChartApp(String metricId, int score) {
    	vitalSignsController.pushScoreToChart(metricId, score);
    }

    @Override
    public void setModel(VitalModel vitalModel) {
        vitalSignsController.setModel(vitalModel);
    }

    @Override
    public void configurePatientAssessmentWriter(String idi, PatientAssessmentDataWriter writer) {

        data.operator_id= DeviceIdentityBuilder.randomUDI();

        if(patientAssessmentWriter != null && handle_t != null) {
            patientAssessmentWriter.unregister_instance(data, handle_t);
        }
        patientAssessmentWriter = writer;
        handle_t = null;
        if(patientAssessmentWriter != null) {
            handle_t = patientAssessmentWriter.register_instance(data);
        }

    }

    // API registered with the application-wide event bus
    //
    @Subscribe
    public void onPartitionChooserChangeEvent(PartitionChooserModel.PartitionChooserChangeEvent evt) {
        boolean b = evt.partitionIsPatient();
        observationControls.setDisable(!b);
    }

    @FXML
    public void setEffectiveDate(ActionEvent event) {
        Object src = event.getSource();
        observationEffectiveTime.setVisible(timeAsOf == src);
    }

    @FXML
    public void addObservation() {
        ObservationType oc = observationCodes.getSelectionModel().getSelectedItem();

        Date date;
        try {
            String td = observationEffectiveTime.getText();
            date = dateFormat.parse(td);
        } catch (ParseException e) {
            observationEffectiveTime.setText(dateFormat.toPattern());
            date = null;
        }

        if(date != null && oc != null) {

            AssessmentEntry ae = new AssessmentEntry();
            ae.name = oc.label;
            ae.value = Integer.toString(oc.id);

            data.assessments.userData.clear();
            data.assessments.userData.add(ae);

            Time_t t = DomainClock.toDDSTime(date.getTime());
            data.date_and_time.seconds = t.sec;
            data.date_and_time.nanoseconds = t.nanosec;

            log.info("Create new PatientAssessment: " + data);

            if(patientAssessmentWriter != null) {
                patientAssessmentWriter.write(data, handle_t);
            }
        }
    }
    
//    @FXML
//    public void pauseAppTimeline() {
//    	datePickUpdate.pause();
//    }

    List<ObservationType> readObservationTypes() {
        try {
            URL u = NewsTestApplication.class.getResource("ObservationTypes.csv");
            final InputStream is = u.openStream();
            List<ObservationType> list = readObservationTypesFile(is);
            is.close();
            return list;
        }
        catch(IOException ex) {
            log.error("Failed to load observation types.", ex);
            return Collections.emptyList();
        }
    }

    List<ObservationType> readObservationTypesFile(InputStream is) throws IOException {
        List<ObservationType> codes = new ArrayList<>();
        InputStreamReader fr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while (null != (line = br.readLine())) {
            if (!line.startsWith("#")) {
                String arr[] = line.split("[,]");
                if(arr.length==2)
                    codes.add(new ObservationType(Integer.parseInt(arr[0]), arr[1]));
            }
        }
        br.close();

        Collections.sort(codes, (o1, o2) -> o1.label.compareTo(o2.label));
        return codes;
    }
    
    void readScoresFromXML() {
    	try {
    		URL u = NewsTestApplication.class.getResource("scores.xml");
    		InputStream is=u.openStream();
    		DocumentBuilder db=DocumentBuilderFactory.newInstance().newDocumentBuilder();
    		Document scoresDoc=db.parse(is);
    		NodeList allNodes=scoresDoc.getChildNodes();
    		//System.err.println("allNodes length is "+allNodes.getLength());
			Node node=allNodes.item(0);
			//System.err.println("node type is "+node.getNodeType());
			//System.err.println("node name is "+node.getNodeName());
			NodeList scores=node.getChildNodes();
			for(int i=0;i<scores.getLength();i++) {
				Node scoreNode=scores.item(i);
				if(scoreNode.getNodeType()==Node.TEXT_NODE) continue;
				NamedNodeMap nnm=scoreNode.getAttributes();
				if(scoreNode.getNodeName().equals("score")) {
					Score score=new Score(nnm.getNamedItem("name").getNodeValue());
//					System.err.println("Node with name "+scoreNode.getNodeName()+" is "+nnm.getNamedItem("name").getNodeValue());
					NodeList ranges=scoreNode.getChildNodes().item(1).getChildNodes();
					for(int j=0;j<ranges.getLength();j++) {
						Node rangeNode=ranges.item(j);
						if(rangeNode.getNodeType()==Node.TEXT_NODE) continue;
						NamedNodeMap rangeMap=rangeNode.getAttributes();
						score.ranges.add(new Range(rangeMap.getNamedItem("lower").getNodeValue(),rangeMap.getNamedItem("upper").getNodeValue(),rangeMap.getNamedItem("score").getNodeValue()));
//						System.err.printf("range details are lower %s, upper %s, score %s\n",rangeMap.getNamedItem("lower").getNodeValue(),rangeMap.getNamedItem("upper").getNodeValue(),rangeMap.getNamedItem("score").getNodeValue());
					}
					scoreList.add(score);
//					System.err.println("Added to scoreList");
				}
			}
//			for(Score score: scoreList ) {
//				System.err.println(score);
//			}
    	} catch (Exception e) {
    		//This is really quite critical and so should re-throw to prevent intialization or something?
    		log.error("Failed to load score information",e);
    	}
    }

    public static class ObservationType {
        final int id;
        final String label;

        public ObservationType(int id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
    
    public static class Score {
    	String name;
    	ArrayList<Range> ranges;
    	Score(String name) {
    		this.name=name;
    		ranges=new ArrayList();
    	}
    	
    	@Override
    	public String toString() {
    		StringBuilder sb=new StringBuilder(name);
    		for(int i=0;i<ranges.size();i++) {
    			Range r=ranges.get(i);
    			sb.append("\t");
    			sb.append(r.lower+" ");
    			sb.append(r.upper+" ");
    			sb.append(r.score);
    		}
    		return sb.toString();
    	}
    }
    public static class Range {
		int lower;
		int upper;
		int score;
		Range(int lower, int upper, int score) {
			this.lower=lower;
			this.upper=upper;
			this.score=score;
		}
		Range(String lower, String upper, String score) {
			this.lower=Integer.parseInt(lower);
			this.upper=upper.length()==0 ? Integer.MAX_VALUE : Integer.parseInt(upper);
			this.score=Integer.parseInt(score);
		}
	}
    
    /**
     * Configure the mapping of vital signs between scores.xml and VitalSign.
     * KEYS are from VitalSign label field.
     * VALUES are from scores.xml name field.
     */
    private void configureVitalSignsToScore() {
    	vitalSignsToScore=new HashMap<>();
    	vitalSignsToScore.put("Heart Rate", "Heart Rate");
    	vitalSignsToScore.put("SpO2 Pulse Rate", "Heart Rate");
    	vitalSignsToScore.put("ECG Heart Rate", "Heart Rate");
    	vitalSignsToScore.put("SpO\u2082", "Oxygen Saturation");
    	vitalSignsToScore.put("Respiration Rate", "Respiratory Rate");
    	vitalSignsToScore.put("etCO\u2082", null);
    	vitalSignsToScore.put("Temp", "Temperature");
    	vitalSignsToScore.put("Invasive Systolic", "Systolic Blood Pressure");
    	//TODO: more pairs...
    }
}
