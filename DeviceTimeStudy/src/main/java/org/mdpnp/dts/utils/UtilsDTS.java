package org.mdpnp.dts.utils;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * 
 * @author dalonso@mdpnp.org
 *	This class has utility functions for the project, such as format conversions,
 *	pretty printing of times, etc.
 */
public class UtilsDTS {
	
	/**
	 * Default mask for time HH:mm:ss (NOTE HH means 24h, opposed to hh that is 12h) 
	 */
	public static final String DEFAULT_TIME_MASK = "HH:mm:ss";
	
	public static SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_TIME_MASK);
	
	/**
	 * Parses a string date with the given format (mask) into miliseconds 
	 * @param sDate
	 * @param mask Date fotmat. By default DEFAULT_TIME_MASK
	 * @return milisecons
	 */
	public static long parseDateFormatToLong(String sDate, String mask) {
		if(mask==null || mask.trim().equals(""))
			mask = DEFAULT_TIME_MASK;
		SimpleDateFormat sdf = new SimpleDateFormat(mask);
		try{
			Date date = sdf.parse(sDate);
			return date.getTime();
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Parses a date with format DEFAULT_TIME_MASK (HH:mm:ss) and
	 * returns the amount of miliseconds
	 * @param sDate
	 * @return miliseconds
	 */
	public static long parseDateFormatToLong(String sDate) {
		StringTokenizer st = new StringTokenizer(sDate, ":");
		try{
			long time =0;
			time += Long.parseLong(st.nextToken())*60;// hours
			time = (time + Long.parseLong(st.nextToken()) )*60;// minutes
			time += Long.parseLong(st.nextToken());// hours
			return time*1000;//miliseconds
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Returns a pretty printing string w/ the hours, minutes and seconds
	 * between two dates
	 * @param initDate
	 * @param endDate
	 * @return
	 */
	public static String getTimeDiff(Date initDate, Date endDate){
  	  long secs = (endDate.getTime() - initDate.getTime()) / 1000;
  	  int hours = (int)secs / 3600;    
  	  secs = secs % 3600;
  	  int mins = (int)secs / 60;
  	  secs = secs % 60;
  	  return new String("hours "+hours+", min "+mins+", secs "+secs);
	}
	
	/***
	 * Returns a string w/ the pretty printing of hours, minutes and seconds
	 * of an amount of miliseconds 
	 * @param milisec
	 * @return
	 */
	public static String getTimeFrom(long milisec){
		String outTime= "";
	  	  long secs = milisec / 1000;
	  	  int hours = (int)secs / 3600; 
	  	  if(hours>0) outTime = hours+" hours, ";//if ohours, we don't display Hours
	  	  secs = secs % 3600;
	  	  int mins = (int)secs / 60;
	  	  secs = secs % 60;
//	  	  return new String(hours+" hours, "+mins+" min. "+secs+" secs.");
	  	 return outTime+mins+" min. "+secs+" secs.";
		}

	/**
	 * Returns the offset of two dates as long in miliseconds.
	 * 1. Parse string into dates
	 * 2. Check which date is higher (older) and subtract them to obtain the offset
 	 * @param arg1 Date1 
	 * @param arg2 Date2
	 * 
	 */
	public static long getOffsetFromDates(String arg1, String arg2){
		
		long miliSec =0;
		try{
			Date date1 = sdf.parse(arg1);
			Date date2 = sdf.parse(arg2);
			if(date1.compareTo(date2) > 0){
				miliSec = date1.getTime() - date2.getTime();
			}else{
				miliSec = date2.getTime() - date1.getTime();
			}
			return miliSec;
		}catch(Exception e){
			e.printStackTrace();
			return miliSec;//XXX Not best practice
		}
	}
	
	/**
	 * Returns a date with the New Minute Scenario mask
	 * @param date as "HH:mm:ss"
	 * @return date as string as "HH:mm:00" 
	 */
	public static String getNewMinuteDate(String date){
		return date.substring(0, 6)+"00";	
	}
	
	/**
	 * Returns a date with the New Minute Eve Scenario mask
	 * @param date as "HH:mm:ss"
	 * @return date as string as "HH:mm:59" 
	 */
	public static String getNewMinuteEveDate(String date){
		return date.substring(0, 6)+"59";	
	}
	
	/**
	 * NumberFormatter for more pretty printing
	 */
    public static final NumberFormat milisecTimeFomatter = new NumberFormat(){

        public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
//            return new StringBuffer(String.format("%f", number));
        	return new StringBuffer(UtilsDTS.getTimeFrom(Math.round(number)));
        }

        public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
//            return new StringBuffer(String.format("%f", number));
        	return new StringBuffer(UtilsDTS.getTimeFrom(number));
        }

        public Number parse(String source, ParsePosition parsePosition) {
            return null;
        }
    };
    

}
