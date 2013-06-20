package org.mdpnp.dts.launcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RefineryUtilities;
import org.mdpnp.dts.data.DTSdata;
import org.mdpnp.dts.outputter.BarChart;
import org.mdpnp.dts.outputter.PieChart;
import org.mdpnp.dts.reader.Reader;
import org.mdpnp.dts.statistics.OffsetStatisticsImpl;
import org.mdpnp.dts.utils.UtilsDTS;



public class Main {

	//statistics structures
	static List<DTSdata> myDataList; //list of data read from origin file
	static Hashtable byDeviceType; //HT key: (String) device type; value (OffsetStatisticsImpl) statistics for this device type
	static Hashtable byConnection; //HT Key: (String) Networked/Standalone; value (OffsetStatisticsImpl) object w/ statistics
	static Hashtable byThresholdRange;//HT Key threshold range ; value (OffsetStatisticsImpl) object w/ statistics
	
	static int totalLinesRead = 0;//useful lines, Headers don't count
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// test purpose only
		Date initDate, endDate;// dates for performance control
		Date initDateCalc, endDateCalc;// dates for performance control
		
		//TODO path and file name shoud be read from args parameter
		String sFilename = "testData1.csv";
		sFilename = "DST_rawData.csv";
		/**
		 * 1 Create reader object (org.mdpnp.dts.reader.Reader) and read data file.
		 * This will populate our structures for statistics (we get this info via Reader getter methods).
		 * 
		 * 2 With our statistics structures, we populate the datasets we will need to feed the chart printers.
		 * These printers are implementer with jFreeChart Library.
		 * Each different statistics type is done in an auxiliary method
		 * (check http://www.java2s.com/Code/Java/Chart/CatalogChart.htm for more info about charts)
		 * 
		 */
		
		Reader myReader = new Reader("C:\\Users\\da792\\Documents\\Device Time Study Package\\Diego", sFilename, Reader.SEP_PIPE, 5);
		initDate = new Date();
		myReader.readFile();
		endDate = new Date();
		
		initDateCalc = new Date();
		//GET THE DATA
		myDataList = myReader.getDataList();
		byDeviceType = myReader.getStatsByDeviceType();
		byConnection = myReader.getStatsByConnection();
		byThresholdRange = myReader.getByThresholdRange();
//		totalLinesRead = myReader.geLinesReaded()==0?myReader.geLinesReaded():1;

		//GENERATE THE CHARTS
		//Statistics by Device Type
		printStatsByDeviceType();
		System.out.println("*********************");
		
		//Statistics by connection type: Networked Vs. Stand-Alone
		printStatsByConnectionType();
		
		//statistics by threshold Type
		printStatsByThreshold();
               
		endDateCalc = new Date();		
		System.out.println("Done!!");
		System.out.println("Time to read data " +UtilsDTS.getTimeDiff(initDate, endDate));
		System.out.println("Time to calculate data " +UtilsDTS.getTimeDiff(initDateCalc, endDateCalc));

	}
	
	//*********************************************************************************************
	
	/**
	 * prints statistics charts according to the device type
	 */
	private static void printStatsByDeviceType(){
		List<String> auxSortedList = new ArrayList<String>(byDeviceType.keySet());
		Collections.sort(auxSortedList);
		for(String key : auxSortedList){
			OffsetStatisticsImpl value = (OffsetStatisticsImpl)byDeviceType.get(key);
			System.out.println(key+" #Count: "+value.getCount() +", AVG Offset: "+UtilsDTS.getTimeFrom(Math.round(value.getAvgOffset())) +
					" STD Dev "+UtilsDTS.getTimeFrom(Math.round(value.getStdDev())) +
					" max Offset "+UtilsDTS.getTimeFrom(Math.round(value.getMaxOffset())));
			//populate data set
			//TODO feed dataset to LineChart and draw graphics
		}
	}
	
	/**
	 * prints statistics charts according to the connection type (Networked vs Standalone)
	 */
	private static void printStatsByConnectionType(){
		//create empty dataset
		final DefaultCategoryDataset datasetNumDevices = new DefaultCategoryDataset();
		final DefaultCategoryDataset datasetAvgOffset = new DefaultCategoryDataset();
		final DefaultCategoryDataset datasetStdDev = new DefaultCategoryDataset();
		
		List<String> auxConnSortedList = new ArrayList<String>(byConnection.keySet());
		Collections.sort(auxConnSortedList);
		for(String key : auxConnSortedList){
			OffsetStatisticsImpl value = (OffsetStatisticsImpl)byConnection.get(key);
			
			//print data to default console output
			System.out.println(key+" #Count: "+value.getCount() +", AVG Offset: "+UtilsDTS.getTimeFrom(Math.round(value.getAvgOffset())) +
					" STD Dev "+UtilsDTS.getTimeFrom(Math.round(value.getStdDev())) +
					" max Offset "+UtilsDTS.getTimeFrom(Math.round(value.getMaxOffset())));
			
			//populate dataset
			datasetNumDevices.addValue(value.getCount(), key, "# of devices");
			datasetAvgOffset.addValue(value.getAvgOffset(), key, "Avg Offset");
			datasetStdDev.addValue(value.getStdDev(), key, "STD Dev");
			
		}
        
		final BarChart barChart = new BarChart("Networked Vs. Standalone");
		barChart.setDataset(datasetNumDevices);
		barChart.print2BarChart("Networked Vs. Standalone", "Type", "Number of devices");
		barChart.pack();
        RefineryUtilities.centerFrameOnScreen(barChart);
        barChart.setVisible(true);
        
        final BarChart barChartAvg = new BarChart("Networked Vs. Standalone");
        barChartAvg.setDataset(datasetAvgOffset);
        barChartAvg.print2BarChartTimeYAxis("Networked Vs. Standalone", "Type", "Offset Time");
        barChartAvg.pack();
        RefineryUtilities.positionFrameRandomly(barChartAvg);
//        RefineryUtilities.centerFrameOnScreen(barChart);
        barChartAvg.setVisible(true);
        
        final BarChart barChartStd = new BarChart("Networked Vs. Standalone");
        barChartStd.setDataset(datasetStdDev);
        barChartStd.print2BarChartTimeYAxis("Networked Vs. Standalone", "Type", "Offset Time");
        barChartStd.pack();
        RefineryUtilities.positionFrameRandomly(barChartStd);
//        RefineryUtilities.centerFrameOnScreen(barChart);
        barChartStd.setVisible(true);
        
	}
	
	/**
	 * prints statistics by threshold range
	 */
	private static void printStatsByThreshold(){
		DefaultPieDataset dataset = new DefaultPieDataset();
		for(String key : DTSdata.thresholdCategories){
			OffsetStatisticsImpl value = (OffsetStatisticsImpl)byThresholdRange.get(key);
			System.out.println(key+" #Count: "+value.getCount());
			//populate data set
			dataset.setValue(key, value.getCount());
		}
		
		PieChart offsetPieChart = new PieChart("Offset Summary");
		offsetPieChart.setDataset(dataset);
		offsetPieChart.print2DPieChart("Offset Summary");
		offsetPieChart.pack();
        RefineryUtilities.centerFrameOnScreen(offsetPieChart);
        offsetPieChart.setVisible(true);
	}

}
