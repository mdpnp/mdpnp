package org.mdpnp.devices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumHelper {
	private static final Logger log = LoggerFactory.getLogger(EnumHelper.class);
	public static final <T extends Enum<T>> Map<Byte, T> build(Class<T> cls, String resourceName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
		Map<Byte, T> fromByte = new HashMap<Byte, T>();
		Field byteField = cls.getDeclaredField("b");
		Field unitField = null;
		try {
			unitField = cls.getDeclaredField("u");
			unitField.setAccessible(true);
		} catch (NoSuchFieldException nsfe) {
			unitField = null;
		}
		byteField.setAccessible(true);
		
		InputStream is = cls.getResource(resourceName).openStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while(null != (line = br.readLine())) {
			String[] arr = line.split("\t");
			java.lang.Byte b = (byte)(0xFF&Integer.decode(arr[0]));
			T t = Enum.valueOf(cls, arr[1]);
			if(fromByte.containsKey(b)) {
				throw new ExceptionInInitializerError("Multiple " + cls.getSimpleName() + "s  ("+fromByte.get(b)+","+t+") for hex code " + Integer.toHexString(b));
			}
			
			fromByte.put(b, t);
			byteField.setByte(t, b);
			if(unitField != null) {
				if(arr.length > 2) {
					unitField.set(t, Unit.valueOf(arr[2]));
				} else {
					log.warn("no units for " + t);
				}
			}
		}
		br.close();
		is.close();
		return fromByte;
	}
}
