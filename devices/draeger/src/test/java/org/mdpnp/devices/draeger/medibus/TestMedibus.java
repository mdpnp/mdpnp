package org.mdpnp.devices.draeger.medibus;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMedibus {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private static final String[] validation_timestamps = new String[] {
        " 9:33 7-OCT-14", // Leading space
        " 9:33:00 7-OKT-14",
        " 9:33 07-OCT-14",
        "09:33 7-OKT-14",
        "09:33:00 07-OKT-14"
    };
    
    private static final long[] validation_expected = new long[] {
        1412674380000L,
        1412674380000L,
        1412674380000L,
        1412674380000L,
        1412674380000L
    };
    
    @Test
    public void testDateTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int i = 0;
        
        for(String dt : validation_timestamps) {
            long expected = validation_expected[i++];
            Matcher m = Medibus.dateTimePattern.matcher(dt);
            // TODO this code should be sahred in common with Medibus
            if (m.matches()) {
                if(m.groupCount()<6) {
                    // This shouldn't happen because the regex wouldn't have matched
                    fail("Insufficient capture groups in datetime:" + dt);
                } else {
                    int field = 1;
                    try {
                        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(field++)));
                        cal.set(Calendar.MINUTE, Integer.parseInt(m.group(field++)));
                        String seconds = m.group(field++);
                        cal.set(Calendar.SECOND, seconds.length()>0?Integer.parseInt(seconds):0);
                        cal.set(Calendar.MILLISECOND, 0);
                        cal.set(Calendar.DATE, Integer.parseInt(m.group(field++)));
                        String germanMonth = m.group(field++);
                        Integer month = Medibus.germanMonths.get(germanMonth);
                        if(null != month) {
                            cal.set(Calendar.MONTH, month);
                            // Note the V500 as of 12-Mar-2014 emits "14" as the year
                            cal.set(Calendar.YEAR, 2000 + Integer.parseInt(m.group(field++)));
                            assertEquals(expected, cal.getTime().getTime());
                        } else {
                            fail("Cannot process German month \""+germanMonth+"\" in " + dt);
                        }
                        
                    } catch (NumberFormatException nfe) {
                        fail("Unable to parse a field in datetime " + dt + " field was \"" + m.group(field)+"\"");
                    }
                }
            } else {
                fail("Received a bad datetime:" + dt);
            }

        }
    }

}
