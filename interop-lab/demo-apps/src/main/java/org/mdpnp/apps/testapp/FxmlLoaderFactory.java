package org.mdpnp.apps.testapp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Spring lifecycle interceptor to perform additional initialization of the object - such as
 * fxml loading and data binding before it is returned to the context.
 *
 * @param <T>
 */
public class FxmlLoaderFactory<T> implements BeanPostProcessor, InitializingBean, ApplicationContextAware
{
    private static final Logger log = LoggerFactory.getLogger(FxmlLoaderFactory.class);

    /**
     * map of screen definition files to the bean names of beans that should be used as controllers.
     */
    Map<String,String> screenToBeanNameMap = new HashMap<>();
    /**
     * map of all realized screens keyed by the fxml file name.
     */
    Map<String, Parent> loadedScreens = new HashMap<>();

    public FxmlLoaderFactory() {
    }

    public Map<String, String> getScreenDefinitions() {
        return screenToBeanNameMap;
    }

    public void setScreenDefinitions(Map<String, String> v) {
        screenToBeanNameMap = v;
    }

    public Parent getScreen(String screenName) {
        return loadedScreens.get(screenName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for (Map.Entry<String, String> entry : screenToBeanNameMap.entrySet()) {
            if(entry.getValue().equals(beanName)) {
                loadFxXmlURL(entry.getKey(), bean);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * @param fxXmlURL string pointing to the location of the fxml file.
     * @param bean bean to use as a controller
     * @returnui widget created by this load.
     */
    Parent loadFxXmlURL(final String fxXmlURL, final Object bean)  {

        InputStream fxmlStream = null;
        try
        {
            Callback<Class<?>, Object> factory = new Callback<Class<?>, Object>()
            {
                public Object call(Class<?> type) {
                    if(type.isAssignableFrom(bean.getClass()))
                        return bean;
                    else
                        throw new RuntimeException("Bean " + bean.getClass().getCanonicalName() +
                                                   " is incompatible with " + type.getCanonicalName());
                }
            };

            fxmlStream = bean.getClass().getResourceAsStream(fxXmlURL);
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(factory);
            Parent ui = loader.load(fxmlStream);
            loadedScreens.put(fxXmlURL, ui);
            log.info("Done with" + fxXmlURL + " load");
            return ui;
        }
        catch(Exception ex) {
            throw new RuntimeException("Failed to load " + fxXmlURL, ex);
        }
        finally
        {
            if (fxmlStream != null)
            {
                try {
                    fxmlStream.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext c) throws BeansException {
        applicationContext = c;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        verifyBeans(screenToBeanNameMap);
    }

    /**
     * verify that all beans listed as ui controllers for the purpose of fxml augmentation are
     * actually present in the context.
     */
    void verifyBeans(Map<String, String> v)
    {
        Set<String> controllers = new HashSet<String>();
        if(v != null) controllers.addAll(v.values());

        String[] all = applicationContext.getBeanDefinitionNames();
        for (String name : all) {
            controllers.remove(name);
        }

        for (String name : controllers) {
            throw new IllegalArgumentException("UI controller bean " + name + " is not registered in the context");
        }

        // To get more metadata from the bean definition:
        // ConfigurableListableBeanFactory clbf = ((AbstractApplicationContext) ctx).getBeanFactory();
        // BeanDefinition clbf.getBeanDefinition(name);

    }

    ApplicationContext applicationContext;
}







