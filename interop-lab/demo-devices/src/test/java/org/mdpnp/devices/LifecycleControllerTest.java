package org.mdpnp.devices;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class LifecycleControllerTest {

    private static final Logger log = LoggerFactory.getLogger(LifecycleControllerTest.class);

    @Test
    public void testBeanInvocation() throws Exception {

        List<String> res = new ArrayList<>();

        LifecycleController srv = new LifecycleController();
        srv.setStartMethod("start");

        List<Object> o = new ArrayList<>();
        o.add(new ManagedBean("ONE", res));
        o.add(new Object());
        o.add(new ManagedBean("TWO", res));
        srv.setManagedBeans(o);

        srv.onApplicationEvent(null);

        Assert.assertEquals(2, res.size());
        Assert.assertEquals("ONE", res.get(0));
        Assert.assertEquals("TWO", res.get(1));

    }

    static class ManagedBean {

        final String beanName;
        final List<String> res;

        public ManagedBean(String n, List<String> l) {
            beanName = n;
            res = l;
        }

        public void start() {
            log.info("Starting " + beanName);
            res.add(beanName);
        }
    }
}


