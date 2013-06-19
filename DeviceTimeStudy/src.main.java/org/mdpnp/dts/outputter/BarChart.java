package org.mdpnp.dts.outputter;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.TextAnchor;
import org.mdpnp.dts.utils.UtilsDTS;

/**
 * 
 * @author dalonso@mdpnp.org
 * This class implements Bar Chart Graphics with the JFreechart Library
 *
 */
public class BarChart extends ApplicationFrame{
	
	private static final long serialVersionUID = 1L;

	private CategoryDataset dataset;
	//TODO Add chart dimension attributes width and height and setters
	
	//cons
	public BarChart(String title) {
		super(title);
	}
	  
	//setters  	  
	public void setDataset(CategoryDataset dataset){
		this.dataset = dataset;
	}

	/**
	 * Prints a bar chart
	 * @param chartTitle the chart title
     * @param categoryLabel Label for the domain (horizontal) axis label (categories)
     * @param rangeLabel Label for the range (vertical) axis label (Values)
	 */
	public void printMultipleBarChart(String chartTitle, String categoryLabel, String valuesLabel){
		final JFreeChart chart = createChart(dataset, chartTitle, categoryLabel, valuesLabel);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel); 
	  }
	
	/**
	 * Prints a two bar chart
	 * @param chartTitle the chart title
     * @param categoryLabel Label for the domain (horizontal) axis label (categories)
     * @param rangeLabel Label for the range (vertical) axis label (Values)
	 */
	public void print2BarChart(String chartTitle, String categoryLabel, String rangeLabel){
        final JFreeChart chart = createChart2BarChart(dataset, chartTitle, categoryLabel, rangeLabel);
        // add the chart to a panel
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
	}
	
	/**
	 * Prints a two bar chart. The Y axis is a displays time in a HH:mm:ss manner
	 * @param chartTitle the chart title
     * @param categoryLabel Label for the domain (horizontal) axis label (categories)
     * @param rangeLabel Label for the range (vertical) axis label (Values)
	 */
	public void print2BarChartTimeYAxis(String chartTitle, String categoryLabel, String rangeLabel){
        final JFreeChart chart = createChart2BarChart(dataset, chartTitle, categoryLabel, rangeLabel);
        
        //Change Y axis display info to pretty printed time
        CategoryPlot plot = (CategoryPlot) chart.getPlot();        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(UtilsDTS.milisecTimeFomatter);
        
        //display info in the bars
        CategoryItemRenderer categoryrenderer = plot.getRenderer();
        categoryrenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
        for(int i=0; i< dataset.getRowCount();i++){
            //NOTE for StandardCategoryItemLabelGenerator: {X}refers to the info displayed in the dataset. {0} rowKey {1} columnKey {2} value. 
            categoryrenderer.setSeriesItemLabelGenerator(i, new StandardCategoryItemLabelGenerator("{2}",UtilsDTS.milisecTimeFomatter));
            categoryrenderer.setSeriesItemLabelsVisible(i,true);
        }
        chart.getCategoryPlot().setRenderer(categoryrenderer);
        
        // add the chart to a panel
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
	}
	
	 /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset.
     * @param chartTitle the chart title
     * @param categoryLabel Label for the domain (horizontal) axis label (categories)
     * @param rangeLabel Label for the range (vertical) axis label (Values)
     * @return The chart.
     */
    private JFreeChart createChart2BarChart(final CategoryDataset dataset, String chartTitle, String categoryLabel, String rangeLabel) {
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
        	chartTitle,         // chart title
        	categoryLabel,               // domain axis label (categories)
        	rangeLabel,                  // range axis label  (Values)
            dataset,                  // data
            PlotOrientation.VERTICAL,
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        //OPTIONAL CUSTOMISATION OF THE CHART

        // set the background color for the chart...
        chart.setBackgroundPaint(new Color(0xBBBBDD));

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        
        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        
        // set up gradient paints for series...
        final GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, Color.yellow, 
            0.0f, 0.0f, Color.lightGray
        );
        final GradientPaint gp1 = new GradientPaint(
            0.0f, 0.0f, Color.green, 
            0.0f, 0.0f, Color.lightGray
        );
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        
        // OPTIONAL CUSTOMISATION COMPLETED.
        
        return chart;
        
    }

	  

	    
	    /**
	     * Creates a multibar chart.
	     * 
	     * @param dataset  the dataset.
	     * @param chartTitle title shown on the chart
	     * @param categoryLabel domain or horizontal axis label
	     * @param valuesLabel range or vertical axis label
	     * @return a sample chart.
	     */
	    private JFreeChart createChart(final CategoryDataset dataset, String chartTitle, String categoryLabel, String valuesLabel) {

	        final JFreeChart chart = ChartFactory.createBarChart(
	        	chartTitle,       // chart title
	            categoryLabel,               // domain axis label
	            valuesLabel,                  // range axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL, // the plot orientation
	            false,                    // include legend
	            true,
	            false
	        );

	        chart.setBackgroundPaint(Color.lightGray);

	        // get a reference to the plot for further customisation...
	        final CategoryPlot plot = chart.getCategoryPlot();
	        plot.setNoDataMessage("NO DATA!");

	        final CategoryItemRenderer renderer = new CustomRenderer(
	            new Paint[] {Color.red, Color.blue, Color.green,
	                Color.yellow, Color.orange, Color.cyan,
	                Color.magenta, Color.blue}
	        );
//	        renderer.setLabelGenerator(new StandardCategoryLabelGenerator());
	        renderer.setItemLabelsVisible(true);
	        final ItemLabelPosition p = new ItemLabelPosition(
	            ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 45.0
	        );
	        renderer.setPositiveItemLabelPosition(p);
	        plot.setRenderer(renderer);

	        // change the margin at the top of the range axis...
	        final ValueAxis rangeAxis = plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setLowerMargin(0.15);
	        rangeAxis.setUpperMargin(0.15);

	        return chart;

	    }
	    
	    /**
	     * A custom renderer that returns a different color for each item in a single series.
	     */
	    class CustomRenderer extends BarRenderer {

	        /** The colors. */
	        private Paint[] colors;

	        /**
	         * Creates a new renderer.
	         *
	         * @param colors  the colors.
	         */
	        public CustomRenderer(final Paint[] colors) {
	            this.colors = colors;
	        }

	        /**
	         * Returns the paint for an item.  Overrides the default behaviour inherited from
	         * AbstractSeriesRenderer.
	         *
	         * @param row  the series.
	         * @param column  the category.
	         *
	         * @return The item color.
	         */
	        public Paint getItemPaint(final int row, final int column) {
	            return this.colors[column % this.colors.length];
	        }
	    }
	    

}
