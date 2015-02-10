package org.mdpnp.apps.testapp.export;

import org.mdpnp.apps.testapp.Device;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;

class SelectableNode extends DefaultMutableTreeNode
{
    boolean selected;

    public SelectableNode(Object userObject, boolean sel) {
        super(userObject);
        selected = sel;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean v) {
        selected = v;
    }

    static class CheckBoxNodeRenderer implements TreeCellRenderer {
        private JCheckBox checkboxUI = new JCheckBox();

        private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

        Color selectionBorderColor;
        Color selectionForeground;
        Color selectionBackground;
        Color textForeground;
        Color textBackground;

        protected JCheckBox getLeafRenderer() {
            return checkboxUI;
        }

        public CheckBoxNodeRenderer() {
            Font fontValue;
            fontValue = UIManager.getFont("Tree.font");
            if (fontValue != null) {
                checkboxUI.setFont(fontValue);
            }
            Boolean b = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
            checkboxUI.setFocusPainted(b==null?false:b);

            selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
            selectionForeground = UIManager.getColor("Tree.selectionForeground");
            selectionBackground = UIManager.getColor("Tree.selectionBackground");
            textForeground = UIManager.getColor("Tree.textForeground");
            textBackground = UIManager.getColor("Tree.textBackground");
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded, boolean leaf, int row,
                                                      boolean hasFocus) {

            if (!(value instanceof SelectableNode)) {

                return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded,
                                                                    leaf, row, hasFocus);
            }

            SelectableNode node = (SelectableNode)value;

            String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
            checkboxUI.setText(stringValue);
            checkboxUI.setSelected(false);

            checkboxUI.setEnabled(tree.isEnabled());

            if (selected) {
                checkboxUI.setForeground(selectionForeground);
                checkboxUI.setBackground(selectionBackground);
            } else {
                checkboxUI.setForeground(textForeground);
                checkboxUI.setBackground(textBackground);
            }

            String txt=null;
            Object o = node.getUserObject();
            if(o instanceof Device) {
                txt = ((Device)o).getMakeAndModel() + " (" + ((Device)o).getUDI() + ")";
            }
            else {
                txt = node.toString();
            }
            checkboxUI.setText(txt);
            checkboxUI.setSelected(node.isSelected());

            return checkboxUI;
        }
    }

    static class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

        CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();

        public CheckBoxNodeEditor() {

        }

        public Object getCellEditorValue() {
            JCheckBox checkbox = renderer.getLeafRenderer();
            return checkbox.isSelected();
        }

        public boolean isCellEditable(EventObject event) {
            return true;
        }

        public Component getTreeCellEditorComponent(JTree tree, Object value,
                                                    boolean selected, boolean expanded, boolean leaf, int row) {

            Component editor = renderer.getTreeCellRendererComponent(tree, value,
                                                                     true, expanded, leaf, row, true);

            // editor always selected / focused
            ItemListener itemListener = new ItemListener() {
                public void itemStateChanged(ItemEvent itemEvent) {
                    if (stopCellEditing()) {
                        fireEditingStopped();
                    }
                }
            };
            if (editor instanceof JCheckBox) {
                ((JCheckBox) editor).addItemListener(itemListener);
            }

            return editor;
        }
    }
}
