package org.mdpnp.apps.device;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private final Label[] params;

    public abstract String[] getWaveformMetricIds();
    public abstract String[] getWaveformLabels();
    public abstract int getParameterCount();
    public abstract String[] getParameterMetricIds(int i);
    public abstract String getParameterUnits(int i);
    public abstract String getParameterLabel(int i);
    public abstract String getStyleClassName();
    public abstract Paint getWaveformPaint();
    
    private final Set<String> waveformMetrics = new HashSet<String>();
    private final Set<String>[] parameterMetrics;
  
  
  private final Map<String, WaveformPanel> panelMap = new HashMap<String, WaveformPanel>();
  private final GridPane waves = new GridPane();
  
  public AbstractWaveAndParamsPanel() {
      getStyleClass().add(getStyleClassName());
      
      waveformMetrics.addAll(Arrays.asList(getWaveformMetricIds()));
      parameterMetrics = new Set[getParameterCount()];
      for(int i = 0; i < parameterMetrics.length; i++) {
          parameterMetrics[i] = new HashSet<String>();
          parameterMetrics[i].addAll(Arrays.asList(getParameterMetricIds(i)));
      }

      setBottom(labelLeft("Last Sample: ", time));
      setCenter(waves);

      GridPane numerics = new GridPane();
      FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(time.getFont());
      // TODO This is a layout hack
      float w = fm.computeStringWidth("RespiratoryRate");
      numerics.setMinWidth(w);
      numerics.setPrefWidth(w);
      
      params = new Label[getParameterCount()];
      for(int i = 0; i < params.length; i++) {
          BorderPane t;
          params[i] = new Label(" ");
          params[i].getStyleClass().add("parameter");
          numerics.add(t = label(getParameterLabel(i), params[i]), 0, i);
          
          GridPane.setVgrow(t, Priority.ALWAYS);
          GridPane.setHgrow(t, Priority.ALWAYS);
          t.setRight(new Label(getParameterUnits(i)));
          BorderPane.setAlignment(t.getRight(), Pos.BOTTOM_LEFT);
      }
      setRight(numerics);
  }

  @Override
  public void destroy() {
      for (WaveformPanel wp : panelMap.values()) {
          wp.stop();
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
  }
  protected void remove(NumericFx data) {
      for(int i = 0; i < getParameterCount(); i++) {
          if(parameterMetrics[i].contains(data.getMetric_id())) {
              params[i].textProperty().unbind();
          }
      }
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
      if(!time.textProperty().isBound()) {
          time.textProperty().bind(data.presentation_timeProperty().asString());
      }
      if(waveformMetrics.contains(data.getMetric_id())) {
          WaveformPanel wuws = panelMap.get(data.getMetric_id());
          if (null == wuws) {
              SampleArrayWaveformSource saws = new SampleArrayWaveformSource(deviceMonitor.getSampleArrayList().getReader(), data.getHandle());
              wuws = new WaveformPanelFactory().createWaveformPanel();
              wuws.setSource(saws);
              final WaveformPanel _wuws = wuws;
              final int idx = panelMap.size();
              panelMap.put(data.getMetric_id(), wuws);
              ((JavaFXWaveformPane)_wuws).getCanvas().getGraphicsContext2D().setStroke(getWaveformPaint());
              Node x = (Node) _wuws;
              GridPane.setVgrow(x, Priority.ALWAYS);
              GridPane.setHgrow(x, Priority.ALWAYS);
              waves.add(x, 0, idx);
              wuws.start();
          }
          
      }

  }
  
  protected void remove(SampleArrayFx data) {
      time.textProperty().unbind();
      WaveformPanel wuws = panelMap.remove(data.getMetric_id());
      if(null != wuws) {
          wuws.stop();
          wuws.setSource(null);
          waves.getChildren().remove(wuws);
      }
  }
  
  private final OnListChange<SampleArrayFx> sampleArrayListener = new OnListChange<SampleArrayFx>(
          (t)->add(t), null, (t)->remove(t));

 
}
