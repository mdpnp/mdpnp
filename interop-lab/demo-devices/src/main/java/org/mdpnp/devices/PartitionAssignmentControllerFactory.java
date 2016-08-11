package org.mdpnp.devices;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import ice.DeviceIdentity;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.ScheduledExecutorService;


/**
 *
 */
public class PartitionAssignmentControllerFactory implements FactoryBean<PartitionAssignmentController>, DisposableBean {

    private PartitionAssignmentController instance;

    private ScheduledExecutorService executor;
    private EventLoop eventLoop;
    private Subscriber subscriber;
    private Publisher publisher;
    private DeviceIdentity deviceIdentity;
    private DomainParticipant domainParticipant;
    private String partitionFileName=null;

    @Override
    public PartitionAssignmentController getObject() throws Exception {
        if(instance == null) {
            String f = partitionFileName;
            if(f == null || f.length() == 0)
                instance = new PartitionAssignmentController(deviceIdentity, domainParticipant, eventLoop, publisher, subscriber);
            else
                instance = new PartitionAssignmentController.PersistentPartitionAssignment(executor, f, deviceIdentity, domainParticipant, eventLoop, publisher, subscriber);
        }
        instance.start();
        return instance;
    }

    public PartitionAssignmentControllerFactory(ScheduledExecutorService executor,
                                                DomainParticipant domainParticipant,
                                                EventLoop eventLoop,
                                                Publisher publisher,
                                                Subscriber subscriber,
                                                DeviceIdentity deviceIdentity) {
        this(executor, domainParticipant, eventLoop, publisher, subscriber, deviceIdentity, null);
    }

    public PartitionAssignmentControllerFactory(ScheduledExecutorService executor,
                                                DomainParticipant domainParticipant,
                                                EventLoop eventLoop,
                                                Publisher publisher,
                                                Subscriber subscriber,
                                                DeviceIdentity deviceIdentity,
                                                String partitionFileName) {
        this.executor = executor;
        this.domainParticipant = domainParticipant;
        this.eventLoop = eventLoop;
        this.publisher = publisher;
        this.subscriber = subscriber;
        this.deviceIdentity = deviceIdentity;
        this.partitionFileName = partitionFileName;
    }

    @Override
    public Class<?> getObjectType() {
        return PartitionAssignmentController.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if(instance != null) {
            instance.shutdown();
        }
    }
}
