package org.mdpnp.apps.testapp.export;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

import com.google.common.eventbus.Subscribe;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;

import org.mdpnp.apps.testapp.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;

public class DeviceTreeModel extends SelectableNode implements ListChangeListener<Device> {

    private static final Logger log = LoggerFactory.getLogger(DeviceTreeModel.class);

    public DeviceTreeModel() {
        super("ICE", true);
        setExpanded(true);
        setSelected(true);
    }

    protected SelectableNode makeNewNodeFactory(TreeItem<Object> parent, Object uData)
    {
        boolean sel = parent instanceof SelectableNode ? ((SelectableNode)parent).isSelected() : true;
        final SelectableNode node = new SelectableNode(uData, sel);
        node.selectedProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                cascadeChildren(node, node.isSelected());
            }
            
        });
        return node;
    }

    private void cascadeChildren(SelectableNode node, boolean v) {
        node.setSelected(v);
        if(!node.isLeaf()) {
            Iterator<TreeItem<Object>> iter = node.getChildren().iterator();
            while (iter.hasNext()) {
                Object o = iter.next();
                if(o instanceof SelectableNode) {
                    SelectableNode n = (SelectableNode) o;
                    cascadeChildren(n, v);
                }
            }
        }
    }

    @Subscribe
    public void handleDataSampleEvent(NumericsDataCollector.NumericSampleEvent evt) throws Exception {

        final Value value = evt.getValue();

        // This call back is going to happen A LOT. need fast lookup of the nodes.
        // And only of we see this for the first timer burden the FX thread with
        // tree model modifications.
        //
        final String key = toKey(value);

        if (nodeLookup.get(key) == null) {
            Iterator<TreeItem<Object>> iter = getChildren().iterator();
            while (iter.hasNext()) {
                TreeItem<Object> dn = (TreeItem<Object>) iter.next();
                Device d = (Device) dn.getValue();
                if (d.getUDI().equals(value.getUniqueDeviceIdentifier())) {
                    final TreeItem<Object> mn = ensureMetricNode(dn, value);
                    final TreeItem<Object> in = ensureInstanceNode(mn, value);

                    nodeLookup.put(key, in);

                    log.debug("adding to the tree: {}", key);
                }
            }
        }
    }

    static String toKey(Value value) {
        return value.getUniqueDeviceIdentifier() + "/" + value.getMetricId() + "/" + value.getInstanceId();
    }

    TreeItem<Object> ensureMetricNode(TreeItem<Object> d, Value value)
    {
        Iterator<TreeItem<Object>> iter = d.getChildren().iterator();
        while (iter.hasNext()) {
            TreeItem<Object> tn = (TreeItem<Object>) iter.next();
            if (tn.getValue().equals(value.getMetricId())) {
                    return tn;
            }
        }

        TreeItem<Object> tn = makeNewNodeFactory(d, value.getMetricId());
        d.getChildren().add(tn);
        return tn;
    }

    TreeItem<Object> ensureInstanceNode(TreeItem<Object> d, Value value)
    {
        Iterator<TreeItem<Object>> iter = d.getChildren().iterator();
        while (iter.hasNext()) {
            TreeItem<Object> tn = (TreeItem<Object>) iter.next();
            if (tn.getValue().equals(value.getInstanceId())) {
                return tn;
            }
        }

        TreeItem<Object> tn = makeNewNodeFactory(d, value.getInstanceId());
        d.getChildren().add(tn);
        return tn;
    }

    public boolean isEnabled(Value value)
    {
        // this call back is going to happen A LOT. need fast lookup of the nodes.
        //
        final String key = toKey(value);
        TreeItem<Object> node = nodeLookup.get(key);
        if(node instanceof SelectableNode) {
            return ((SelectableNode)node).isSelected();
        }
        return false;
    }

    ConcurrentMap<String, TreeItem<Object>> nodeLookup = new MapMaker().weakValues().makeMap();

    @Override
    public void onChanged(javafx.collections.ListChangeListener.Change<? extends Device> c) {
        while(c.next()) {
            for(Device d : c.getRemoved()) {
                log.info("Device Removed", d.toString());

                Iterator<TreeItem<Object>> itr = getChildren().iterator();
                while(itr.hasNext()) {
                    if(d.equals(itr.next().getValue())) {
                        itr.remove();
                    }
                }
            }
            for(Device d : c.getAddedSubList()) {
                log.info("Device Added", d.toString());
                // TODO preserve the sort order of the underlying list
                getChildren().add(makeNewNodeFactory(this, d));
            }
        }
        
    }

}
