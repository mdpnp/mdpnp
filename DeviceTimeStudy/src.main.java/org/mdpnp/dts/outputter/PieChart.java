package org.mdpnp.dts.outputter;

import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.util.Rotation;

/**
 * 
 * @author dalonso@mdpnp.org
 * This class uses the JFreeChart library to create Pie Charts
 *
 */
public class PieChart extends ApplicationFrame{
	  private static final long serialVersionUID = 1L;
	  
	  private DefaultPieDataset dataset;
	  
		public void setDataset(DefaultPieDataset dataset){
			this.dataset = dataset;
		}

	  public PieChart(String applicationTitle) {
	        super(applicationTitle);
	  }
	  
	  /**
	   * Prints a 3D pie chart
	   * @param chartTitle
	   */
	  public void print3DPieChart(String chartTitle){
	        // based on the dataset we create the chart
	        JFreeChart chart = create3DChart(dataset, chartTitle);
	        // we put the chart into a panel
	        ChartPanel chartPanel = new ChartPanel(chart);
	        // default size
	        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
	        // add it to our application
	        setContentPane(chartPanel);
	  }
	  
	  /**
	   * Prints a 2D Pie Chart
	   * @param title
	   */
	  public void print2DPieChart(String title){
		  setContentPane(createPanel(dataset, title));
	  }
	    
	  /**
	     * Creates a 3D chart
	     */

	    private JFreeChart create3DChart(PieDataset dataset, String title) {
	        
	        JFreeChart chart = ChartFactory.createPieChart3D(title,          // chart title
	            dataset,                // data
	            true,                   // include legend
	            true,
	            false);

	        PiePlot3D plot = (PiePlot3D) chart.getPlot();
	        plot.setStartAngle(290);
	        plot.setDirection(Rotation.CLOCKWISE);
	        plot.setForegroundAlpha(0.5f);
	        return chart;
	        
	    }
	    
	    /**
	     * Creates a 2D chart
	     * @param dataset
	     * @param title
	     * @return
	     */
	    private static JFreeChart create2DChart(PieDataset dataset, String title) {
	        
	        JFreeChart chart = ChartFactory.createPieChart(
	        	title,  // chart title
	            dataset,             // data
	            true,               // include legend
	            true,
	            false
	        );

	        PiePlot plot = (PiePlot) chart.getPlot();
	        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
	        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
	                "{0} ({1} Dev.) {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
	            ));
	        plot.setNoDataMessage("No data available");
	        plot.setCircular(false);
	        plot.setLabelGap(0.02);
	        plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator(
	                "{0} : {1}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
	        return chart;
	        
	    }
	    
	    
	    public static JPanel createPanel(PieDataset dataset, String title) {
	        JFreeChart chart = create2DChart(dataset, title);
	        return new ChartPanel(chart);
	    }
}
