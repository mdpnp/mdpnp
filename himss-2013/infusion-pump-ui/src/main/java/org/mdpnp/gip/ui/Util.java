package org.mdpnp.gip.ui;

import java.lang.reflect.Field;

/**
 * @author Jeff Plourde
 *
 */
public class Util {
	public static final String toString(Object o) {
		StringBuilder sb = new StringBuilder("[");
		for(Field f : o.getClass().getDeclaredFields()) {
			try {
				f.setAccessible(true);
				sb.append(f.getName()).append("=").append(f.get(o)).append(",");
				f.setAccessible(false);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		sb.delete(sb.length() - 1, sb.length());
		sb.append("]");
		return sb.toString();
	}
}
