package org.mdpnp.apps.testapp.comboboxfix;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.KeyBinding;

import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.UP;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

public class ComboBoxListViewBehavior<T> extends ComboBoxBaseBehavior<T> {

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/
    
    /**
     * 
     */
    public ComboBoxListViewBehavior(final FixedComboBox<T> comboBox) {
        super(comboBox, COMBO_BOX_BINDINGS);
    }

    /***************************************************************************
     *                                                                         *
     * Key event handling                                                      *
     *                                                                         *
     **************************************************************************/

    protected static final List<KeyBinding> COMBO_BOX_BINDINGS = new ArrayList<KeyBinding>();
    static {
        COMBO_BOX_BINDINGS.add(new KeyBinding(UP, KEY_PRESSED, "selectPrevious"));
        COMBO_BOX_BINDINGS.add(new KeyBinding(DOWN, "selectNext"));
        COMBO_BOX_BINDINGS.addAll(COMBO_BOX_BASE_BINDINGS);
    }

    @Override protected void callAction(String name) {
        if ("selectPrevious".equals(name)) {
            selectPrevious();
        } else if ("selectNext".equals(name)) {
            selectNext();
        } else {
            super.callAction(name);
        }
    }
    
    private FixedComboBox<T> getComboBox() {
        return (FixedComboBox<T>) getControl();
    }

    private void selectPrevious() {
        SelectionModel<T> sm = getComboBox().getSelectionModel();
        if (sm == null) return;
        sm.selectPrevious();
    }
    
    private void selectNext() {
        SelectionModel<T> sm = getComboBox().getSelectionModel();
        if (sm == null) return;
        sm.selectNext();
    }
}
