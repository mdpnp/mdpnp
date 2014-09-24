package org.mdpnp.devices.draeger.medibus;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestRTMedibus {
    @Test
    public void testParseInt() throws Exception {
        String[] str = new String[] {"101.483", "-100", "  -1", "  1  ", "999", "-4.342   ", "-  80"};
        int[]    itg = new int[]    {101, -100, -1, 1, 999, -4, -80};
        for(int i = 0; i < str.length; i++) {
            assertEquals(itg[i], RTMedibus.parseInt(str[i].getBytes("ASCII")));
        }
        
        assertEquals(-80, RTMedibus.parseInt(new byte[] {'1','0','-',' ',' ','-',' ',' ','8','0','.','5'}, 3, 9));
    }
}
