package org.mdpnp.apps.testapp.validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mdpnp.apps.fxbeans.NumericFx;

import javafx.collections.ModifiableObservableListBase;

public class ValidationOracle extends ModifiableObservableListBase<Validation> {

    private final List<Validation> backing = new ArrayList<Validation>();
    private final Map<NumericFx, Validation> randomAccess = new HashMap<NumericFx, Validation>();
    
    public Validation getByNumeric(NumericFx numeric) {
        return randomAccess.get(numeric);
    }
    
    @Override
    public Validation get(int index) {
        return backing.get(index);
    }

    @Override
    public int size() {
        return backing.size();
    }

    @Override
    protected void doAdd(int index, Validation element) {
        backing.add(index, element);
        randomAccess.put(element.getNumeric(), element);
    }

    @Override
    protected Validation doSet(int index, Validation element) {
        Validation oldElement = backing.set(index, element);
        randomAccess.remove(oldElement.getNumeric());
        randomAccess.put(element.getNumeric(), element);
        return oldElement;
    }

    @Override
    protected Validation doRemove(int index) {
        Validation oldElement = backing.remove(index);
        randomAccess.remove(oldElement.getNumeric());
        return oldElement;
    }

}
