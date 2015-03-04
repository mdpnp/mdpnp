package org.mdpnp.apps.testapp.export;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.vital.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;

public class DeviceTreeModel extends SelectableNode implements ListChangeListener<Device>, DataCollector.DataSampleEventListener {

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

    public static String textForNode(Object o) {
        String txt="";
        if(o instanceof TreeItem) {
            o = ((TreeItem)o).getValue();
        }
        if(o instanceof Device) {
            Device d = (Device)o;
            if(d.getMakeAndModel() != null)
                txt = d.getMakeAndModel() + " (" + d.getUDI() + ")";
            else
                txt = d.getUDI();
        }
        else if(o != null) {
            txt = o.toString();
        }
        return txt;
    }

//    @Override
//    public void valueForPathChanged(TreePath path, Object newValue) {
//        MutableTreeNode aNode = (MutableTreeNode)path.getLastPathComponent();
//        if(aNode instanceof SelectableNode && newValue instanceof Boolean)  {
//            cascadeChildren((SelectableNode)aNode, (Boolean)newValue);
//        }
//        else {
//            super.valueForPathChanged(path, newValue);
//        }
//    }

    private void cascadeChildren(SelectableNode node, boolean v) {
        node.setSelected(v);
//        nodeChanged(node);
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

    @Override
    public void handleDataSampleEvent(DataCollector.DataSampleEvent evt) throws Exception {

        final Value value = (Value) evt.getSource();

        // This call back is going to happen A LOT. need fast lookup of the nodes.
        // And only of we see this for the first timer burden the swing thread with
        // tree model modifications.
        //
        final String key = toKey(value);

        if (nodeLookup.get(key) == null) {
            @SuppressWarnings("rawtypes")
            Iterator<TreeItem<Object>> iter = getChildren().iterator();
            while (iter.hasNext()) {
                TreeItem<Object> dn = (TreeItem<Object>) iter.next();
                Device d = (Device) dn.getValue();
                if (d.getUDI().equals(value.getUniqueDeviceIdentifier())) {
                    final TreeItem<Object> mn = ensureMetricNode(dn, value);
                    final TreeItem<Object> in = ensureInstanceNode(mn, value);

                    nodeLookup.put(key, in);

                    log.debug("adding to the tree: " + key);

//                    int idx = mn.getIndex(in);
//                    Object path[] = getPathToRoot(mn);
//                    DeviceTreeModel.this.fireTreeNodesInserted(
//                            DeviceTreeModel.this,
//                            path,
//                            new int[]{idx},
//                            new Object[]{in});
                }
            }
        }
    }

    static String toKey(Value value) {
        return value.getUniqueDeviceIdentifier() + "/" + value.getMetricId() + "/" + value.getInstanceId();
    }

    TreeItem<Object> ensureMetricNode(TreeItem<Object> d, Value value)
    {
        @SuppressWarnings("rawtypes")
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
            if(c.wasPermutated()) {
                // what does this mean? indices changed?
            } else if(c.wasUpdated()) {
                // heartbeat message that we do not cared about
            } else {
                for(Device d : c.getRemoved()) {
                    log.info("Device Removed", d.toString());

                    @SuppressWarnings("rawtypes")
                    Iterator<TreeItem<Object>> itr = getChildren().iterator();
                    while(itr.hasNext()) {
                        if(d.equals(itr.next())) {
                            itr.remove();
                        }
                    }
                }
                for(Device d : c.getAddedSubList()) {
                    log.info("Device Added", d.toString());
                    // TODO preserve the sort order of the underlying list
                    
                    getChildren().add(makeNewNodeFactory(this, d));

//                    reload();  
                }
            }
        }
        
    }

}
