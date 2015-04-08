package org.mdpnp.devices;

import ice.ConnectionType;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.TCPSerialProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Hashtable;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 */
public interface DeviceDriverProvider {

    DeviceType     getDeviceType();
    AbstractDevice newInstance(AbstractApplicationContext context) throws Exception;
    Handle create(AbstractApplicationContext context) throws Exception;

    // MIKEFIX merge this into device adapter somehow maybe?
    public interface Handle {
        AbstractDevice getImpl();
        void shutdown();
        void setPartition(String[] partition);
        <T> T getComponent(String name, Class<T> requiredType) throws Exception;
        <T> T getComponent(Class<T> requiredType) throws Exception;
    }

    public static class DeviceType {

        private final ice.ConnectionType connectionType;
        private final String manufacturer, model, aliases[];

        public DeviceType(ice.ConnectionType connectionType, String manufacturer, String model, String alias) {
            this(connectionType, manufacturer, model, new String[] { alias });
        }

        public DeviceType(ice.ConnectionType connectionType, String manufacturer, String model, String[] alias) {
            this.connectionType = connectionType;
            this.manufacturer = manufacturer;
            this.model = model;
            this.aliases = alias;
        }

        public ice.ConnectionType getConnectionType() {
            return connectionType;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public String getModel() {
            return model;
        }

        public String[] getAliases() {
            return aliases;
        }

        public String getAlias() {
            return aliases[0];
        }

        @Override
        public String toString() {
            return manufacturer + " " + model;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DeviceType that = (DeviceType) o;

            if (!connectionType.equals(that.connectionType)) return false;
            if (!manufacturer.equals(that.manufacturer)) return false;
            if (!model.equals(that.model)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = connectionType.hashCode();
            result = 31 * result + manufacturer.hashCode();
            result = 31 * result + model.hashCode();
            return result;
        }
    }


    public abstract static class SpringLoaderDriver implements DeviceDriverProvider {

        private static final Logger log = LoggerFactory.getLogger(SpringLoaderDriver.class);

        protected String getContextPath() {
            return "classpath*:/DriverContext.xml";
        }

        @Override
        public DeviceDriverProvider.Handle create(AbstractApplicationContext parentContext) throws Exception {

            String contextPath = getContextPath();

            AbstractApplicationContext context =
                    new ClassPathXmlApplicationContext(new String[] { contextPath }, false, parentContext);

            context.setDisplayName(getDeviceType().toString());
            context.setId(getDeviceType().getAlias() + hashCode());

            BeanPostProcessor bpp = new BeanPostProcessor() {
                @Override
                public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
                    if(o instanceof AbstractDeviceFactory) {
                        ((AbstractDeviceFactory)o).setDeviceFactory(SpringLoaderDriver.this);
                    }
                    return o;
                }

                @Override
                public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
                    return o;
                }
            };

            context.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor()
            {
                @Override
                public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
                    configurableListableBeanFactory.registerSingleton("driverFactoryProcessor", bpp);
                }
            });

            context.refresh();

            parentContext.addApplicationListener(new ApplicationListener<ContextClosedEvent>()
            {
                @Override
                public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
                    // only care to trap parent close events to kill the child context
                    if(parentContext == contextClosedEvent.getApplicationContext()) {
                        log.info("Handle parent context shutdown event");
                        context.close();
                    }
                }
            });

            return new SpringHandle(context);
        }
    }

    public static class AbstractDeviceFactory implements FactoryBean<AbstractDevice>, ApplicationContextAware {

        private static final Logger log = LoggerFactory.getLogger(AbstractDeviceFactory.class);

        @Override
        public AbstractDevice getObject() throws Exception {
            if(instance == null) {
                DeviceDriverProvider.DeviceType type = deviceFactory.getDeviceType();

                log.trace("Create DeviceAdapter with type=" + type);
                if (ConnectionType.Network.equals(type.getConnectionType())) {
                    SerialProviderFactory.setDefaultProvider(new TCPSerialProvider());
                    log.info("Using the TCPSerialProvider, be sure you provided a host:port target");
                }

                instance = deviceFactory.newInstance((AbstractApplicationContext)context);
                instance.setExecutor(executor);
            }
            return instance;
        }

        @Override
        public Class<?> getObjectType() {
            return AbstractDevice.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

        @Override
        public void setApplicationContext(ApplicationContext ac) throws BeansException {
            context = ac;
        }

        public void shutdown() {
            if(instance != null)
                instance.shutdown();
        }

        public void setDeviceFactory(DeviceDriverProvider deviceFactory) {
            this.deviceFactory = deviceFactory;
        }

        public void setExecutor(ScheduledExecutorService executor) {
            this.executor = executor;
        }

        private AbstractDevice instance;
        private ApplicationContext context;
        private DeviceDriverProvider deviceFactory;
        private ScheduledExecutorService executor;
    }

    public static class DeviceFactoryNamingStrategy implements ObjectNamingStrategy,ApplicationContextAware {

        @Override
        public ObjectName getObjectName(Object o, String s) throws MalformedObjectNameException {

            Hashtable<String, String> m = new Hashtable<>();
            m.put("service", s);
            ObjectName on = new ObjectName("mdpnp.driver." + context.getId(), m);
            return on;
        }

        @Override
        public void setApplicationContext(ApplicationContext ac) throws BeansException {
            context = ac;
        }
        protected ApplicationContext context;
    }

    public static class SpringHandle implements DeviceDriverProvider.Handle {

        final AbstractApplicationContext context;

        public SpringHandle(AbstractApplicationContext context) {
            this.context = context;
        }

        public void setPartition(String[] partition) {
            PartitionAssignmentController pac = context.getBean(PartitionAssignmentController.class);
            pac.setPartition(partition);
        }

        public AbstractDevice getImpl() {
            AbstractDevice device = context.getBean(org.mdpnp.devices.AbstractDevice.class);
            return device;
        }

        public <T> T getComponent(String name, Class<T> requiredType) throws Exception {
            return context.getBean(name, requiredType);
        }

        public <T> T getComponent(Class<T> requiredType) throws Exception {
            return context.getBean(requiredType);
        }

        public void shutdown() {
            context.destroy();
        }
    }

    public static class NativeHandle implements DeviceDriverProvider.Handle {

        final AbstractDevice device;

        public NativeHandle(AbstractDevice device) {
            this.device = device;
        }

        public AbstractDevice getImpl() {
            return device;
        }

        public void setPartition(String[] partition) {
            throw new UnsupportedOperationException("Define what 'setPartition' means on 'AbstractDevice'");
        }

        public <T> T getComponent(String name, Class<T> requiredType) throws Exception {
            throw new UnsupportedOperationException("Define what 'getComponent' means on 'AbstractDevice'");
        }

        public <T> T getComponent(Class<T> requiredType) throws Exception {
            throw new UnsupportedOperationException("Define what 'getComponent' means on 'AbstractDevice'");
        }

        public void shutdown() {
            device.shutdown();
        }
    }
}
