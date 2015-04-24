package org.mdpnp.devices.puritanbennett._840;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PB840Parameters extends PB840 {
    public PB840Parameters(final InputStream in, final OutputStream out) {
        super(in, out);
        loadFields(fields);
    }
    

    
    private static final byte[] RSET = new byte[] {'R','S','E','T','\r'};
    private static final byte[] SNDA = new byte[] {'S', 'N', 'D', 'A', '\r'};
    private static final byte[] SNDF = new byte[] {'S','N','D','F','\r'};
    public void sendReset() throws IOException {
        out.write(RSET);
        out.flush();
    }
    public void sendA() throws IOException {
        out.write(SNDA);
        out.flush();
    }
    public void sendF() throws IOException {
        out.write(SNDF);
        out.flush();
    }
    
    private static final Pattern dataField = Pattern.compile("([^,\\03]*)[,\\03]{1,2}");
    
    private static final Logger log = LoggerFactory.getLogger(PB840Parameters.class);
    
    protected final List<String> fieldValues = new ArrayList<String>(173);
    
    
    /**
     * Receives and parses a MISCA or MISCF response from the PB840
     * @return
     * @throws IOException
     */
    public boolean receive() throws IOException {
        for(;;) {
            String line = in.readLine();
            if(null == line) {
                return true;
            }
            
            log.trace("READ A PARAMETER LINE:"+line);
            
            Matcher dataFieldMatch = dataField.matcher(line);
            
            fieldValues.clear();
            fieldValues.add("ZERO");
            if(dataFieldMatch.find()) {
                final String responseType = dataFieldMatch.group(1).trim();
                try {
                    // I want to generate the receiveStartResponse/receiveEndResponse sequence on EVERY LINE
                    // In this way consumers of this class can safely chain new requests to prior responses
                    // even where the response is not understood
                    receiveStartResponse(responseType);
                    fieldValues.add(responseType);
                    if(dataFieldMatch.find()) {
                        fieldValues.add(dataFieldMatch.group(1).trim());
                        @SuppressWarnings("unused")
                        int bytes = 0;
                        try {
                            bytes = Integer.parseInt(dataFieldMatch.group(1).trim());
                        } catch(NumberFormatException nfe) {
                            log.warn(line);
                            log.warn("Received an invalid byte count ", nfe);
                            continue;
                        }
                        //#bytes between <STX> and <CR>
                        if(dataFieldMatch.find()) {
                            String s = dataFieldMatch.group(1).trim();
                            fieldValues.add(s);
                            int fieldCount = 0; 
                            try {
                                fieldCount = Integer.parseInt(s);//#fields between <STX> and <CR>
                            } catch(NumberFormatException nfe) {
                                log.warn(line);
                                log.warn("Received an invalid field count ", nfe);
                                continue;
                            }
                            fieldValues.add("<STX"); // This will keep field numbers consistent
                            Field field = null;
                            try {
                                for(int i = 0; i < fieldCount; i++) {
                                    if(dataFieldMatch.find()) {
                                        fieldValues.add(dataFieldMatch.group(1).trim());
                                    } else {
                                        log.warn(line);
                                        log.warn("Missing expected field " + (i+1) +", aborting this line...");
                                        continue;
                                    }
                                }
                                if(fieldValues.size() < (fieldCount + 5)) {
                                    log.warn(line);
                                    log.warn("Received " + fieldValues.size() + " fields where " + (fieldCount+5) + " expected");
                                    continue;
                                }
                                fieldValues.add("<ETX>"); // for consistency
                                fieldValues.add("<CR>");
                                final Field[] fields = this.fields.get(responseType);
                                if(fields != null) {
                                    for (int i = 0; i < fields.length; i++) {
                                        field = fields[i];
                                        field.handle(fieldValues);
                                    }
                                } else {
                                    log.warn(line);
                                    log.warn("Unknown response type " + responseType);
                                }
                            } catch(NumberFormatException nfe) {
                                log.error("Error in field " + field);
                            }
                        } else {
                            log.warn(line);
                            log.warn("Not a valid response, no field count:"+line);
                        }
                    } else {
                        log.warn(line);
                        log.warn("Not a valid response, no bytes:"+line);
                    }
                } finally {
                    receiveEndResponse();
                }
            } else {
                try {
                    // Generate this sequence for every CR terminated line!
                    receiveStartResponse("");
                } finally {
                    receiveEndResponse();
                }
                log.warn(line);
                log.warn("Not a valid response:"+line);
            }
        }
    }
    

    /**
     * Receives the response for a command type
     * e.g. MISCF - request for ventilator settings, 
     *              monitored data and alarm information
     *      MISCA - request for ventilator settings 
     *              and monitored data
     * @param fieldValues
     */
    public void receiveStartResponse(String type) {
        
    }
    
    public void receiveEndResponse() {
        
    }
    
    public void receiveSetting(String name, Units units, String value) {
        
    }
    
    public void receiveNumeric(String name, Units units, String value) {
        
    }
    
//    public void receiveAlarmSetting(String name, String lower, String upper) {
//        
//    }
    
    public void receiveAlarmLimit(String metricName, PB840.Units unitID, String value, String limitType){
    	
    }
    
    public void receiveTechnicalAlert(String name, String value) {
        
    }
    
    public void receivePatientAlert(String name, String value) {
        
    }
    
    public void receiveVentilatorId(String model, String id) {
        
    }
    
    public void receiveDate(int month, int day, int year) {
        
    }
    
    public void receiveTime(int hour, int minute) {
        
    }
    
    abstract class Field {
        final String name, description;

        public Field(final String name, final String description) {
            this.name = name;
            this.description = description;
        }

        abstract void handle(List<String> fieldValues);
        
    }

    class Numeric extends Field {
        final int fieldNumber;
        final Units units;

        
        public Numeric(final String name, final String description, final Units units, final int fieldNumber) {
            super(name, description);
            this.units = units;
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receiveNumeric(name, units, fieldValues.get(fieldNumber));
        }
        
        @Override
        public String toString() {
            return "Numeric["+name+","+description+","+units+","+fieldNumber+"]";
        }
    }
    class Setting extends Field {
        final int fieldNumber;
        final Units units;

        
        public Setting(final String name, final String description, final Units units, final int fieldNumber) {
            super(name, description);
            this.units = units;
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receiveSetting(name, units, fieldValues.get(fieldNumber));
        }
        
        @Override
        public String toString() {
            return "Setting["+name+","+description+","+units+","+fieldNumber+"]";
        }
    }    
    class AlarmLimit extends Field {
    	final int value;
    	final Units units;
    	final String limitType;
    	//String AlarmType, String unitID, String AlrmThreshold, String value
    	

		public AlarmLimit(final String name, final String description, final Units units, final int value, final String limitType){
	            super(name, description);
	            this.units = units;
	            this.value = value;
	            this.limitType = limitType;
			}

		
	     @Override
	        void handle(List<String> fieldValues) {
	    	 if (value > -1)//inexistent limits are fields -1 in the PB840.fields
	    		 receiveAlarmLimit(name, units, fieldValues.get(value), limitType);
//	    		 receiveAlarmLimit(name, units, value < 0 ? null:fieldValues.get(value), limitType);
//	                    lowFieldNumber < 0 ? null : fieldValues.get(lowFieldNumber), 
//	                    highFieldNumber < 0 ? null : fieldValues.get(highFieldNumber));
	        }
		
		
        @Override
        public String toString() {
            return "AlarmLimit["+name+","+description+","+value+","+units+","+limitType+"]";
        }
    	
    }
    
    class PatientAlert extends Field {
        final int fieldNumber;

        public PatientAlert(final String name, final String description, final int fieldNumber) {
            super(name, description);
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receivePatientAlert(name, fieldValues.get(fieldNumber));
        }
        @Override
        public String toString() {
            return "PatientAlert["+name+","+description+","+fieldNumber+"]";
        }
    }
    
    class TechnicalAlert extends Field {
        final int fieldNumber;

        public TechnicalAlert(final String name, final String description, final int fieldNumber) {
            super(name, description);
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receiveTechnicalAlert(name, fieldValues.get(fieldNumber));
        }
        
        @Override
        public String toString() {
            return "TechnicalAlert["+name+","+description+","+fieldNumber+"]";
        }
    }
    
    class VentilatorId extends Field {
        final int fieldNumber;

        public VentilatorId(final int fieldNumber) {
            super(null, null);
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            String[] modelSerial = fieldValues.get(fieldNumber).split(" ");
            receiveVentilatorId(modelSerial.length > 1 ? modelSerial[0] : "840", modelSerial.length > 1 ? modelSerial[1] : modelSerial[0]);
        }
        
        @Override
        public String toString() {
            return "VentilatorId["+name+","+description+","+fieldNumber+"]";
        }
    }
    
    class Time extends Field {
        final int fieldNumber;

        public Time(final int fieldNumber) {
            super(null, null);
            this.fieldNumber = fieldNumber;
        }
        
        @Override
        void handle(List<String> fieldValues) {
            String[] hour_minute = fieldValues.get(fieldNumber).split(":");
            receiveTime(Integer.parseInt(hour_minute[0]), Integer.parseInt(hour_minute[1]));
        }
        
        @Override
        public String toString() {
            return "Time["+fieldNumber+"]";
        }
    }
    protected static final Map<String, Integer> months = new HashMap<String, Integer>();
    static {
        months.put("JAN", Calendar.JANUARY);
        months.put("FEB", Calendar.FEBRUARY);
        months.put("MAR", Calendar.MARCH);
        months.put("APR", Calendar.APRIL);
        months.put("MAY", Calendar.MAY);
        months.put("JUN", Calendar.JUNE);
        months.put("JUL", Calendar.JULY);
        months.put("AUG", Calendar.AUGUST);
        months.put("SEP", Calendar.SEPTEMBER);
        months.put("OCT", Calendar.OCTOBER);
        months.put("NOV", Calendar.NOVEMBER);
        months.put("DEC", Calendar.DECEMBER);
    }
    class Date extends Field {
        final int fieldNumber;
        
        public Date(final int fieldNumber) {
            super(null, null);
            this.fieldNumber = fieldNumber;
        }
        @Override
        void handle(List<String> fieldValues) {
            String[] month_day_year = fieldValues.get(fieldNumber).split(" ");
            receiveDate(months.get(month_day_year[0]), Integer.parseInt(month_day_year[1]), Integer.parseInt(month_day_year[2]));
        }
        
        @Override
        public String toString() {
            return "Date["+fieldNumber+"]";
        }
    }

    protected final Map<String, Field[]> fields = new HashMap<String, Field[]>();
    protected void loadFields(Map<String, Field[]> fields) {
        int lineNumber = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(PB840Parameters.class.getResourceAsStream("pb840.fields")));
            String line = null;

            List<Field> currentFields = new ArrayList<Field>();
            String currentResponseType = null;
            
            
            while (null != (line = br.readLine())) {
                // A header row is included
                if(0==lineNumber++) { continue; }
                line = line.trim();
                if ('#' != line.charAt(0)) {
                    String v[] = line.split("\t");

                    if (v.length < 1) {
                        log.warn("Bad line" + lineNumber + ":" + line);
                    } else if(v.length < 2) {
                        if(null != currentResponseType) {
                            fields.put(currentResponseType, currentFields.toArray(new Field[0]));
                            currentFields.clear();
                        }
                        if(!currentFields.isEmpty()) {
                            log.warn(currentFields.size() + " orphaned fields:"+currentFields);
                            currentFields.clear();
                        }
                        currentResponseType = v[0];
                    } else {
                        String type = v[0];
                        String name =  v[1];
                        String units = v.length > 2 ? v[2] : null;
                        String field1 = v.length > 3 ? v[3] : null;
                        String field2 = v.length > 4 ? v[4] : null;
                        String description = v.length > 5 ? v[5] : null;
                        if("S".equals(type)) {
                            currentFields.add(new Setting(name, description, Units.valueOf(units), Integer.parseInt(field1)));
                        } else if("N".equals(type)) {
                            currentFields.add(new Numeric(name, description, Units.valueOf(units), Integer.parseInt(field1)));
                        } else if("AS".equals(type)) {
                        	currentFields.add(new AlarmLimit(name, description, Units.getValue(units), Integer.parseInt(field1), "low_limit"));
                        	currentFields.add(new AlarmLimit(name, description, Units.getValue(units), Integer.parseInt(field2), "high_limit"));
                        } else if("PA".equals(type)) {
                            currentFields.add(new PatientAlert(name, description, Integer.parseInt(field1)));
                        } else if("TA".equals(type)) {
                            currentFields.add(new TechnicalAlert(name, description, Integer.parseInt(field1)));
                        } else if("ID".equals(type)) {
                            currentFields.add(new VentilatorId(Integer.parseInt(field1)));
                        } else if("TM".equals(type)) {
                            currentFields.add(new Time(Integer.parseInt(field1)));
                        } else if("DT".equals(type)) {
                            currentFields.add(new Date(Integer.parseInt(field1)));
                        } else {
                            throw new IllegalArgumentException("Unknown field type " + type + " with " + v.length + " total elements");
                        }
                    }
                }
            }
            if(null != currentResponseType) {
                fields.put(currentResponseType, currentFields.toArray(new Field[0]));
                currentFields.clear();
            }
            if(!currentFields.isEmpty()) {
                log.warn(currentFields.size() + " orphaned fields:"+currentFields);
                currentFields.clear();
            }
            currentResponseType = null;
        } catch (Exception e) {
            throw new RuntimeException("Error at line " + lineNumber, e);
        }
    }

}
