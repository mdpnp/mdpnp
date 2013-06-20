package org.mdpnp.dts.data;

import org.mdpnp.dts.utils.UtilsDTS;

/**
 * 
 * @author dalonso@mdpnp.org
 *
 *This class represents a row of raw data in the excel sheet
 */
public class DTSdata {
	
	//Name of the different thresholds we can have for the offsets
	public static String[] thresholdCategories ={"Offset less than 2 sec",
		"Offset more than 2 sec", "Offset more than 1 min", "Offset more than 5 min", "Offset more than 10 min",
		"Offset more than 15 min", "Offset more than 30 min", "Offset more than 1 hour", "Offset more than 2 hours"};
	
	//fields (as in Clock Study_Consolidated Data_v6_13Apr2013.xlsx)
	private int number; //ID number of the device assigned by MDPnP
	private String institution; //Name of Institution where the data was taken
	private String deviceLocation; //Device Department 
	private String deviceRoom; //Device Room
	private String deviceType; //Type of Device
	private String specification; //Device Manufacturer and Model number
	private String connection; //Is it connected to a network or stand-alone?
	//values Stand-Alone or Networked
    private String syncTime; //Does it sync its time over the network?
    //valuies yes-No-Unknown
    private String bioMedChanged; //Is is changed by Biomeds twice a year for DST change?
    //values yes-No-autosync
    private String pictureTaken;//"What device was used to capture the picture? 
    	//Camera/ iPhone/ Android"
    private String sNTP_referenceTime; //The actual  NTP reference time as displayed in picture captured by Camera/ iPhone/ Android before each reading.
    private String referenceEXIF; //The NTP reference time from the picture EXIF INFO/ Properties.
    private String cameraOffset; //Calculated Difference between Camera/ iPhone/ Android and NTP reference time.
    //(calculated as sNTP_referenceTime minus referenceEXIF fields)
    private String cameraErrorMargin; //Assume: Camera Error Margin: 1sec for Andriod/ iPhone & 2sec for Camera
    private String datePictureTaken; //The date on which picture was taken
    private String deviceTimeDisplayed; //The time displayed on the device
    private boolean displaysSeconds; // Does the device time display seconds or only minutes?
    private String deviceErrorMargin; //Assume: Device Error Margin: plus or minus 1sec
    private boolean currentlyOnDST; //Is the device currently on DST? (Only for BWH Data)
    private String deviceTimeCorrectedForDST; //If the device currently on DST, correct for DST by subtracting 1hour. (Only for BWH Data)
    private String deviceTime; //Calculate Device Time (Camera Error Margin+ Device Error Margin+ Device Time Displayed)
    private String EXIFTime; //Device Time from picture EXIF INFO/ Properties.
    private String correctedEXIFTime; //Correct the EXIF time of Device Picture by adding/subtracting the Camera Offset
    private String deviceOffset; //Calculate Device Offset = Corrected EXIF time (MINUS) Device Time
    private String absDeviceOffset; //Take absolute values of Device Offset (Convert Negative Values into Positive)
    private String offsetSign; //Note the Offset Sign of the Device Offset (Positive Offset or Negative Offset or No Offset)
    private boolean thresholdGT_2sec; //displays seconds, threshold is greater than 2 seconds
    private boolean thresholdGT_1min; //displays minutes, threshold is greater than ONE minute
    private boolean thresholdGT_5min; //displays minutes, threshold is greater than FIVE minutes
    private boolean thresholdGT_10min; //displays minutes, threshold is greater than 10 minutes
    private boolean thresholdGT_15min; //displays minutes, threshold is grater than 15 minutes
    private boolean thresholdGT_30min; //displays minutes, threshold is greater than 30 minute
    private boolean thresholdGT_1Hour; //displays minutes, threshold is greater than ONE HOUR
    private boolean thresholdGT_2Hour; //displays minutes, threshold is greater than TWO Hours
    private String threshold;//actual threshold value
    
    //cons
    public DTSdata(){
    	
    }

    //getters & setters
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getDeviceLocation() {
		return deviceLocation;
	}

	public void setDeviceLocation(String deviceLocation) {
		this.deviceLocation = deviceLocation;
	}

	public String getDeviceRoom() {
		return deviceRoom;
	}

	public void setDeviceRoom(String deviceRoom) {
		this.deviceRoom = deviceRoom;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getSyncTime() {
		return syncTime;
	}

	public void setSyncTime(String syncTime) {
		this.syncTime = syncTime;
	}

	public String getBioMedChanged() {
		return bioMedChanged;
	}

	public void setBioMedChanged(String bioMedChanged) {
		this.bioMedChanged = bioMedChanged;
	}

	public String getPictureTaken() {
		return pictureTaken;
	}

	public void setPictureTaken(String pictureTaken) {
		this.pictureTaken = pictureTaken;
	}

	public String getsNTP_referenceTime() {
		return sNTP_referenceTime;
	}

	public void setsNTP_referenceTime(String sNTP_referenceTime) {
		this.sNTP_referenceTime = sNTP_referenceTime;
	}

	public String getReferenceEXIF() {
		return referenceEXIF;
	}

	public void setReferenceEXIF(String referenceEXIF) {
		this.referenceEXIF = referenceEXIF;
	}

	public String getCameraOffset() {
		return cameraOffset;
	}

	public void setCameraOffset(String cameraOffset) {
		this.cameraOffset = cameraOffset;
	}

	public String getCameraErrorMargin() {
		return cameraErrorMargin;
	}

	public void setCameraErrorMargin(String cameraErrorMargin) {
		this.cameraErrorMargin = cameraErrorMargin;
	}

	public String getDatePictureTaken() {
		return datePictureTaken;
	}

	public void setDatePictureTaken(String datePictureTaken) {
		this.datePictureTaken = datePictureTaken;
	}

	public String getDeviceTimeDisplayed() {
		return deviceTimeDisplayed;
	}

	public void setDeviceTimeDisplayed(String deviceTimeDisplayed) {
		this.deviceTimeDisplayed = deviceTimeDisplayed;
	}

	public boolean isDisplaysSeconds() {
		return displaysSeconds;
	}

	public void setDisplaysSeconds(boolean displaysSeconds) {
		this.displaysSeconds = displaysSeconds;
	}

	public String getDeviceErrorMargin() {
		return deviceErrorMargin;
	}

	public void setDeviceErrorMargin(String deviceErrorMargin) {
		this.deviceErrorMargin = deviceErrorMargin;
	}

	public boolean isCurrentlyOnDST() {
		return currentlyOnDST;
	}

	public void setCurrentlyOnDST(boolean currentlyOnDST) {
		this.currentlyOnDST = currentlyOnDST;
	}

	public String getDeviceTimeCorrectedForDST() {
		return deviceTimeCorrectedForDST;
	}

	public void setDeviceTimeCorrectedForDST(String deviceTimeCorrectedForDST) {
		this.deviceTimeCorrectedForDST = deviceTimeCorrectedForDST;
	}

	public String getDeviceTime() {
		return deviceTime;
	}

	public void setDeviceTime(String deviceTime) {
		this.deviceTime = deviceTime;
	}

	public String getEXIFTime() {
		return EXIFTime;
	}

	public void setEXIFTime(String eXIFTime) {
		EXIFTime = eXIFTime;
	}

	public String getCorrectedEXIFTime() {
		return correctedEXIFTime;
	}

	public void setCorrectedEXIFTime(String correctedEXIFTime) {
		this.correctedEXIFTime = correctedEXIFTime;
	}

	public String getDeviceOffset() {
		return deviceOffset;
	}

	public void setDeviceOffset(String deviceOffset) {
		this.deviceOffset = deviceOffset;
	}

	public String getAbsDeviceOffset() {
		return absDeviceOffset;
	}

	public void setAbsDeviceOffset(String absDeviceOffset) {
		this.absDeviceOffset = absDeviceOffset;
	}

	public String getOffsetSign() {
		return offsetSign;
	}

	public void setOffsetSign(String offsetSign) {
		this.offsetSign = offsetSign;
	}

	public boolean isThresholdGT_2sec() {
		return thresholdGT_2sec;
	}

	public void setThresholdGT_2sec(boolean thresholdGT_2sec) {
		this.thresholdGT_2sec = thresholdGT_2sec;
	}

	public boolean isThresholdGT_1min() {
		return thresholdGT_1min;
	}

	public void setThresholdGT_1min(boolean thresholdGT_1min) {
		this.thresholdGT_1min = thresholdGT_1min;
	}

	public boolean isThresholdGT_5min() {
		return thresholdGT_5min;
	}

	public void setThresholdGT_5min(boolean thresholdGT_5min) {
		this.thresholdGT_5min = thresholdGT_5min;
	}

	public boolean isThresholdGT_10min() {
		return thresholdGT_10min;
	}

	public void setThresholdGT_10min(boolean thresholdGT_10min) {
		this.thresholdGT_10min = thresholdGT_10min;
	}

	public boolean isThresholdGT_15min() {
		return thresholdGT_15min;
	}

	public void setThresholdGT_15min(boolean thresholdGT_15min) {
		this.thresholdGT_15min = thresholdGT_15min;
	}

	public boolean isThresholdGT_30min() {
		return thresholdGT_30min;
	}

	public void setThresholdGT_30min(boolean thresholdGT_30min) {
		this.thresholdGT_30min = thresholdGT_30min;
	}

	public boolean isThresholdGT_1Hour() {
		return thresholdGT_1Hour;
	}

	public void setThresholdGT_1Hour(boolean thresholdGT_1Hour) {
		this.thresholdGT_1Hour = thresholdGT_1Hour;
	}

	public boolean isThresholdGT_2Hour() {
		return thresholdGT_2Hour;
	}

	public void setThresholdGT_2Hour(boolean thresholdGT_2Hour) {
		this.thresholdGT_2Hour = thresholdGT_2Hour;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}
    
	//------------------------------------------------------------------------
	//other auxiliary functions
	
	/**
	 * Returns the device offset (signed) as a long (miliseconds)
	 * @return
	 */
	public long getDeviceOffasetAsLong(){		
//		return UtilsDTS.parseDateFormatToLong(this.deviceOffset, null);
		return UtilsDTS.parseDateFormatToLong(this.deviceOffset);
	}
	
	/**
	 * Retuns the unsigned device offset as a long (miliseconds)
	 * @return
	 */
	public long getAbsDeviceOffasetAsLong(){		
//		return UtilsDTS.parseDateFormatToLong(this.deviceOffset, null);
		return UtilsDTS.parseDateFormatToLong(this.absDeviceOffset);
	}

	/**
	 * Returns a String that displays the case of the offset duration / threshold
	 * @return
	 */
	public String getThresholdCase(){
		if(!thresholdGT_2sec) return "Offset less than 2 sec";
		else if (!thresholdGT_1min) return "Offset more than 2 sec";
		else if (!thresholdGT_5min) return "Offset more than 1 min";
		else if (!thresholdGT_10min) return "Offset more than 5 min";
		else if (!thresholdGT_15min) return "Offset more than 10 min";
		else if (!thresholdGT_30min) return "Offset more than 15 min";
		else if (!thresholdGT_1Hour) return "Offset more than 30 min";
		else if (!thresholdGT_2Hour) return "Offset more than 1 hour";
		else return "Offset more than 2 hours";
 	}
 







}
