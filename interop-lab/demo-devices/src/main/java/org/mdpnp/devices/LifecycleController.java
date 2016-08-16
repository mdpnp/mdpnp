package org.mdpnp.devices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class LifecycleController implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(LifecycleController.class);

    private List managedBeans = new ArrayList();
    private String startMethodName="start";
    private ApplicationContext applicationContext;

    public void setManagedBeans(List managedBeans) {
        this.managedBeans = managedBeans;
    }

    public void setStartMethod(String startMethodName) {
        this.startMethodName = startMethodName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        // not my context...
        if(contextRefreshedEvent != null && contextRefreshedEvent.getSource() != applicationContext)
            return;

        log.info("Got ContextRefreshedEvent from " + (applicationContext==null?"????":applicationContext.getId()));

        int count=0;

        for(Object bean : managedBeans) {

            try {
                Method m = BeanUtils.findMethod(bean.getClass(), startMethodName);
                m.invoke(bean);
                count++;
            }
            catch(Exception ex) {
                log.error("Failed to call " + bean.getClass() + "::" + startMethodName);
            }
        }

        log.info("Successfully called " + startMethodName + " on " + count + " beans");

    }
}
