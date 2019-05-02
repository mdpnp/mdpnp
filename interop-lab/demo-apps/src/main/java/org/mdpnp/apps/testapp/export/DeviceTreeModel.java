package org.mdpnp.apps.testapp.export;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;

import org.mdpnp.apps.testapp.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        // This call back is going to happen A LOT. need fast lookup of the nodes.
        // And only of we see this for the first timer burden the FX thread with
        // tree model modifications.
        //
        final String key = toKey(evt);

        if (nodeLookup.get(key) == null) {
            Platform.runLater(() ->
            {
                Iterator<TreeItem<Object>> iter = getChildren().iterator();
                while (iter.hasNext()) {
                    TreeItem<Object> deviceNode = (TreeItem<Object>) iter.next();
                    Device d = (Device) deviceNode.getValue();
                    if (d.getUDI().equals(evt.getUniqueDeviceIdentifier())) {
                        final TreeItem<Object> metricNode = ensureNode(deviceNode, evt.getMetricId());
                        final TreeItem<Object> instanceNode = ensureNode(metricNode, evt.getInstanceId());

                        nodeLookup.put(key, instanceNode);

                        log.debug("adding to the tree: {}", key);
                    }
                }
            });
        }
    }

    static String toKey(DataCollector.DataSampleEvent value) {
        return value.getUniqueDeviceIdentifier() + "/" + value.getMetricId() + "/" + value.getInstanceId();
    }

    TreeItem<Object> ensureNode(TreeItem<Object> d, Object key)
    {
        Iterator<TreeItem<Object>> iter = d.getChildren().iterator();
        while (iter.hasNext()) {
            TreeItem<Object> tn = (TreeItem<Object>) iter.next();
            if (tn.getValue().equals(key)) {
                return tn;
            }
        }

        TreeItem<Object> tn = makeNewNodeFactory(d, key);
        TreeItem testParent=tn;
        d.getChildren().add(tn);
        if( ((SelectableNode)d).isSelected()) {
        	cascadeChildren((SelectableNode)tn, true);
        }

        return tn;
    }

    public boolean isEnabled(DataCollector.DataSampleEvent value)
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

    ConcurrentHashMap<String, TreeItem<Object>> nodeLookup =new ConcurrentHashMap();

    @Override
    public void onChanged(javafx.collections.ListChangeListener.Change<? extends Device> c) {
        while(c.next()) {
            for(Device d : c.getRemoved()) {
                log.info("Device Removed", d.toString());

                for (String key : nodeLookup.keySet()) {
                    if(key.startsWith(d.getUDI()))
                        nodeLookup.remove(key);
                }

                Iterator<TreeItem<Object>> itr = getChildren().iterator();
                while(itr.hasNext()) {
                    final TreeItem<Object> node = itr.next();
                    if(d.equals(node.getValue())) {
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
