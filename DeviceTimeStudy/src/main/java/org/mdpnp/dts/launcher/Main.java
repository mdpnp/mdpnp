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
import org.mdpnp.dts.outputter.LineChart;
import org.mdpnp.dts.outputter.PieChart;
import org.mdpnp.dts.reader.Reader;
import org.mdpnp.dts.statistics.OffsetStatisticsImpl;
import org.mdpnp.dts.utils.UtilsDTS;

import com.sun.org.apache.bcel.internal.generic.DSTORE;



public class Main {

	//statistics structures
	static List<DTSdata> myDataList; //list of data read from origin file
	static Hashtable byDeviceType; //HT key: (String) device type; value (OffsetStatisticsImpl) statistics for this device type
	static Hashtable byConnection; //HT Key: (String) Networked/Standalone; value (OffsetStatisticsImpl) object w/ statistics
	static Hashtable byThresholdRange;//HT Key threshold range ; value (OffsetStatisticsImpl) object w/ statistics
	static Hashtable hospitalsByCategory = new Hashtable<>();//Each hospital by Threshold category
	//HT Key:name of hospital, Val: another HT categories
	//sub HT Categories Key: threshold categories, val Object to calculate statistics
	//FIXME inject these dependencies to the Reader object to populate
	
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
		hospitalsByCategory =myReader.getHospitalsByCategory();

		//GENERATE THE CHARTS
		//Statistics by Device Type
//		printStatsByDeviceType();
//		System.out.println("*********************");
//		
//		//Statistics by connection type: Networked Vs. Stand-Alone
//		printStatsByConnectionType();
//		
//		//statistics by threshold Type
//		printStatsByThreshold();
		
		//statistics for each hospital by threshold range
		printStatsForHospitalByThreshold();
               
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
	
	
	private static void printStatsForHospitalByThreshold(){
		
		// create the dataset
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		//dataset.addValue(value, seriesi=hospital, typej=threshol cat);
		
		//poulate data set
		List<String> hospitalSortedList = new ArrayList<String>(hospitalsByCategory.keySet());
		Collections.sort(hospitalSortedList);
		Collections.reverse(hospitalSortedList);
		/**
		 * Why this reverse order? For practical reasons. Although JFreeChart dataset.addValue(val, series-i, type-j)
		 *  method expects a Comparable ColumnKey, there is no practical way to sort the values in the X-Axis (Categories) 
		 *  and this is defined by the data set received. This means, that if the first series doesn't have all the Categories
		 *  (or sorts them in a random order) the will be displayed this way. This happens with our first hospital studied, so 
		 *  we use the reverse order to get all the Categories in alphabetical order.
		 */
		for(String insitutuionKey : hospitalSortedList){
			Hashtable htThresholds =  (Hashtable) hospitalsByCategory.get(insitutuionKey);
//			List<String> thresholdsSortedList = new ArrayList<String>(htThresholds.keySet());
//			Collections.sort(thresholdsSortedList);
//			for(String thresholdKey : thresholdsSortedList){
//				OffsetStatisticsImpl data = (OffsetStatisticsImpl)htThresholds.get(thresholdKey);
//				dataset.addValue(data.getCount(), insitutuionKey, thresholdKey);
//			}
			String[] thresholds = DTSdata.thresholdCategories;
			for(String thresholdKey : thresholds){
				if(htThresholds.containsKey(thresholdKey)){
					OffsetStatisticsImpl data = (OffsetStatisticsImpl)htThresholds.get(thresholdKey);
					dataset.addValue(data.getCount(), insitutuionKey, thresholdKey);
					System.out.println("dataset.addValue("+data.getCount()+", "+insitutuionKey+", "+thresholdKey+");");
				}
			}
		}
				
		//create linear chart 
		LineChart lineChart = new LineChart("Devices");
		lineChart.setDataset(dataset);
		lineChart.print("Devices per hospital", "Thresholds", "# of devices");
		lineChart.pack();
		RefineryUtilities.centerFrameOnScreen(lineChart);
		lineChart.setVisible(true);

	}

}
