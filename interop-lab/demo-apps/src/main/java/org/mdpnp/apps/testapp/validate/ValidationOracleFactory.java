package org.mdpnp.apps.testapp.validate;

import javafx.collections.ObservableList;

import org.mdpnp.apps.device.OnListChange;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class ValidationOracleFactory implements FactoryBean<ValidationOracle>, DisposableBean {

    private ValidationOracle instance;
    
    private ObservableList<NumericFx> numericList;
    
    public ObservableList<NumericFx> getNumericList() {
        return numericList;
    }
    public void setNumericList(ObservableList<NumericFx> numericList) {
        this.numericList = numericList;
    }
    
    @Override
    public void destroy() throws Exception {
        if(numericList != null) {
            numericList.removeListener(numericListener);
            numericList.forEach((t)->instance.remove(new Validation(t)));
        }
    }

    private OnListChange<NumericFx> numericListener = new OnListChange<NumericFx>(
            (t)->instance.add(new Validation(t)),
            null,
            (t)->instance.remove(new Validation(t)));
    
    @Override
    public ValidationOracle getObject() throws Exception {
        if(null == instance) {
            instance = new ValidationOracle();
            if(null != numericList) {
                numericList.addListener(numericListener);
                numericList.forEach((t)->instance.add(new Validation(t)));
            }
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return ValidationOracle.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
