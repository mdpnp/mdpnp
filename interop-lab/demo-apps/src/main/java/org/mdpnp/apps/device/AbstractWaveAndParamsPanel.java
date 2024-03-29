package org.mdpnp.apps.device;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.border.Border;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.guis.waveform.javafx.JavaFXWaveformPane;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

public abstract class AbstractWaveAndParamsPanel extends DevicePanel {
    private final Label time = new Label(" ");
    protected final Label[] params;
    protected BorderPane[] paramLabelBorders;

    public abstract String[] getWaveformMetricIds();
    public abstract String[] getWaveformLabels();
    public abstract int getParameterCount();
    public abstract String[] getParameterMetricIds(int i);
    public abstract String getParameterUnits(int i);
    public abstract String getParameterLabel(int i);
    public abstract String getStyleClassName();
    public abstract Paint getWaveformPaint();
    
    /**
     * 
     * @param metricId - the metric to be published.  This method is a "compatibility" wrapper to allow
     * us to have different coloured waveforms for different metrics.  Previously this class just called
     * getWaveformPaint() without a metric, so to avoid changing all the subclasses we just call that
     * method from in here, and then any subclass that wants different colours can override this.
     * 
     * @return a Paint for the waveform.
     */
    public Paint getWaveformPaint(String metricId) {
    	return getWaveformPaint();
    }
    
    /**
     * A method (possibly in the wrong place) to allow mix and max scaling to be fixed for a metric.
     * At the moment, the renderer auto scales min/max according to the incoming data.  There are some
     * cases where we want to override that.  This is a pre-cursor to allowing that.  This base implementation
     * just returns the min and max doubles, which is the same pair of values that the renderer uses.
     * 
     * Actually that never works - we need null to retain the default values.
     * 
     * @param metricId
     * @return
     */
    public double[]  getMinMaxForMetric(String metricId) {
    	return null;
    }
    
    private final Set<String> waveformMetrics = new HashSet<String>();
    private final Set<String>[] parameterMetrics;
  
  
  private final Map<String, BorderPane> panelMap = new HashMap<String, BorderPane>();
  private final GridPane waves = new GridPane();
  
  private static final double LEFT_RIGHT_PANEL_WIDTH = 100.0;
  
  public AbstractWaveAndParamsPanel() {
      getStyleClass().add(getStyleClassName());
      
      waveformMetrics.addAll(Arrays.asList(getWaveformMetricIds()));
      parameterMetrics = new Set[getParameterCount()];
      for(int i = 0; i < parameterMetrics.length; i++) {
          parameterMetrics[i] = new HashSet<String>();
          parameterMetrics[i].addAll(Arrays.asList(getParameterMetricIds(i)));
      }

      setBottom(labelLeft("", time));
      setCenter(waves);

      GridPane numerics = new GridPane();
      numerics.setMinWidth(LEFT_RIGHT_PANEL_WIDTH);
      numerics.setPrefWidth(LEFT_RIGHT_PANEL_WIDTH);

      params = new Label[getParameterCount()];
      paramLabelBorders = new BorderPane[params.length];

      for(int i = 0; i < params.length; i++) {
          BorderPane t;
          params[i] = new Label(" ");
          params[i].getStyleClass().add("parameter");
          paramLabelBorders[i]=label(getParameterLabel(i)+" ("+getParameterUnits(i)+")", params[i]);
          numerics.add(t = paramLabelBorders[i], 0, i);
          
          GridPane.setVgrow(t, Priority.ALWAYS);
          GridPane.setHgrow(t, Priority.ALWAYS);
      }
      setRight(numerics);
  }

  protected void customiseLabels() {

  }

  @Override
  public void destroy() {
      for (BorderPane wp : panelMap.values()) {
          ((WaveformPanel)wp.getCenter()).stop();
      }
      deviceMonitor.getNumericModel().removeListener(numericListener);
      deviceMonitor.getSampleArrayModel().removeListener(sampleArrayListener);
      super.destroy();
  }
  
  protected void add(NumericFx data) {
      for(int i = 0; i < getParameterCount(); i++) {
          if(parameterMetrics[i].contains(data.getMetric_id())) {
              params[i].textProperty().bind(data.valueProperty().asString("%.0f"));
          }
      }
      addToHeader(data);
  }

  protected void add(NumericFx data, String format) {
      for(int i = 0; i < getParameterCount(); i++) {
          if(parameterMetrics[i].contains(data.getMetric_id())) {
              params[i].textProperty().bind(data.valueProperty().asString(format));
          }
      }
      addToHeader(data);
  }

  protected void remove(NumericFx data) {
      for(int i = 0; i < getParameterCount(); i++) {
          if(parameterMetrics[i].contains(data.getMetric_id())) {
              params[i].textProperty().unbind();
          }
      }
  }
  
  protected void addToHeader(NumericFx data) {
	  
  }
  
  private final OnListChange<NumericFx> numericListener = new OnListChange<NumericFx>(
          (t)->add(t), null, (t)->remove(t));
  
  public void set(DeviceDataMonitor deviceMonitor) {
      super.set(deviceMonitor);
      deviceMonitor.getNumericModel().addListener(numericListener);
      deviceMonitor.getNumericModel().forEach((t)->add(t));
      deviceMonitor.getSampleArrayModel().addListener(sampleArrayListener);
      deviceMonitor.getSampleArrayModel().forEach((t)->add(t));
  };
  
  protected void add(SampleArrayFx data) {
	  String myTempMetricId=data.getMetric_id();
	  double myMinMax[]=getMinMaxForMetric(myTempMetricId);
      if(!time.textProperty().isBound()) {
          time.textProperty().bind(data.presentation_timeProperty().asString());
      }
      if(waveformMetrics.contains(data.getMetric_id())) {
          BorderPane bp = panelMap.get(data.getMetric_id());
          if (null == bp) {
              SampleArrayWaveformSource saws = new SampleArrayWaveformSource(deviceMonitor.getSampleArrayList().getReader(), data.getHandle());
              WaveformPanel wuws = new WaveformPanelFactory().createWaveformPanel();
              wuws.setSource(saws);
              if(myMinMax!=null) {
            	  System.err.println("Using min max "+myMinMax[0]+" "+myMinMax[1]+" for metric " +myTempMetricId);
              }
              wuws.setFixedMinMax(myMinMax);
              final int idx = panelMap.size();
              
              ((JavaFXWaveformPane)wuws).getCanvas().getGraphicsContext2D().setStroke(getWaveformPaint(data.getMetric_id()));
              Node x = (Node) wuws;
              bp = new BorderPane(x);
              GridPane.setVgrow(bp, Priority.ALWAYS);
              GridPane.setHgrow(bp, Priority.ALWAYS);
              
              for(int i = 0; i < getWaveformMetricIds().length; i++) {
                  if(getWaveformMetricIds()[i].equals(data.getMetric_id())) {
                      Label l = new Label(getWaveformLabels()[i]);
                      l.setMinWidth(LEFT_RIGHT_PANEL_WIDTH);
                      l.setPrefWidth(LEFT_RIGHT_PANEL_WIDTH);
                      l.setWrapText(true);
                      bp.setLeft(l);
                      bp.setPadding(new Insets(0,0,10,0));
                      break;
                  }
              }
              panelMap.put(data.getMetric_id(), bp);
              //TODO: Can we have an order somehow? Maybe a "getIndexForMetric" method or something?
              waves.add(bp, 0, idx);

              wuws.start();
          }
          
      }

  }
  
  protected void remove(SampleArrayFx data) {
      time.textProperty().unbind();
      BorderPane bp = panelMap.remove(data.getMetric_id());
      if(null != bp) {
          WaveformPanel wuws = (WaveformPanel) bp.getCenter();
          wuws.stop();
          wuws.setSource(null);
          waves.getChildren().remove(bp);
      }
  }
  
  private final OnListChange<SampleArrayFx> sampleArrayListener = new OnListChange<SampleArrayFx>(
          (t)->add(t), null, (t)->remove(t));

 
}
