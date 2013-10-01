package org.mdpnp.dts.statistics;

import java.util.ArrayList;
import java.util.List;

import org.mdpnp.dts.utils.UtilsDTS;

/**
 * @author dalonso@mdpnp.org
 * <p>
 * Implements basic functionality to get statistics of the offsets.<p>
 * These would be offset average and Standard deviation for the regular (basic) case, 
 *  the "New Minute" case (clock doesn't display seconds, and we assume we just change to this minute XX:00)
 *  the "New Minute Eve" case (clock doesn't display seconds, and we assume is about to change to the next minute XX:59)
 *  <p>
 *  XXX Offsets are calculated always in MILISECONDS, for consistency w/ Date.getTime() 
 * <p>
 * XXX Note about the Standard Deviation:
 * The standard deviation is equal to the square root of the variance.
 * The following formula is valid only if the N values we use in our calculations form the complete population.
 * If the values instead were a random sample drawn from some larger parent population, then we would have divided by (N - 1) 
 * instead of N in the denominator of the last formula, and then the quantity thus obtained would be called the sample standard deviation.
 *
 */
public class OffsetStatisticsImpl implements OffsetStatistics {
	
	//
	private List<Long> offsetList; //regular offset list
	private List<Long> offsetNewMinute; //list of offset considering new minute (XX:00)
	private List<Long> offsetNewMinuteEve; //list of offset considering new minute eve (XX:59)
	private List<Long> offsetWorstCaseScenario;//when the offset is maximun between newMinute and NewMinuteEve
	private List<Long> offsetBestCaseScenario;//we assume offset is the minimun between the NeMinuteScenario and the NewMinuteEveScenario
	
	
	private long totalOffset; //total value of regular offset
	private long totalOffsetNewMinute; //total value of offset for new minute comparison 
	private long totalOffsetNewMinuteEve; //total value of offset for new minute eve comparison
	private long totalOffsetWorstCaseScenario;//
	private long totalOffsetBestCaseScenario;//
	
	private long maxOffset;//maximal regular offset 
	private long maxOffsetNewMinute; //maximal offset for new minute comparison	
	private long maxOffsetNewMinuteEve; //maximal offset for new minute eve comparison
	private long maxOffsetWorstCaseScenario; //maximal offset value for the worst case scenario
	private long maxOffsetBestCaseScenario;//maximal value for the best case scenario
	
	private long minOffset;//minimal regular offset 
	private long minOffsetNewMinute; //minimal offset for new minute comparison	
	private long minOffsetNewMinuteEve; //minimal offset for new minute eve comparison
	private long minOffsetWorstCaseScenario; //minimal offset value for the worst case scenario
	private long minOffsetBestCaseScenario;//minimal value for the best case scenario
	
	//cons
	public OffsetStatisticsImpl(){
		offsetList = new ArrayList<Long>();
		offsetNewMinute = new ArrayList<Long>();
		offsetNewMinuteEve = new ArrayList<Long>();
		offsetWorstCaseScenario = new ArrayList<Long>();
		offsetBestCaseScenario = new ArrayList<Long>();
				
		totalOffset = totalOffsetNewMinute = totalOffsetNewMinuteEve = totalOffsetWorstCaseScenario = totalOffsetBestCaseScenario = 0; //init totals 
		maxOffset = maxOffsetNewMinute = maxOffsetNewMinuteEve = maxOffsetWorstCaseScenario = maxOffsetBestCaseScenario = Long.MIN_VALUE; //init maximums
		minOffset = minOffsetNewMinute = minOffsetNewMinuteEve = minOffsetWorstCaseScenario = minOffsetBestCaseScenario = Long.MAX_VALUE; //init minimuns
	}
	
//getters and setters
	/**
	 * Returns the amount of items studied. 
	 * NOTE: this number should be the same for the three different time scenarios
	 */
	public int getCount() {
		return offsetList.size();
	}

	/**
	 * Returns the regular average
	 */
	public double getAvgOffset() {
		if(offsetList.size()>0)
			return totalOffset/offsetList.size();
		else
			return 0;
	}

	/**
	 * Returns the offset average for the "New Minute" scenario
	 */
	public double getAvgOffsetNewMinute() {
		if (offsetNewMinute.size() >0)
			return totalOffsetNewMinute/offsetNewMinute.size();
		else
			return 0;
	}

	/**
	 * Returns the offset average for the "New minute Eve" Scenario
	 */
	public double getAvgOffsetNewMinuteEve() {
		if(offsetNewMinuteEve.size() >0)
			return totalOffsetNewMinuteEve/offsetNewMinuteEve.size();
		else
			return 0;
			
	}
	
	/**
	 * Returns the offset average for the "Worst Case" scenario
	 */
	public double getAvgOffsetWorstCaseScenario() {
		if (offsetWorstCaseScenario.size() >0)
			return totalOffsetWorstCaseScenario/offsetWorstCaseScenario.size();
		else
			return 0;
	}

	/**
	 * Returns the offset average for the "Best Case" Scenario
	 */
	public double getAvgOffsetBestCaseScenario() {
		if(offsetBestCaseScenario.size() >0)
			return totalOffsetBestCaseScenario/offsetBestCaseScenario.size();
		else
			return 0;
			
	}


	/**
	 * Returns the regular standard deviation
	 */
	public double getStdDev() {
		if(getCount()==0)
			return 0;
		long total = 0;
		double avg = getAvgOffset();
		for(Long l : offsetList){
			total+= Math.pow(l-avg, 2);
		}
		return Math.sqrt(total/getCount());
	}
	
	/**
	 * Returns the standard deviation for the "New Minute" scenario
	 */
	public double getStdDevNewMinute() {
		if(getCount()==0)
			return 0;
		long total = 0;
		double avg = getAvgOffsetNewMinute();
		for(Long l : offsetNewMinute){
			total+= Math.pow(l-avg, 2);
		}
		return Math.sqrt(total/getCount());
	}
	
	/**
	 * Returns the standard deviation for the "New Minute Eve" scenario
	 */
	public double getStdDevNewMinuteEve() {
		if(getCount()==0)
			return 0;
		long total = 0;
		double avg = getAvgOffsetNewMinuteEve();
		for(Long l : offsetNewMinuteEve){
			total+= Math.pow(l-avg, 2);
		}
		return Math.sqrt(total/getCount());
	}
	
	/**
	 * Returns the standard deviation for the "Worst Case" scenario
	 */
	public double getStdDevWorstCaseScenario() {
		if(getCount()==0)
			return 0;
		long total = 0;
		double avg = getAvgOffsetWorstCaseScenario();
		for(Long l : offsetWorstCaseScenario){
			total+= Math.pow(l-avg, 2);
		}
		return Math.sqrt(total/getCount());
	}
	
	/**
	 * Returns the standard deviation for the "Best Case" scenario
	 */
	public double getStdDevBestCaseScenario() {
		if(getCount()==0)
			return 0;
		long total = 0;
		double avg = getAvgOffsetBestCaseScenario();
		for(Long l : offsetBestCaseScenario){
			total+= Math.pow(l-avg, 2);
		}
		return Math.sqrt(total/getCount());
	}

	/**
	 * Minimal regular offset
	 */
	public long getMinOffset() {
		return minOffset;
	}

	/**
	 * Maximum regular offset
	 */
	public long getMaxOffset() {
		return maxOffset;
	}
	
	/**
	 * Minimal "New Minute" offset
	 */
	public long getMinOffsetNewMin() {
		return minOffsetNewMinute;
	}

	/**
	 * Maximum "New Minute" offset
	 */
	public long getMaxOffsetNewMin() {
		return maxOffsetNewMinute;
	}
	
	/**
	 * Minimal "New Minute Eve" offset
	 */
	public long getMinOffsetNewMinEve() {
		return minOffsetNewMinuteEve;
	}

	/**
	 * Maximum "New Minute Eve" offset
	 */
	public long getMaxOffsetNewMinEve() {
		return maxOffsetNewMinuteEve;
	}
	
	/**
	 * Minimal "Worst Case Sceanrio" offset
	 */
	public long getMinOffsetWorstCaseScenario() {
		return minOffsetWorstCaseScenario;
	}

	/**
	 * Maximum "New Minute Eve" offset
	 */
	public long getMaxOffWorstCaseScenario() {
		return maxOffsetWorstCaseScenario;
	}
	
	/**
	 * Minimal "Best Case Scenario" offset
	 */
	public long getMinOffsetBestCaseScenario() {
		return minOffsetBestCaseScenario;
	}

	/**
	 * Maximum "New Minute Eve" offset
	 */
	public long getMaxOffsetBestCaseScenario() {
		return maxOffsetBestCaseScenario;
	}


	/**
	 * Adds and offset to the list
	 * @param offset miliseconds
	 */
	public void addOffset(long offset){
		offsetList.add(offset);
		totalOffset += offset;
		maxOffset = maxOffset>offset? maxOffset : offset;
		minOffset = minOffset<offset? minOffset : offset;	
	}
	
	/**
	 * Adds and offset to the New Minute offset list
	 * @param offset miliseconds
	 */
	public void addOffsetNewMinute(long offset){
		offsetNewMinute.add(offset);
		totalOffsetNewMinute += offset;
		maxOffsetNewMinute = maxOffsetNewMinute>offset? maxOffsetNewMinute : offset;
		minOffsetNewMinute = minOffsetNewMinute<offset? minOffsetNewMinute : offset;		
	}
	
	/**
	 * Adds and offset to the New Minute Eve offset list
	 * @param offset miliseconds
	 */
	public void addOffsetNewMinuteEve(long offset){
		offsetNewMinuteEve.add(offset);
		totalOffsetNewMinuteEve += offset;
		maxOffsetNewMinuteEve = maxOffsetNewMinuteEve>offset? maxOffsetNewMinuteEve : offset;
		minOffsetNewMinuteEve = minOffsetNewMinuteEve<offset? minOffsetNewMinuteEve : offset;		
	}
	
	/**
	 * Adds and offset to the Worst Case Scenario offset list
	 * @param offset miliseconds
	 */
	public void addOffsetWorstCaseScenario(long offset){
		offsetWorstCaseScenario.add(offset);
		totalOffsetWorstCaseScenario += offset;
		maxOffsetWorstCaseScenario = maxOffsetWorstCaseScenario>offset ? maxOffsetWorstCaseScenario : offset;
		minOffsetWorstCaseScenario = minOffsetWorstCaseScenario<offset ? minOffsetWorstCaseScenario: offset;		
	}
	
	/**
	 * Adds and offset to the Best Case Scenario offset list
	 * @param offset miliseconds
	 */
	public void addOffsetBestCaseScenario(long offset){
		offsetBestCaseScenario.add(offset);
		totalOffsetBestCaseScenario += offset;
		maxOffsetBestCaseScenario = maxOffsetBestCaseScenario>offset ? maxOffsetBestCaseScenario : offset;
		minOffsetBestCaseScenario = minOffsetBestCaseScenario<offset ? minOffsetBestCaseScenario : offset;		
	}
	
	/**
	 * Calculates the offset between two dates and adds it to the proper offset list structures. <p>
	 * If (NOT displaysSeconds) it calculates and adds the offsets for 'New Minute' and 'New Minute Eve' scenarios.
	 * @param date1
	 * @param date2
	 * @param displaysSeconds
	 */
	public void addOffset(String cameraTime, String MedDeviceTime, boolean displaysSeconds){
		//1. Calculate offset between dates
		long offset = UtilsDTS.getOffsetFromDates(cameraTime, MedDeviceTime);
		long auxOffsetNewMinute;
		long auxOffsetNewMinuteEve;
		long auxOffsetWorstCaseScenario;
		long auxOffsetBestCaseScenario;
		addOffset(offset);
		//2. a) if the M.device displays seconds, offset is the same for all the scenarios
		if(displaysSeconds){
			addOffsetNewMinute(offset);
			addOffsetNewMinuteEve(offset);
			addOffsetBestCaseScenario(offset);
			addOffsetWorstCaseScenario(offset);
		}else{
			//2. b) if not, we need to calculate the offset for the new Minute & New Minute Eve scenarios
			String newMinuteScn = UtilsDTS.getNewMinuteDate(MedDeviceTime);
			auxOffsetNewMinute = UtilsDTS.getOffsetFromDates(cameraTime, newMinuteScn);
			addOffsetNewMinute(auxOffsetNewMinute);
			
			String newMinuteEveScn = UtilsDTS.getNewMinuteEveDate(MedDeviceTime);
			auxOffsetNewMinuteEve = UtilsDTS.getOffsetFromDates(cameraTime, newMinuteEveScn);
			addOffsetNewMinuteEve(auxOffsetNewMinuteEve);	
			
			auxOffsetWorstCaseScenario = Math.max(auxOffsetNewMinute, auxOffsetNewMinuteEve);
			addOffsetWorstCaseScenario(auxOffsetWorstCaseScenario);
			
			//if medical device and reference time (camera) are in the same day, hour and minute, we assume seconds so be so too, and offset is 0
			// if not, the best case offset will be the minimun between the new minute and new minute eve.
			
			auxOffsetBestCaseScenario = UtilsDTS.isSameMinute(MedDeviceTime, cameraTime) ? 0 : Math.min(auxOffsetNewMinute, auxOffsetNewMinuteEve);
			addOffsetBestCaseScenario(auxOffsetBestCaseScenario);
			
		}		
	}

}
