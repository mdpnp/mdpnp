/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices;

import ice.DeviceIdentity;
import ice.DeviceIdentityDataWriter;
import ice.DeviceIdentityTypeSupport;
import ice.Numeric;
import ice.NumericDataWriter;
import ice.NumericTypeSupport;
import ice.SampleArray;
import ice.SampleArrayDataWriter;
import ice.SampleArrayTypeSupport;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;


public abstract class AbstractDevice implements ThreadFactory {
    protected final ThreadGroup threadGroup;
	protected final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(this);
	protected final EventLoop eventLoop;

	private static final Logger log = LoggerFactory.getLogger(AbstractDevice.class);
	
	protected final DomainParticipant domainParticipant;
	protected final Publisher publisher;
	protected final Subscriber subscriber;
	protected final Topic deviceIdentityTopic;
	protected final DeviceIdentityDataWriter deviceIdentityWriter;
	protected final DeviceIdentity deviceIdentity;
	protected InstanceHandle_t deviceIdentityHandle;
	
	protected final Topic numericTopic;
	protected final NumericDataWriter numericDataWriter;
	
	protected final Topic sampleArrayTopic;
	protected final SampleArrayDataWriter sampleArrayDataWriter;
	
	public Subscriber getSubscriber() {
	    return subscriber;
	}
	
	public static class InstanceHolder<T> {
	    public T data;
	    public InstanceHandle_t handle;
	    
	    public InstanceHolder() {
	        
	    }
	    
	    public InstanceHolder(T t, InstanceHandle_t handle) {
	        this.data = t;
	        this.handle = handle;
	    }
	    
	    @Override
	    public String toString() {
	        return "[data="+data+",handle="+handle+"]";
	    }
	}
	
	public DeviceIdentity getDeviceIdentity() {
	    return deviceIdentity;
	}
	
	public DomainParticipant getParticipant() {
	    return domainParticipant;
	}
	
	protected InstanceHolder<Numeric> createNumericInstance(int name) {
	    if(deviceIdentity == null || deviceIdentity.universal_device_identifier == null || "".equals(deviceIdentity.universal_device_identifier)) {
	        throw new IllegalStateException("Please populate deviceIdentity.universal_device_identifier before calling createNumericInstance");
	    }
	    
	    InstanceHolder<Numeric> holder = new InstanceHolder<Numeric>();
	    holder.data = new Numeric();
	    holder.data.universal_device_identifier = deviceIdentity.universal_device_identifier;
	    holder.data.name = name;
	    holder.handle = numericDataWriter.register_instance(holder.data);
	    return holder;
	}
	
	protected InstanceHolder<SampleArray> createSampleArrayInstance(int name) {
	    if(deviceIdentity == null || deviceIdentity.universal_device_identifier == null || "".equals(deviceIdentity.universal_device_identifier)) {
            throw new IllegalStateException("Please populate deviceIdentity.universal_device_identifier before calling createSampleArrayInstance");
        }
        
	    InstanceHolder<SampleArray> holder = new InstanceHolder<SampleArray>();
        holder.data = new SampleArray();
        holder.data.universal_device_identifier = deviceIdentity.universal_device_identifier;
        holder.data.name = name;
        holder.handle = sampleArrayDataWriter.register_instance(holder.data);
        return holder;
	}
	
	protected void numericSample(InstanceHolder<Numeric> holder, float newValue) {
	    holder.data.value = newValue;
	    numericDataWriter.write(holder.data, holder.handle);
	}
	
	protected void sampleArraySample(InstanceHolder<SampleArray> holder, Number[] newValues, int msPerSample) {
	    holder.data.values.clear();
        for(Number n : newValues) {
            holder.data.values.addFloat(n.floatValue());
        }
        holder.data.millisecondsPerSample = msPerSample;
        
        sampleArrayDataWriter.write(holder.data, holder.handle);
	}
	
	protected String iconResourceName() {
		return null;
	}
	
	protected boolean iconFromResource(DeviceIdentity di, String iconResourceName) throws IOException {
		if(null != iconResourceName) {
			try {
			    
				Method read = Class.forName("javax.imageio.ImageIO").getMethod("read", URL.class);
				Object bi = read.invoke(null, getClass().getResource(iconResourceName));
//				BufferedImage bi = ImageIO.read(getClass().getResource(iconResourceName));
				Class<?> bufferedImage = Class.forName("java.awt.image.BufferedImage");
				di.icon.width = (Integer) bufferedImage.getMethod("getWidth").invoke(bi);
//				int width = bi.getWidth();
				di.icon.height = (Integer) bufferedImage.getMethod("getHeight").invoke(bi);
				
//				int height = bi.getHeight();
				Method getRGB = bufferedImage.getMethod("getRGB", int.class, int.class);

				byte[] raster = new byte[di.icon.width * di.icon.height * 4];
				log.trace("Image w="+di.icon.width+" h="+di.icon.height + " raster.length="+raster.length);
				IntBuffer bb = ByteBuffer.wrap(raster).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
				for(int y = 0; y < di.icon.height; y++) {
					for(int x = 0; x < di.icon.width; x++) {
						bb.put((Integer) getRGB.invoke(bi, x, y));
//						bb.put(bi.getRGB(x, y));
					}
				}
				di.icon.raster.clear();
				di.icon.raster.addAllByte(raster);
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
	
	private int threadOrdinal = 0;
	
	@Override
	public Thread newThread(Runnable r) {
	    Thread t = new Thread(threadGroup, r, "AbstractDevice-"+(++threadOrdinal));
	    t.setDaemon(true);
	    return t;
	}
	
	
	public void shutdown() {
	    // TODO there is nothing preventing inheritors from interacting with these objects as (and after) they are destroyed.
	    // TODO there isn't a coherent way to dispose of the instances an inheritor may have registered... perhaps they should be responsible in their override of shutdown?
	    
	    publisher.delete_datawriter(sampleArrayDataWriter);
	    domainParticipant.delete_topic(sampleArrayTopic);
	    SampleArrayTypeSupport.unregister_type(domainParticipant, SampleArrayTypeSupport.get_type_name());
	    
	    publisher.delete_datawriter(numericDataWriter);
	    domainParticipant.delete_topic(numericTopic);
	    NumericTypeSupport.unregister_type(domainParticipant, NumericTypeSupport.get_type_name());
	    
	    publisher.delete_datawriter(deviceIdentityWriter);
	    domainParticipant.delete_topic(deviceIdentityTopic);
	    DeviceIdentityTypeSupport.unregister_type(domainParticipant, DeviceIdentityTypeSupport.get_type_name());
	    
	    domainParticipant.delete_publisher(publisher);
	    domainParticipant.delete_subscriber(subscriber);

	    DomainParticipantFactory.get_instance().delete_participant(domainParticipant);
	    
	    executor.shutdown();
	    log.info("AbstractDevice shutdown complete");
	}
	
	public AbstractDevice(int domainId, EventLoop eventLoop) {
        DomainParticipantQos pQos = new DomainParticipantQos();
        DomainParticipantFactory.get_instance().get_default_participant_qos(pQos);
        pQos.participant_name.name = "AbstractDevice";
	    domainParticipant = DomainParticipantFactory.get_instance().create_participant(domainId, pQos, null, StatusKind.STATUS_MASK_NONE);
        publisher = domainParticipant.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        subscriber = domainParticipant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        
        DeviceIdentityTypeSupport.register_type(domainParticipant, DeviceIdentityTypeSupport.get_type_name());
        deviceIdentityTopic = domainParticipant.create_topic(ice.DeviceIdentityTopic.VALUE, DeviceIdentityTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        deviceIdentityWriter = (DeviceIdentityDataWriter) publisher.create_datawriter(deviceIdentityTopic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        if(null == deviceIdentityWriter) {
            throw new RuntimeException("deviceIdentityWriter not created");
        }
        deviceIdentity = new DeviceIdentity();
        try {
            iconFromResource(deviceIdentity, iconResourceName());
        } catch (IOException e1) {
            log.warn("",e1);
        }
        
        NumericTypeSupport.register_type(domainParticipant, NumericTypeSupport.get_type_name());
        numericTopic = domainParticipant.create_topic(ice.NumericTopic.VALUE, NumericTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        numericDataWriter = (NumericDataWriter) publisher.create_datawriter(numericTopic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        if(null == numericDataWriter) {
            throw new RuntimeException("numericDataWriter not created");
        }
        
        SampleArrayTypeSupport.register_type(domainParticipant, SampleArrayTypeSupport.get_type_name());
        sampleArrayTopic = domainParticipant.create_topic(ice.SampleArrayTopic.VALUE, SampleArrayTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        sampleArrayDataWriter = (SampleArrayDataWriter) publisher.create_datawriter(sampleArrayTopic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        if(null == sampleArrayDataWriter) {
            throw new RuntimeException("sampleArrayDataWriter not created");
        }
        
		threadGroup = new ThreadGroup(Thread.currentThread().getThreadGroup(), "AbstractDevice") {
    		  @Override
    		public void uncaughtException(Thread t, Throwable e) {
    		    log.error("Thrown by " + t.toString(), e);
    		    super.uncaughtException(t, e);
    		}  
		};
		threadGroup.setDaemon(true);
		this.eventLoop = eventLoop;
	}
}
