package org.mdpnp.apps.testapp.validate;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.mdpnp.apps.testapp.vital.Vital;

import javafx.scene.image.ImageView;
import javafx.scene.control.Label;

public class Chart {
    protected StackedBarChart<String, Number> barChart;
//    protected LineChart<String, Number> lineChart;

    @FXML StackPane stackPane;
    
    @FXML Button removeButton;
    @FXML BorderPane main;
    
    private Vital vital;
    private VitalValidator vitalValidator;
    @FXML ImageView validatedImageView;
    @FXML Label stdev;
    @FXML Label mean;
    @FXML Label n;
    @FXML Label stdevpct;
    
    private ValidationOracle validationOracle;
    
    public Chart() {
        
    }
    
    public VitalValidator getVitalValidator() {
        return vitalValidator;
    }
    
//    public LineChart<String, Number> getLineChart() {
//        return lineChart;
//    }
    
    public Vital getVital() {
        return vital;
    }
    public Button getRemoveButton() {
        return removeButton;
    }
    
    public Label getStdev() {
        return stdev;
    }
    public Label getMean() {
        return mean;
    }
    
    
    public void setModel(final IntegerProperty maxDataPoints, final DoubleProperty maxSigmaPct, Vital v, ValidationOracle validationOracle) {
        this.validationOracle = validationOracle;
        if(null != this.vital) {
            main.setCenter(null);
            barChart.titleProperty().unbind();
            barChart = null;
        }
        this.vital = v;
        if(null != v) {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            
//            lineChart = new LineChart<>(xAxis, new NumberAxis());
            barChart = new StackedBarChart<>(xAxis, yAxis);
//            this.vitalValidator = new VitalValidator(maxDataPoints, maxSigma, v, barChart.getData(), lineChart.getData(), validationOracle);
            this.vitalValidator = new VitalValidator(maxDataPoints, maxSigmaPct, v, barChart.getData(), validationOracle);
            validatedImageView.visibleProperty().bind(vitalValidator.validatedProperty());
            mean.textProperty().bind(Bindings.concat("μ=", vitalValidator.meanProperty().asString("%.1f")));
            stdev.textProperty().bind(Bindings.concat("σ=", vitalValidator.sigmaProperty().asString("%.4f")));
            n.textProperty().bind(Bindings.concat("n=", vitalValidator.nProperty().asString()));
            stdevpct.textProperty().bind(Bindings.concat("σ(%)=", vitalValidator.sigmaPctProperty().asString("%.4f")));
            barChart.setMinHeight(250.0);
            barChart.setVerticalGridLinesVisible(false);
            barChart.setAnimated(false);
            barChart.setCategoryGap(0.0);
//            validator.setData(barChart.getData());
//            barChart.getData().add(validator.getSeries());
            barChart.titleProperty().bind(v.labelProperty());
            
            
//            configureOverlayChart(lineChart);
//            lineChart.setMinHeight(250.0);
//            lineChart.setVerticalGridLinesVisible(false);
//            lineChart.setAnimated(false);
//            lineChart.setCreateSymbols(false);
//            lineChart.setLegendVisible(true);
            
//            stackPane.getChildren().add(0, lineChart);
            stackPane.getChildren().add(0, barChart);
            
//            main.setCenter(barChart);
//            BorderPane.setAlignment(barChart, Pos.CENTER);
        }
    }
    
//    private void configureOverlayChart(final XYChart<String, Number> chart) {
//        chart.setAlternativeRowFillVisible(false);
//        chart.setAlternativeColumnFillVisible(false);
//        chart.setHorizontalGridLinesVisible(false);
//        chart.setVerticalGridLinesVisible(false);
//        chart.getXAxis().setVisible(false);
//        chart.getYAxis().setVisible(false);
//        
//        chart.getStylesheets().addAll(getClass().getResource("overlay-chart.css").toExternalForm());
//    }

}
