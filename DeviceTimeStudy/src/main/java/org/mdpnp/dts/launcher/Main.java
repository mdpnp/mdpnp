package org.mdpnp.dts.launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.print.DocFlavor.READER;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RefineryUtilities;
import org.mdpnp.dts.charts.BarChart;
import org.mdpnp.dts.charts.LineChart;
import org.mdpnp.dts.charts.PieChart;
import org.mdpnp.dts.data.DTSdata;
import org.mdpnp.dts.io.DTSFileWriter;
import org.mdpnp.dts.io.Reader;
import org.mdpnp.dts.statistics.OffsetStatisticsImpl;
import org.mdpnp.dts.utils.UtilsDTS;

import com.sun.org.apache.bcel.internal.generic.DSTORE;



public class Main {

	//statistics structures
	static List<DTSdata> myDataList; //list of data read from origin file
	static Hashtable byDeviceType; //HT key: (String) device type; value (OffsetStatisticsImpl) statistics for this device type
	static Hashtable byConnection; //HT Key: (String) Networked/Standalone; value (OffsetStatisticsImpl) object w/ statistics
	static Hashtable byThresholdRange;//HT Key threshold range ; value (OffsetStatisticsImpl) object w/ statistics
	static Hashtable byThresholdRangeBestCase;//HT Key threshold range ; value (OffsetStatisticsImpl) object w/ statistics
	static Hashtable byThresholdRangeWorstCase;//HT Key threshold range ; value (OffsetStatisticsImpl) object w/ statistics
	static Hashtable hospitalsByCategory = new Hashtable<>();//Each hospital by Threshold category
	//HT Key:name of hospital, Val: another HT categories
	//sub HT Categories Key: threshold categories, val Object to calculate statistics
	//FIXME inject these dependencies to the Reader object to populate
	
	static int totalLinesRead = 0;//useful lines, Headers don't count
	
	//FIXME path should be read from properties file or console args 
	static String path ="C:\\Users\\da792\\Documents\\Device Time Study Package\\Diego\\";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// test purpose only
		Date initDate, endDate;// dates for performance control
		Date initDateCalc, endDateCalc;// dates for performance control
		
		//TODO path and file name shoud be read from args parameter
//		String sFilename = "testData1.csv"; //test purposes
		String sFilename = "DST_rawData.csv";//overwrite RAW_DATA
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
		
		//Reader myReader = new Reader("C:\\Users\\da792\\Documents\\Device Time Study Package\\Diego", sFilename, Reader.SEP_PIPE, 5);
		final int DEFAULT_SKIPPED_LINES =5;//these lines are just headers
		Reader myReader = new Reader(path, sFilename, Reader.SEP_PIPE, DEFAULT_SKIPPED_LINES);
		initDate = new Date();
		myReader.readFile();
		endDate = new Date();
		
		initDateCalc = new Date();
		//GET THE DATA
		myDataList = myReader.getDataList();
		byDeviceType = myReader.getStatsByDeviceType();
		byConnection = myReader.getStatsByConnection();
		byThresholdRange = myReader.getByThresholdRange();
		byThresholdRangeBestCase = myReader.getByThresholdRangeBestCase();
		byThresholdRangeWorstCase = myReader.getByThresholdRangeWorstCase();
//		totalLinesRead = myReader.geLinesReaded()==0?myReader.geLinesReaded():1;
		hospitalsByCategory =myReader.getHospitalsByCategory();

		//GENERATE THE CHARTS - Statistics
		//Statistics by Device Type
//		printStatsByDeviceType();
		writeStatsByDeviceType(path, "byDeviceType.csv");
//		System.out.println("*********************");
//		
//		//Statistics by connection type: Networked Vs. Stand-Alone
//		printStatsByConnectionType();
		writeStatsByConnectionType(path+"\\new\\", "byConnection.csv");
//		
//		//statistics by threshold Type
//		printStatsByThreshold();
		writeStatsByThreshold(path+"\\new\\", "byThreshold.csv");
		
		//statistics for each hospital by threshold range
//		printStatsForHospitalByThreshold();
//		writeStatsForHospitalByThreshold();//write to file
		writeStatsForHospitalByThreshold(path+"\\new\\", "dataForHospitalByThreshold.csv");
               
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
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		List<String> auxSortedList = new ArrayList<String>(byDeviceType.keySet());
		Collections.sort(auxSortedList);
		for(String key : auxSortedList){
			OffsetStatisticsImpl value = (OffsetStatisticsImpl)byDeviceType.get(key);
			System.out.println(key+" #Count: "+value.getCount() +", AVG Offset: "+UtilsDTS.getTimeFrom(Math.round(value.getAvgOffset())) +
					" STD Dev "+UtilsDTS.getTimeFrom(Math.round(value.getStdDev())) +
					" max Offset "+UtilsDTS.getTimeFrom(Math.round(value.getMaxOffset())));
			//populate data set
			//TODO feed dataset to LineChart and draw graphics values, Series, Category
			
			dataset.addValue(Math.round(value.getAvgOffset()), key, "Avg Offset");
			dataset.addValue(Math.round(value.getStdDev()), key, "STD Dev");
		}
		
		final BarChart barChart = new BarChart("Devices by Type");
		barChart.setDataset(dataset);
		barChart.printMultipleBarChartTimeAxis("Devices by Type", "", "");
		barChart.pack();
        RefineryUtilities.centerFrameOnScreen(barChart);
        barChart.setVisible(true);
	}
	
	/**
	 * prints statistics to file
	 * @param pathName
	 * @param fileName
	 */
	private static void writeStatsByDeviceType(String pathName, String fileName){
		DTSFileWriter fileWriter = new DTSFileWriter(pathName, fileName);
		String sep = Reader.SEP_PIPE;
		
		//fisrt row is columns header
		fileWriter.addRow(""+sep+"Count"+sep+"Avg. Offset"+sep+"STD Dev."+sep+"Max. Offset"+sep+"Min. Offset");
		
		List<String> auxSortedList = new ArrayList<String>(byDeviceType.keySet());
		Collections.sort(auxSortedList);
		for(String key : auxSortedList){
			OffsetStatisticsImpl value = (OffsetStatisticsImpl)byDeviceType.get(key);
			String row = key+sep+value.getCount()
					+sep+UtilsDTS.parseDateToStringFormat(Math.round(value.getAvgOffset()))
					+sep+UtilsDTS.parseDateToStringFormat(Math.round(value.getStdDev()))
					+sep+UtilsDTS.parseDateToStringFormat(Math.round(value.getMaxOffset()))
					+sep+UtilsDTS.parseDateToStringFormat(Math.round(value.getMinOffset()));
			fileWriter.addRow(row);
		}
		fileWriter.close();
	}
	
	/**
	 * prints statistics charts according to the connection type (Networked vs Standalone)
	 */
	private static void printStatsByConnectionType(){
		//create empty dataset
		final DefaultCategoryDataset datasetNumDevices = new DefaultCategoryDataset();
		final DefaultCategoryDataset datasetAvgOffset = new DefaultCategoryDataset();
		final DefaultCategoryDataset datasetStdDev = new DefaultCategoryDataset();
		final DefaultCategoryDataset datasetTriplet = new DefaultCategoryDataset();
		
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
			
			datasetTriplet.addValue(value.getAvgOffset(), "regular Offset", key);
			datasetTriplet.addValue(value.getAvgOffsetNewMinute(), "New Minute Scenario Offset", key);
			datasetTriplet.addValue(value.getAvgOffsetNewMinuteEve(), "New Minute Eve Scenario", key);
			
//			datasetTriplet.addValue(value.getAvgOffset(), key, "regular Offset");
//			datasetTriplet.addValue(value.getAvgOffset_newMinute(), key, "New Minute Scenario Offset");
//			datasetTriplet.addValue(value.getAvgOffset_NewMinuteEve(), key, "New Minute Eve Scenario");
			
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
        
        //Triplets
        final BarChart barChartTriplet = new BarChart("Networked Vs. Standalone");
        barChartTriplet.setDataset(datasetTriplet);
//        barChartStd.print2BarChartTimeYAxis("Networked Vs. Standalone", "Type", "Offset Time");
        barChartTriplet.printMultipleBarChartTimeAxis("Networked Vs. Standalone", "Type", "Offset Time");
        barChartTriplet.pack();
        RefineryUtilities.positionFrameRandomly(barChartTriplet);
//        RefineryUtilities.centerFrameOnScreen(barChart);
        barChartTriplet.setVisible(true);
        
	}
	
	private static void writeStatsByConnectionType(String pathName, String fileName){
		DTSFileWriter fileWriter = new DTSFileWriter(pathName, fileName);
		
		List<String> auxConnSortedList = new ArrayList<String>(byConnection.keySet());
		Collections.sort(auxConnSortedList);
		String row ="";
		String sep = Reader.SEP_PIPE;
		
		fileWriter.addRow(""+sep+"regular Offset"+sep+"Worst Case Scenario Offset"+sep+"Best Case Scenario Offset");
		
		for(String key : auxConnSortedList){
			OffsetStatisticsImpl value = (OffsetStatisticsImpl)byConnection.get(key);
//			row=key+sep+UtilsDTS.getTimeFrom(Math.round(value.getAvgOffset()))
//					+sep+UtilsDTS.getTimeFrom(Math.round(value.getAvgOffset_newMinute()))
//					+sep+UtilsDTS.getTimeFrom(Math.round(value.getAvgOffset_NewMinuteEve()));
			
			row=key+" Avg Offset"+sep+UtilsDTS.parseDateToStringFormat(Math.round(value.getAvgOffset()))
			+sep+ UtilsDTS.parseDateToStringFormat(Math.round(value.getAvgOffsetWorstCaseScenario()))
			+sep+ UtilsDTS.parseDateToStringFormat(Math.round(value.getAvgOffsetBestCaseScenario()));			
			fileWriter.addRow(row);
			
			row=key+" STD Dev"+sep+UtilsDTS.parseDateToStringFormat(Math.round(value.getStdDev()))
			+sep+ UtilsDTS.parseDateToStringFormat(Math.round(value.getStdDevWorstCaseScenario()))
			+sep+ UtilsDTS.parseDateToStringFormat(Math.round(value.getStdDevBestCaseScenario()));			
			fileWriter.addRow(row);
			
			System.out.println(key+" # devices "+value.getCount()+
					" max offset Best Case: "+ UtilsDTS.getTimeFrom(value.getMaxOffsetBestCaseScenario())+
					" max offset Worst Case"+ UtilsDTS.getTimeFrom(value.getMaxOffWorstCaseScenario()));
		}
		fileWriter.close();
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
	
	private static void writeStatsByThreshold(String pathName, String fileName){
		DTSFileWriter fileWriter = new DTSFileWriter(pathName, fileName);
		
		List<String> rangesList = new ArrayList<String>(byThresholdRange.keySet());

		String sep = Reader.SEP_PIPE;
		String title =""+sep;
		String rowBestCase ="best case"+sep;
		String rowWorstCase ="worst case"+sep;
		
		//write header line
		fileWriter.addRow(""+sep+"regular Offset"+sep+"Worst Case Scenario Offset"+sep+"Best Case Scenario Offset");
		
		for(String range: rangesList){
			title +=" "+range+sep;
			OffsetStatisticsImpl valueBC = (OffsetStatisticsImpl)byThresholdRangeBestCase.get(range);
			OffsetStatisticsImpl valueWC = (OffsetStatisticsImpl)byThresholdRangeWorstCase.get(range);
			rowBestCase += valueBC.getCount()+sep;
			rowWorstCase += valueWC.getCount()+sep;
			
		}
		//write to file
		fileWriter.addRow(title);
		fileWriter.addRow(rowBestCase);
		fileWriter.addRow(rowWorstCase);	
		fileWriter.close();
	}
	
	
	private static void printStatsForHospitalByThreshold(){		
		// create the dataset
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		//dataset.addValue(value, seriesi=hospital, typej=threshol cat);
		
		//populate data set
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
					//System.out.println("dataset.addValue("+data.getCount()+", "+insitutuionKey+", "+thresholdKey+");");//test purpose only
					
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
	
	/**
	 * Writes the information into a CSV file
	 * @deprecated
	 */
	private static void writeStatsForHospitalByThreshold(){
		//Add capability to write data into a file
		FileWriter fw = null;
		BufferedWriter bw = null;
		String outputFileName = "dataForHospitalByThreshold.csv";
		//Writer
		File file = new File(path+outputFileName);
		try{
			if (!file.exists()) {
				file.createNewFile();
			}
			
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			
			List<String> hospitalSortedList = new ArrayList<String>(hospitalsByCategory.keySet());
			Collections.sort(hospitalSortedList);
			
			//print columns headers
			String outputrow = ""+Reader.SEP_PIPE;
			for(String s : DTSdata.thresholdCategories){
				outputrow += s+Reader.SEP_PIPE;
			}
			bw.write(outputrow+"\n");	
			
			
			for(String insitutuionKey : hospitalSortedList){//Series Key
				Hashtable htThresholds =  (Hashtable) hospitalsByCategory.get(insitutuionKey);
				String[] thresholds = DTSdata.thresholdCategories;
				outputrow =  insitutuionKey+Reader.SEP_PIPE;
				for(String thresholdKey : thresholds){//Categories Key
					if(htThresholds.containsKey(thresholdKey)){
						OffsetStatisticsImpl data = (OffsetStatisticsImpl)htThresholds.get(thresholdKey);
						outputrow += data.getCount()+Reader.SEP_PIPE; //column 'thresholdKey'
						
					}else{
						outputrow += "0"+Reader.SEP_PIPE;
					}
				}
				//add row to file
				bw.write(outputrow+"\n");	
			}
			
			bw.flush();
			bw.close();
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void writeStatsForHospitalByThreshold(String pathName, String fileName){
		DTSFileWriter fileWriter = new DTSFileWriter(pathName, fileName);
		
		List<String> hospitalSortedList = new ArrayList<String>(hospitalsByCategory.keySet());
		Collections.sort(hospitalSortedList);
		
		//print columns headers
		String outputrow = ""+Reader.SEP_PIPE;
		for(String s : DTSdata.thresholdCategories){
			outputrow += s+Reader.SEP_PIPE;
		}	
		fileWriter.addRow(outputrow);
		
		
		for(String insitutuionKey : hospitalSortedList){//Series Key
			Hashtable htThresholds =  (Hashtable) hospitalsByCategory.get(insitutuionKey);
			String[] thresholds = DTSdata.thresholdCategories;
			outputrow =  insitutuionKey+Reader.SEP_PIPE;
			for(String thresholdKey : thresholds){//Categories Key
				if(htThresholds.containsKey(thresholdKey)){
					OffsetStatisticsImpl data = (OffsetStatisticsImpl)htThresholds.get(thresholdKey);
					outputrow += data.getCount()+Reader.SEP_PIPE; //column 'thresholdKey'
					
				}else{
					outputrow += "0"+Reader.SEP_PIPE;
				}
			}
			//add row to file
			fileWriter.addRow(outputrow);
		}
		fileWriter.close();
	}

}
