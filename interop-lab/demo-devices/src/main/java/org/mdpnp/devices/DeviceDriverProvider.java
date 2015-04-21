package org.mdpnp.devices;

import ice.ConnectionType;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
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
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 */
public interface DeviceDriverProvider {

    static final Logger log = LoggerFactory.getLogger(SpringLoadedDriver.class);

    DeviceType     getDeviceType();
    AbstractDevice newInstance(AbstractApplicationContext context) throws Exception;
    DeviceAdapter  create(AbstractApplicationContext context) throws Exception;

    /**
     * An interface for the device driver that is presented to the rest of the system. The base AbstractDevice object
     * became too overloaded with being a responsible for both device interactions and assembly of infrastructure that
     * a higher-level entity evolved. Though we moved all drivers to be assembled via spring ioc container, any
     * alternative implementation will be supported as long as the interface is implemented.
     *
     * @see SpringDecorator
     */
    public interface DeviceAdapter  {

        enum AdapterState { init, connected, stopped };

        <T> T getComponent(String name, Class<T> requiredType) throws Exception;
        <T> T getComponent(Class<T> requiredType) throws Exception;

        AbstractDevice getDevice();

        void stop();

        void setPartition(String[] v);
        void setAddress(String address);

        boolean connect();
        void disconnect();

        void addObserver(Observer v);
        void deleteObserver(Observer v);
    }

    public static class DeviceType {

        private final ice.ConnectionType connectionType;
        private final String manufacturer, model, aliases[];
        private final int connectionCount;

        public DeviceType(ice.ConnectionType connectionType, String manufacturer, String model, String alias, int connectionCount) {
            this(connectionType, manufacturer, model, new String[] { alias }, connectionCount);
        }
        
        public DeviceType(ice.ConnectionType connectionType, String manufacturer, String model, String[] alias, int connectionCount) {
            this.connectionType = connectionType;
            this.manufacturer = manufacturer;
            this.model = model;
            this.aliases = alias;
            this.connectionCount = connectionCount;
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
        
        public int getConnectionCount() {
            return connectionCount;
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


    /**
     * An implementation of DeviceDriverProvider that is using spring container for
     * the assembly of the component. String configuration could be very different
     * depending on sophistication of the driver, but for a some of them beans provided
     * in the basic DriverContext.xml should be sufficient. The purpose of this wrapper is
     * to initialize the context in such a way that driverFactoryProcessor bean produces a
     * driver of a desired type.
     */
    public abstract static class SpringLoadedDriver implements DeviceDriverProvider {

        protected String getContextPath() {
            return "classpath*:/DriverContext.xml";
        }

        @Override
        public DeviceAdapter create(AbstractApplicationContext parentContext) throws Exception {

            String contextPath = getContextPath();

            AbstractApplicationContext context =
                    new ClassPathXmlApplicationContext(new String[] { contextPath }, false, parentContext);

            // set the context name to something readable and unique. this name will be used
            // to create jmx names for the beans that are to be exposed for management.
            //
            context.setDisplayName(getDeviceType().toString());
            context.setId(getDeviceType().getAlias() + hashCode());

            // create a post processor to inject a device factory with the appropriate device
            // implementation.
            //
            BeanPostProcessor bpp = new BeanPostProcessor() {
                @Override
                public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
                    if(o instanceof AbstractDeviceFactory) {
                        ((AbstractDeviceFactory)o).setDriverProvider(SpringLoadedDriver.this);
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

            // now create them all.
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

            return new SpringDecorator(context);
        }
    }

    /**
     * factory to create an instance of the device adapter for a particular device driver. It is used
     * in the generic DriverContext.xml spring configuration file. Not an ideal thing as we do a few
     * autowiring things here that the container should be doing for us, but the alternative is for
     * force each driver to have its own spring configuration. Dunno.
     */
    public static class AbstractDeviceFactory implements FactoryBean<AbstractDevice>, ApplicationContextAware {

        @Override
        public AbstractDevice getObject() throws Exception {
            if(instance == null) {
                DeviceDriverProvider.DeviceType type = driverProvider.getDeviceType();

                log.trace("Create DeviceAdapter with type=" + type);
                if (ConnectionType.Network.equals(type.getConnectionType())) {
                    SerialProviderFactory.setDefaultProvider(new TCPSerialProvider());
                    log.info("Using the TCPSerialProvider, be sure you provided a host:port target");
                }

                instance = driverProvider.newInstance((AbstractApplicationContext)context);
                instance.setExecutor(executor);
                instance.init();
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

        public void setDriverProvider(DeviceDriverProvider driverProvider) {
            this.driverProvider = driverProvider;
        }

        public void setExecutor(ScheduledExecutorService executor) {
            this.executor = executor;
        }

        private AbstractDevice instance;
        private ApplicationContext context;
        private DeviceDriverProvider driverProvider;
        private ScheduledExecutorService executor;
    }

    /**
     * utility to pick unique names for jmx beans - jmx namespace is flat so we need to suffix all
     * managed beans with the id of the context they came from.
     */
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

    static class SpringDecorator extends Observable implements DeviceAdapter {

        private final AbstractApplicationContext context;
        private  String address=null;
        public SpringDecorator(AbstractApplicationContext context) {
            this.context = context;
        }

        @Override
        public void setPartition(String[] partition) {
            PartitionAssignmentController pac = context.getBean(PartitionAssignmentController.class);
            pac.setPartition(partition);
        }

        @Override
        public AbstractDevice getDevice() {
            AbstractDevice device = context.getBean(org.mdpnp.devices.AbstractDevice.class);
            return device;
        }

        @Override
        public <T> T getComponent(String name, Class<T> requiredType) throws Exception {
            return context.getBean(name, requiredType);
        }

        @Override
        public <T> T getComponent(Class<T> requiredType) throws Exception {
            return context.getBean(requiredType);
        }

        @Override
        public void stop() {
            context.destroy();

            setChanged();
            notifyObservers(AdapterState.stopped);
        }

        @Override
        public void setAddress(String v) {
            address = v;
        }

        @Override
        public void addObserver(Observer v) {
            super.addObserver(v);
        }

        @Override
        public void deleteObserver(Observer v) {
            super.deleteObserver(v);
        }

        @Override
        public boolean connect() {
            AbstractDevice device = getDevice();
            if(device == null)
                throw new IllegalStateException("Cannot connect - null device");
            if (device instanceof AbstractConnectedDevice) {
                log.info("Connecting to >" + address + "<");
                boolean b = ((AbstractConnectedDevice) device).connect(address);
                if(b) {
                    setChanged();
                    notifyObservers(AdapterState.connected);
                }
                return b;
            }
            else
                return true;
        }

        @Override
        public void disconnect()
        {
            AbstractDevice device = getDevice();
            if (null != device && device instanceof AbstractConnectedDevice) {
                AbstractConnectedDevice cDevice = (AbstractConnectedDevice) device;
                cDevice.disconnect();
                if (!cDevice.awaitState(ice.ConnectionState.Terminal, 5000L)) {
                    log.warn("ConnectedDevice ended in State:" + cDevice.getState());
                }
            }
        }
    }
}
