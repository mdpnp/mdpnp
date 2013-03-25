package org.mdpnp.qmdi.types.polymorphism.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TestExecutor {

	private TestExecutor() {
	}
	
	private static ScheduledExecutorService executor;
	
	public static final synchronized ScheduledExecutorService get() {
		if(null == executor) {
			executor = Executors.newSingleThreadScheduledExecutor();
		}
		return executor;
	}

}
