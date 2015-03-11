package org.mdpnp.apps.testapp.export;

import javafx.scene.control.CheckBoxTreeItem;

class SelectableNode extends CheckBoxTreeItem<Object>
{
    public SelectableNode(Object userObject, boolean sel) {
        super(userObject);
    }
}
