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
                fieldValues.add(responseType);
                if(dataFieldMatch.find()) {
                    fieldValues.add(dataFieldMatch.group(1).trim());
                    @SuppressWarnings("unused")
                    int bytes = 0;
                    try {
                        bytes = Integer.parseInt(dataFieldMatch.group(1).trim());
                    } catch(NumberFormatException nfe) {
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
                            log.warn("Received an invalid field count ", nfe);
                            continue;
                        }
                        fieldValues.add("<STX"); // This will keep field numbers consistent
                        try {
                            receiveStartResponse(responseType);
                            for(int i = 0; i < fieldCount; i++) {
                                if(dataFieldMatch.find()) {
                                    fieldValues.add(dataFieldMatch.group(1).trim());
                                } else {
                                    log.warn("Missing expected field " + (i+1));
                                }
                            }
                            if(fieldValues.size() < (fieldCount + 5)) {
                                log.warn("Received " + fieldValues.size() + " fields where " + (fieldCount+5) + " expected");
                                continue;
                            }
                            fieldValues.add("<ETX>"); // for consistency
                            fieldValues.add("<CR>");
                            final Field[] fields = this.fields.get(responseType);
                            if(fields != null) {
                                for (Field field : fields) {
                                    field.handle(fieldValues);
                                }
                            } else {
                                log.warn("Unknown response type " + responseType);
                            }
                        } finally {
                            receiveEndResponse();
                        }
                    } else {
                        log.warn("Not a valid response, no field count:"+line);
                    }
                } else {
                    log.warn("Not a valid response, no bytes:"+line);
                }
            } else {         
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
    
    public void receiveSetting(String name, String value) {
        
    }
    
    public void receiveNumeric(String name, Units units, String value) {
        
    }
    
    public void receiveAlarmSetting(String name, String lower, String upper) {
        
    }
    
    public void receiveTechnicalAlert(String name, String value) {
        
    }
    
    public void receivePatientAlert(String name, String value) {
        
    }
    
    public void receiveVentilatorId(String id) {
        
    }
    
    public void receiveDate(int month, int day, int year) {
        
    }
    
    public void receiveTime(int hour, int minute) {
        
    }
    
    abstract class Field {
        final String name;

        public Field(final String name) {
            this.name = name;
        }

        abstract void handle(List<String> fieldValues);
    }

    class Numeric extends Field {
        final int fieldNumber;
        final Units units;

        
        public Numeric(final String name, final Units units, final int fieldNumber) {
            super(name);
            this.units = units;
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receiveNumeric(name, units, fieldValues.get(fieldNumber));
        }
        
        @Override
        public String toString() {
            return "Numeric["+name+","+units+","+fieldNumber+"]";
        }
    }
    class AlarmSetting extends Field {
        final int lowFieldNumber, highFieldNumber;

        public AlarmSetting(final String name, final int lowFieldNumber, final int highFieldNumber) {
            super(name);
            this.lowFieldNumber = lowFieldNumber;
            this.highFieldNumber = highFieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receiveAlarmSetting(name, 
                    lowFieldNumber < 0 ? null : fieldValues.get(lowFieldNumber), 
                    highFieldNumber < 0 ? null : fieldValues.get(highFieldNumber));
        }
        
        @Override
        public String toString() {
            return "AlarmSetting["+name+","+lowFieldNumber+","+highFieldNumber+"]";
        }
    }
    
    class PatientAlert extends Field {
        final int fieldNumber;

        public PatientAlert(String name, final int fieldNumber) {
            super(name);
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receivePatientAlert(name, fieldValues.get(fieldNumber));
        }
        @Override
        public String toString() {
            return "PatientAlert["+name+","+fieldNumber+"]";
        }
    }
    
    class TechnicalAlert extends Field {
        final int fieldNumber;

        public TechnicalAlert(String name, final int fieldNumber) {
            super(name);
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receiveTechnicalAlert(name, fieldValues.get(fieldNumber));
        }
        
        @Override
        public String toString() {
            return "TechnicalAlert["+name+","+fieldNumber+"]";
        }
    }
    
    class VentilatorId extends Field {
        final int fieldNumber;

        public VentilatorId(final int fieldNumber) {
            super(null);
            this.fieldNumber = fieldNumber;
        }

        @Override
        void handle(List<String> fieldValues) {
            receiveVentilatorId(fieldValues.get(fieldNumber));
        }
        
        @Override
        public String toString() {
            return "VentilatorId["+name+","+fieldNumber+"]";
        }
    }
    
    class Time extends Field {
        final int fieldNumber;

        public Time(final int fieldNumber) {
            super(null);
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
            super(null);
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

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(PB840Parameters.class.getResourceAsStream("pb840.fields")));
            String line = null;

            List<Field> currentFields = new ArrayList<Field>();
            String currentResponseType = null;
            int lineNumber = 0;
            
            while (null != (line = br.readLine())) {
                lineNumber++;
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
                        if("N".equals(v[0]) && v.length == 4) {
                            currentFields.add(new Numeric(v[1], Units.valueOf(v[2]), Integer.parseInt(v[3])));
                        } else if("AS".equals(v[0]) && v.length == 4) {
                            currentFields.add(new AlarmSetting(v[1], Integer.parseInt(v[2]), Integer.parseInt(v[3])));
                        } else if("PA".equals(v[0]) && v.length == 3) {
                            currentFields.add(new PatientAlert(v[1], Integer.parseInt(v[2])));
                        } else if("TA".equals(v[0]) && v.length == 3) {
                            currentFields.add(new TechnicalAlert(v[1], Integer.parseInt(v[2])));
                        } else if("ID".equals(v[0]) && v.length == 2) {
                            currentFields.add(new VentilatorId(Integer.parseInt(v[1])));
                        } else if("TM".equals(v[0]) && v.length == 2) {
                            currentFields.add(new Time(Integer.parseInt(v[1])));
                        } else if("DT".equals(v[0]) && v.length == 2) {
                            currentFields.add(new Date(Integer.parseInt(v[1])));
                        } else if(v.length > 0) {
                            throw new IllegalArgumentException("Unknown field type " + v[0] + " with " + v.length + " total elements");
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
            throw new RuntimeException(e);
        }
    }

}
