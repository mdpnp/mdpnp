package org.mdpnp.devices.denver.mseries;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {
	
	public PatternTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Pattern p=Pattern.compile("[0-9]{0,4}\\.[0-9]{0,4} ?g?");
		String[] vals=new String[] {
			"12.9 g" , 
			"1 + 0000.0002" ,
			"S + 0000.0003g" ,
			"ST + 0000.0003" ,
			"+ 0000.0003" ,
			"+ 0000.0003 grams" ,
			"12.4 g   00:03:03"
		};
		for(String val : vals) {
			System.out.println("Checking val "+val);
			Matcher m=p.matcher(val);
			while(m.find()) {
				//System.out.println("It's a match...");
				if( m.groupCount() > 0 ) {
					//More than one match...
					for(int j=0;j<m.groupCount();j++) {
						System.out.println("match group "+j+" is "+m.group(j));
					}
				} else {
					int start=m.start();
					int end=m.end();
					System.out.println("Matched string is "+val.substring(start, end));
					String stripped=val.substring(start,end).replaceAll("[ +g]", "")/*.replaceAll("[A-z]", "")*/;
					System.out.println("Stripped "+val+" is "+stripped);
					Float f=Float.valueOf(stripped);
					System.out.println("f is "+f);
					
				}
			}
		}

	}

}
