package org.mdpnp.qmdi;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Jeff Plourde
 *
 */
public class Main {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
	if(args.length <= 0) {
	    System.err.println("Please provide argument {submit|ack|await}");
	    return;
	}
	String [] _args = new String[args.length-1];
	System.arraycopy(args, 1, _args, 0, _args.length);
	if("submit".equals(args[0])) {
	    Class.forName("org.mdpnp.qmdi.DocumentSubmit").getMethod("main", String[].class).invoke(null, new Object[] {_args});
	} else if("ack".equals(args[0])) {
	    Class.forName("org.mdpnp.qmdi.ack.Acknowledge").getMethod("main", String[].class).invoke(null, new Object[] {_args});
	} else if("await".equals(args[0])) {
	    Class.forName("org.mdpnp.qmdi.ack.Await").getMethod("main", String[].class).invoke(null, new Object[] {_args});
	}


    }
}
