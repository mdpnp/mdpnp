package org.mdpnp.apps.testapp.vital;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;

public class VitalModelNumericProvider implements ListChangeListener<NumericFx> {

    private final VitalModel model;
    private final NumericFxList numericList;
    
    public VitalModelNumericProvider(final VitalModel model, final NumericFxList numericList) {
        this.model = model;
        this.numericList = numericList;
        
        Platform.runLater( () -> {
            numericList.addListener(VitalModelNumericProvider.this);
            numericList.forEach((fx) -> model.addNumeric(fx));
        });
    }

    @Override
    public void onChanged(javafx.collections.ListChangeListener.Change<? extends NumericFx> c) {
        while(c.next()) {
            c.getRemoved().forEach( (fx) -> model.removeNumeric(fx));
            c.getAddedSubList().forEach((fx) -> model.addNumeric(fx));
        }
    }
}
