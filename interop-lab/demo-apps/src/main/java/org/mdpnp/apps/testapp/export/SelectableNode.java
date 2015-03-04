package org.mdpnp.apps.testapp.export;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

@SuppressWarnings("serial")
class SelectableNode extends CheckBoxTreeItem<Object>
{
    public SelectableNode(Object userObject, boolean sel) {
        super(userObject);
    }

    static class CheckBoxNodeRenderer implements TreeCellRenderer {

        private final JCheckBox    checkboxUI = new JCheckBox();

        private final static DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

        @SuppressWarnings("unused")
        private final Color selectionBorderColor;
        private final Color selectionForeground;
        private final Color selectionBackground;
        private final Color textForeground;
        private final Color textBackground;

        protected JCheckBox getLeafRenderer() {
            return checkboxUI;
        }

        public CheckBoxNodeRenderer() {

            Font fontValue = UIManager.getFont("Tree.font");
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

            checkboxUI.setEnabled(tree.isEnabled());

            if (selected) {
                checkboxUI.setForeground(selectionForeground);
                checkboxUI.setBackground(selectionBackground);
            } else {
                checkboxUI.setForeground(textForeground);
                checkboxUI.setBackground(textBackground);
            }

            String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
            checkboxUI.setText(stringValue);
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
