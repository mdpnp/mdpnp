package org.mdpnp.apps.testapp.export;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentMap;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.vital.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;

@SuppressWarnings("serial")
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

    public static String textForNode(Object o) {
        String txt="";
        if(o instanceof DefaultMutableTreeNode) {
            o = ((DefaultMutableTreeNode)o).getUserObject();
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
            Enumeration<?> iter = node.children();
            while (iter.hasMoreElements()) {
                SelectableNode n = (SelectableNode) iter.nextElement();
                cascadeChildren(n, v);
            }
        }
    }

    @Override
    public void intervalAdded(final ListDataEvent e) {

        log.info("Device Added", e.toString());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) getRoot();
                @SuppressWarnings("unchecked")
                AbstractListModel<Device> dlm = (AbstractListModel<Device>)e.getSource();

                for(int idx=e.getIndex0(); idx<=e.getIndex1(); idx++) {
                    Device d = dlm.getElementAt(idx);
                    treeRoot.add(makeNewNodeFactory(treeRoot, d));
                }

                reload();  
            }
        });
    }

    @Override
    public void intervalRemoved(final ListDataEvent e) {

        log.info("Device Removed", e.toString());

        if(e.getIndex0() != e.getIndex1())
            throw new IllegalArgumentException("Contact had changed - the model must throw one event per deletion.");

        DeviceListModel dlm = (DeviceListModel)e.getSource();
        final Device d = dlm.getLastRemoved();
        if(d == null)
            throw new IllegalArgumentException("Model does not tell us which device had been deleted.");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) getRoot();
                @SuppressWarnings("rawtypes")
                Enumeration iter = treeRoot.children();
                for(int idx=0; iter.hasMoreElements(); idx++) {
                    DefaultMutableTreeNode n = (DefaultMutableTreeNode) iter.nextElement();
                    if (n.getUserObject().equals(d)) {
                        treeRoot.remove(n);
                        DeviceTreeModel.this.fireTreeNodesRemoved(
                                DeviceTreeModel.this,
                                getPathToRoot(treeRoot),
                                new int[]{idx},
                                new Object[]{n});
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        // heartbeat message that we do not cared about
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

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) getRoot();
                    @SuppressWarnings("rawtypes")
                    Enumeration iter = treeRoot.children();
                    while (iter.hasMoreElements()) {
                        DefaultMutableTreeNode dn = (DefaultMutableTreeNode) iter.nextElement();
                        Device d = (Device) dn.getUserObject();
                        if (d.getUDI().equals(value.getUniqueDeviceIdentifier())) {
                            final DefaultMutableTreeNode mn = ensureMetricNode(dn, value);
                            final DefaultMutableTreeNode in = ensureInstanceNode(mn, value);

                            nodeLookup.put(key, in);

                            log.debug("adding to the tree: " + key);

                            int idx = mn.getIndex(in);
                            Object path[] = getPathToRoot(mn);
                            DeviceTreeModel.this.fireTreeNodesInserted(
                                    DeviceTreeModel.this,
                                    path,
                                    new int[]{idx},
                                    new Object[]{in});
                        }
                    }
                }
            });
        }
    }

    static String toKey(Value value) {
        return value.getUniqueDeviceIdentifier() + "/" + value.getMetricId() + "/" + value.getInstanceId();
    }

    DefaultMutableTreeNode ensureMetricNode(DefaultMutableTreeNode d, Value value)
    {
        @SuppressWarnings("rawtypes")
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
        @SuppressWarnings("rawtypes")
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
