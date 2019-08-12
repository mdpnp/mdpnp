package org.mdpnp.apps.testapp.news;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import org.mdpnp.apps.testapp.vital.VitalSign;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the application where users can add vital signs to charts.  The time intervals
 * available in the selection box are defined here in the enum.  The animation of the chart
 * is provided by the implementation of EventHandler<ActionEvent>, that is invoked by the
 * KeyFrame for the Timeline.  Then handle() method calculates the current time and subtracts
 * the interval, and those two values then set the upper and lower bounds for the x-axis.  As
 * the KeyFrame has a one second interval, the graph is shifted by 1 second each second.
 *
 */
public class ChartApplication implements ListChangeListener<Vital>, EventHandler<ActionEvent>, NewsTestApplicationFactory.WithVitalModel {
    protected static final Logger log = LoggerFactory.getLogger(ChartApplication.class);
    private ArrayList<DateAxis> dateAxes=new ArrayList<DateAxis>();	//Add to this for each chart...
    @FXML Pane charts;
    @FXML Button forward;
    @FXML Button backward;
    @FXML Button resume;
    VitalModel vitalModel; 
    private Timeline timeline;
    @FXML ComboBox<VitalSign> vitalSigns;
    private long interval = 2 * 60 * 60 * 1000L;
    @FXML ComboBox<TimeInterval> timeInterval;
    
    /**
     * Use this to hold a list of all the Chart objects as they are created,
     * so the list can be looped through, finding the metrics and then setting
     * the score on the relevant chart.  Later, map metrics directly to charts
     * so it can just be a lookup instead of a loop.
     */
    private ArrayList<Chart> allCharts=new ArrayList<>();
    
    public enum TimeInterval {
        _10SECONDS("10 Seconds", 10000L),
        _1MINUTE("1 Minute", 60000L),
        _5MINUTES("5 Minutes", 5 * 60000L),
        _15MINUTES("15 Minutes", 15 * 60000L),
        _1HOUR("1 Hour", 60 * 60000L),
        _2HOURS("2 Hours", 2 * 60 * 60000L),
        _3HOURS("3 Hours", 3 * 60 * 60000L),
        _4HOURS("4 Hours", 4 * 60 * 60000L),
        _5HOURS("5 Hours", 5 * 60 * 60000L),
        _6HOURS("6 Hours", 6 * 60 * 60000L);
        
        
        private String label;
        private long interval;
        
        public long getInterval() {
            return interval;
        }
        public String getLabel() {
            return label;
        }
        
        private TimeInterval(String label, long interval) {
            this.label = label;
            this.interval = interval;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    public ChartApplication() {
    }

    @Override
    public void setModel(VitalModel vitalModel) {
        timeInterval.setItems(FXCollections.observableArrayList(TimeInterval.values()));
        vitalSigns.setItems(FXCollections.observableArrayList(VitalSign.values()));

        if(null != this.vitalModel) {
            this.vitalModel.removeListener(this);
            if(timeline!=null) {
                timeline.stop();
                timeline = null;
            }
        }
        this.vitalModel = vitalModel;
        if(null != vitalModel) {
            vitalModel.addListener(this);
            timeline = new Timeline(new KeyFrame(new Duration(1000.0), this));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }
    }

    @Override
    public void onChanged(javafx.collections.ListChangeListener.Change<? extends Vital> c) {
        while(c.next()) {
            Iterator <? extends Vital> vitr = c.getRemoved().iterator();
            while(vitr.hasNext()) {
                final Vital vi = vitr.next();
                Iterator<Node> childitr = charts.getChildren().iterator();
                while(childitr.hasNext()) {
                    Node n = childitr.next();
                    Chart chart = (Chart) n.getUserData();
                    if(chart.getVital().equals(vi)) {
                        chart.setModel(null, null);
                        childitr.remove();
                    }
                }
            }
            
            
            vitr = c.getAddedSubList().iterator();
            while(vitr.hasNext()) {
                final Vital vi = vitr.next();
                
                FXMLLoader loader = new FXMLLoader(Chart.class.getResource("Chart.fxml"));
                try {
                    Parent node = loader.load();
                    Chart chart = loader.getController();
                    allCharts.add(chart);
                    //New chart, new DateAxis
                    long now = System.currentTimeMillis();
                    now -= now % 1000;
                    DateAxis dateAxis=new DateAxis(new Date(now - interval), new Date(now));
//                    dateAxis.setLowerBound();
//                    dateAxis.setUpperBound();
                    dateAxis.setAutoRanging(false);
                    dateAxis.setAnimated(false);
                    dateAxes.add(dateAxis);
                    chart.setModel(vi, dateAxis);
                    node.setUserData(chart);
                    log.info("Added chart has x-azis "+chart.lineChart.getXAxis());
                    chart.getRemoveButton().setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            vitalModel.remove(vi);
                        }
                        
                    });
                    backward.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							//Pause or stop?
							timeline.pause();
							Date currentLower=dateAxis.getLowerBound();
							Date currentUpper=dateAxis.getUpperBound();
							long lowerMillis=currentLower.getTime();
							lowerMillis-=interval;
							Date newLower=new Date(lowerMillis);
							long upperMillis=currentUpper.getTime();
							upperMillis-=interval;
							Date newUpper=new Date(upperMillis);
							dateAxes.forEach(a -> {
								a.setLowerBound(newLower);
								a.setUpperBound(newUpper);
							});
							
						}

                    });
                    
                    forward.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							//Pause or stop?
							timeline.pause();
							Date currentLower=dateAxis.getLowerBound();
							Date currentUpper=dateAxis.getUpperBound();
							long lowerMillis=currentLower.getTime();
							lowerMillis+=interval;
							Date newLower=new Date(lowerMillis);
							long upperMillis=currentUpper.getTime();
							upperMillis+=interval;
							Date newUpper=new Date(upperMillis);
							dateAxes.forEach(a -> {
								a.setLowerBound(newLower);
								a.setUpperBound(newUpper);
							});
						}
						
                    });
                    
                    resume.setOnAction( e -> {
                    	timeline.play();
                    });
                    
                    charts.getChildren().add(0,node);
                } catch (IOException e) {
                    log.error("Unable to create a Chart", e);
                }

            }
        }
        
    }

    @Override
    public void handle(ActionEvent event) {
        long now = System.currentTimeMillis();
        now -= now % 1000;
        Date lowerBound=new Date(now - interval);
        Date upperBound=new Date(now);
        dateAxes.forEach( a -> {
        	//a is a DateAxis object
        	a.setLowerBound(lowerBound);
            a.setUpperBound(upperBound);	
        });
        
        //System.err.printf("lower and upper now %s and %s\n",lowerBound,upperBound);
    }

    @FXML public void addVitalSign() {
        VitalSign vs = vitalSigns.getSelectionModel().getSelectedItem();
        if(null != vs) {
            vs.addToModel(vitalModel);
        }
    }

    @FXML public void onTimeInterval(ActionEvent event) {
        TimeInterval ti = timeInterval.getSelectionModel().getSelectedItem();
        if(null != ti) {
            interval = ti.getInterval();
        }
    }
    
    public void pushScoreToChart(String metricId, int score) {
    	boolean pushed=false;
    	for(int i=0;i<allCharts.size();i++) {
    		Chart chart=allCharts.get(i);
    		String[] metrics=chart.getVital().getMetricIds();
    		//System.err.println("chart "+i+" has "+metrics.length+" metricss");
    		for(String metric : metrics) {
    			if(metric.equals(metricId)) {
    				//System.err.println("chart "+i+" metric "+metric+" matches");
    				if(score==-1) {
    					chart.currentScoreText.setText("X");
    				} else {
    					chart.currentScoreText.setText(String.valueOf(score));
    				}
    				pushed=true;
    			}
    		}
    	}
    	//This shouldn't happen, as the metrics come from the Vital Model anyway, and the Vital Model
    	//is the list of things that are being displayed on charts
    	if(!pushed) {
    		System.err.println("No chart was handling metricId "+metricId+" with score "+score);
    	}
    }
}
