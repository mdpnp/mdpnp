package org.mdpnp.apps.testapp.export;

import com.google.common.collect.MapMaker;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.vital.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentMap;

public class DeviceTreeModel extends DefaultTreeModel implements ListDataListener, DataCollector.DataSampleEventListener {

    private static final Logger log = LoggerFactory.getLogger(DeviceTreeModel.class);

    public DeviceTreeModel() {
        super(new DefaultMutableTreeNode("ICE"));
    }

    protected DefaultMutableTreeNode makeNewNodeFactory(DefaultMutableTreeNode parent, Object uData)
    {
        boolean sel = parent instanceof SelectableNode ? ((SelectableNode)parent).isSelected() : true;
        return new SelectableNode(uData, sel); // DefaultMutableTreeNode(uData);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        MutableTreeNode aNode = (MutableTreeNode)path.getLastPathComponent();
        if(aNode instanceof SelectableNode && newValue instanceof Boolean)  {
            cascadeChildren((SelectableNode)aNode, (Boolean)newValue);
        }
        else {
            super.valueForPathChanged(path, newValue);
        }
    }

    private void cascadeChildren(SelectableNode node, boolean v) {
        node.setSelected(v);
        nodeChanged(node);
        if(!node.isLeaf()) {
            Enumeration iter = node.children();
            while (iter.hasMoreElements()) {
                SelectableNode n = (SelectableNode) iter.nextElement();
                cascadeChildren(n, v);
            }
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {

        log.info("intervalAdded", e.toString());

        AbstractListModel<Device> dlm = (AbstractListModel<Device>)e.getSource();

        for(int idx=e.getIndex0(); idx<=e.getIndex1(); idx++) {
            Device d = dlm.getElementAt(idx);
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) super.getRoot();
            root.add(makeNewNodeFactory(root, d));
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                reload();  
            }
        });
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {

        log.info("intervalRemoved", e.toString());

        AbstractListModel<Device> dlm = (AbstractListModel<Device>)e.getSource();

        for(int idx=e.getIndex0(); idx<=e.getIndex1(); idx++) {
            Device d = dlm.getElementAt(idx);
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) super.getRoot();
            Enumeration iter = root.children();
            while (iter.hasMoreElements()) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode)iter.nextElement();
                if(n.getUserObject().equals(d)) {
                    root.remove(n);
                    break;
                }
            }
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                reload();
            }
        });
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        // heartbeat message that we do not cared about
    }

    @Override
    public void handleDataSampleEvent(DataCollector.DataSampleEvent evt) throws Exception {

        Value value = (Value) evt.getSource();

        // this call back is going to happen A LOT. need fast lookup of the nodes.
        //
        final String key = toKey(value);
        if (nodeLookup.get(key) == null) {

            DefaultMutableTreeNode root = (DefaultMutableTreeNode) super.getRoot();
            Enumeration iter = root.children();
            while (iter.hasMoreElements()) {
                DefaultMutableTreeNode dn = (DefaultMutableTreeNode) iter.nextElement();
                Device d = (Device) dn.getUserObject();
                if (d.getUDI().equals(value.getUniqueDeviceIdentifier())) {
                    final DefaultMutableTreeNode mn = ensureMetricNode(dn, value);
                    final DefaultMutableTreeNode in = ensureInstanceNode(mn, value);

                    nodeLookup.put(key, in);
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            log.info("adding to the tree: " + key);

                            int idx = mn.getIndex(in);
                            Object path[] = getPathToRoot(mn);
                            DeviceTreeModel.this.fireTreeNodesInserted(
                                                       DeviceTreeModel.this,
                                                       path,
                                                       new int[]   { idx },
                                                       new Object[]{ in  });
                        }
                    });                
                }
            }
        }
    }

    static String toKey(Value value) {
        return value.getUniqueDeviceIdentifier() + "/" + value.getMetricId() + "/" + value.getInstanceId();
    }

    DefaultMutableTreeNode ensureMetricNode(DefaultMutableTreeNode d, Value value)
    {
        Enumeration iter = d.children();
        while (iter.hasMoreElements()) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) iter.nextElement();
            if (tn.getUserObject().equals(value.getMetricId())) {
                    return tn;
            }
        }

        DefaultMutableTreeNode tn = makeNewNodeFactory(d, value.getMetricId());
        d.add(tn);
        return tn;
    }

    DefaultMutableTreeNode ensureInstanceNode(DefaultMutableTreeNode d, Value value)
    {
        Enumeration iter = d.children();
        while (iter.hasMoreElements()) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) iter.nextElement();
            if (tn.getUserObject().equals(value.getInstanceId())) {
                return tn;
            }
        }

        DefaultMutableTreeNode tn = makeNewNodeFactory(d, value.getInstanceId());
        d.add(tn);
        return tn;
    }

    public boolean isEnabled(Value value)
    {
        // this call back is going to happen A LOT. need fast lookup of the nodes.
        //
        final String key = toKey(value);
        DefaultMutableTreeNode node = nodeLookup.get(key);
        if(node instanceof SelectableNode) {
            return ((SelectableNode)node).isSelected();
        }
        return false;
    }

    ConcurrentMap<String, DefaultMutableTreeNode> nodeLookup = new MapMaker().weakValues().makeMap();

}
