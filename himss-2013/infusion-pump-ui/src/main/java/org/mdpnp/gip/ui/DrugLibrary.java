package org.mdpnp.gip.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeff Plourde
 *
 */
public class DrugLibrary {
	private final List<CareArea> careAreas = new ArrayList<CareArea>();
	
	public List<CareArea> getCareAreas() {
		return careAreas;
	}
	
	private static final String stringOrNull(String[] str, int i) {
		if(i >= str.length) {
			return null;
		} else {
			if("".equals(str[i])) {
				return null;
			} else {
				return str[i];
			}
		}
	}
	
	private static final Double doubleOrNull(String[] str, int i) {
		String s = stringOrNull(str, i);
		if(null == s) {
			return null;
		} else {
			StringBuilder sb = new StringBuilder();
			for(int j = 0; j < s.length(); j++) {
				char c = s.charAt(j);
				if(Character.isDigit(c) || '.' == c) {
					sb.append(c);
				}
			}
			if(sb.length()==0) {
				return null;
			} else {
				return Double.parseDouble(sb.toString());
			}
		}
	}
	
	private static final String unitsStrOrNull(String[] str, int i) {
		String s = stringOrNull(str, i);
		if(null == s) {
			return null;
		} else {
			StringBuilder sb = new StringBuilder();
			for(int j = 0; j < s.length(); j++) {
				char c = s.charAt(j);
				if(Character.isLetter(c) || '/' == c) {
					sb.append(c);
				}
			}
			return sb.toString();
		}
	}
	
	public DrugLibrary() throws IOException {
		InputStream is = DrugLibrary.class.getResourceAsStream("druglibrary");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		CareArea ca = null;
		
		int lineNumber = 1;
		
		try {
			String line = null;
			while( null != (line = br.readLine())) {
				try {
					String[] values = line.split("\t");
					if(values.length == 1) {
						ca = new CareArea(values[0]);
						careAreas.add(ca);
					} else if(values.length >= 5) {
						DrugEntry de = new DrugEntry(stringOrNull(values, 0),
								doubleOrNull(values, 1),
								unitsStrOrNull(values, 1),
								doubleOrNull(values, 2),
								unitsStrOrNull(values, 2),
								doubleOrNull(values, 3),
								unitsStrOrNull(values, 3),
								stringOrNull(values, 4),
								doubleOrNull(values, 5),
								doubleOrNull(values, 6),
								doubleOrNull(values, 7),
								doubleOrNull(values, 8),
								doubleOrNull(values, 9),
								doubleOrNull(values, 10),
								doubleOrNull(values, 11),
								doubleOrNull(values, 12),
								doubleOrNull(values, 13),
								doubleOrNull(values, 14),
								doubleOrNull(values, 15));
						ca.getDrugEntries().add(de);
								
					}
				} catch (Throwable t) {
					throw new RuntimeException("At line " + lineNumber, t);
				}
				lineNumber++;
			}
		} finally {
			if(br != null) {
				br.close();
			}
		}
	}
	@Override
	public String toString() {
		return Util.toString(this);
	}
	public static void main(String[] args) throws IOException {
		System.out.println(new DrugLibrary());
	}
}
