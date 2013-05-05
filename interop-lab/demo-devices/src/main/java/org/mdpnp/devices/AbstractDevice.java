/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import org.mdpnp.data.IdentifiableUpdate;
import org.mdpnp.data.Identifier;
import org.mdpnp.data.identifierarray.IdentifierArrayUpdate;
import org.mdpnp.data.identifierarray.MutableIdentifierArrayUpdate;
import org.mdpnp.data.identifierarray.MutableIdentifierArrayUpdateImpl;
import org.mdpnp.data.image.MutableImageUpdate;
import org.mdpnp.data.image.MutableImageUpdateImpl;
import org.mdpnp.data.text.MutableTextUpdate;
import org.mdpnp.data.text.MutableTextUpdateImpl;
import org.mdpnp.messaging.Gateway;
import org.mdpnp.messaging.GatewayListener;
import org.mdpnp.nomenclature.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractDevice implements GatewayListener, ThreadFactory {
    protected final ThreadGroup threadGroup;
	protected final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(this);

	private static final Logger log = LoggerFactory.getLogger(AbstractDevice.class);
	protected final Gateway gateway;
	protected final Map<Identifier, IdentifiableUpdate<?>> updates = new HashMap<Identifier, IdentifiableUpdate<?>>();
	protected final MutableTextUpdate nameUpdate = new MutableTextUpdateImpl(Device.NAME);
	protected final MutableTextUpdate guidUpdate = new MutableTextUpdateImpl(Device.GUID);
	protected final MutableImageUpdate iconUpdate = new MutableImageUpdateImpl(Device.ICON);
	
	
	
	protected String iconResourceName() {
		return null;
	}
	
	protected boolean iconUpdateFromResource(MutableImageUpdate iconUpdate, String iconResourceName) throws IOException {
		if(null != iconResourceName) {
			try {
				Method read = Class.forName("javax.imageio.ImageIO").getMethod("read", URL.class);
				Object bi = read.invoke(null, getClass().getResource(iconResourceName));
//				BufferedImage bi = ImageIO.read(getClass().getResource(iconResourceName));
				Class<?> bufferedImage = Class.forName("java.awt.image.BufferedImage");
				int width = (Integer) bufferedImage.getMethod("getWidth").invoke(bi);
//				int width = bi.getWidth();
				int height = (Integer) bufferedImage.getMethod("getHeight").invoke(bi);
//				int height = bi.getHeight();
				Method getRGB = bufferedImage.getMethod("getRGB", int.class, int.class);
				iconUpdate.setWidth(width);
				iconUpdate.setHeight(height);
				byte[] raster = new byte[width * height * 4];
				IntBuffer bb = ByteBuffer.wrap(raster).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
				for(int y = 0; y < height; y++) {
					for(int x = 0; x < width; x++) {
						bb.put((Integer) getRGB.invoke(bi, x, y));
//						bb.put(bi.getRGB(x, y));
					}
				}
				iconUpdate.setRaster(raster);
				return true;
			} catch (InvocationTargetException e) {
				if(e.getCause() instanceof IOException) {
					throw (IOException) e.getCause();
				}
			} catch (Exception e) {
				log.error("error in iconUpdateFromResource", e);
			}
			return false;
		} else {
			return false;
		}

	}
	
	protected boolean prepareIconUpdate(MutableImageUpdate iconUpdate) throws IOException {
		return iconUpdateFromResource(iconUpdate, iconResourceName());
	}
	
	protected void add(IdentifiableUpdate<?>... uu) {
		for(IdentifiableUpdate<?> u : uu) {
			add(u);
		}
	}
	
	protected void add(IdentifiableUpdate<?> u) {
		updates.put(u.getIdentifier(), u);
	}
	
	private int threadOrdinal = 0;
	
	@Override
	public Thread newThread(Runnable r) {
	    Thread t = new Thread(threadGroup, r, "AbstractDevice-"+(++threadOrdinal));
	    t.setDaemon(true);
	    return t;
	}
	
	public AbstractDevice(Gateway gateway) {
		this.gateway = gateway;
		threadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup(), "AbstractDevice") {
    		  @Override
    		public void uncaughtException(Thread t, Throwable e) {
    		    log.error("Thrown by " + t.toString(), e);
    		    super.uncaughtException(t, e);
    		}  
		};
		threadGroup.setDaemon(true);
		add(nameUpdate);
		add(guidUpdate);
		try {
			if(prepareIconUpdate(iconUpdate)) {
				add(iconUpdate);
			}
		} catch(IOException ioe) {
			log.error("Unable to prepareIcon", ioe);
		}
		gateway.addListener(this);
	}
	
	protected IdentifiableUpdate<?> get(Identifier identifier) {
		return updates.get(identifier);		
	}
	
	@Override
	public void update(IdentifiableUpdate<?> command) {
		if(Device.REQUEST_IDENTIFIED_UPDATES.equals(command.getIdentifier())) {
			IdentifierArrayUpdate upds = (IdentifierArrayUpdate)command;
			for(Identifier i : upds.getValue()) {
				
				IdentifiableUpdate<?> iu = get(i);
				if(null != iu) {
//					log.trace("REQUEST_IDENTIFIED_UPDATES:"+i+" " + iu);
					gateway.update(this, iu);
				}
			}
		} else if(Device.REQUEST_AVAILABLE_IDENTIFIERS.equals(command.getIdentifier())) {
			MutableIdentifierArrayUpdate upds = new MutableIdentifierArrayUpdateImpl(Device.GET_AVAILABLE_IDENTIFIERS);
			upds.setValue(this.updates.keySet().toArray(new Identifier[0]));
			gateway.update(this, upds);
		}
	}
	
}
