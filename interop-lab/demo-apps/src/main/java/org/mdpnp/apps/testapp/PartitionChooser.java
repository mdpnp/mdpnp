package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

@SuppressWarnings("serial")
public class PartitionChooser extends JFrame {
    
    protected final List<Subscriber> subscribers = new ArrayList<Subscriber>();
    protected final MyListModel partitions = new MyListModel();
    protected final JButton ok = new JButton("Ok");
        
    @Override
    protected void processComponentEvent(ComponentEvent e) {
        super.processComponentEvent(e);
        if(e.getID()==ComponentEvent.COMPONENT_SHOWN) {
            partitions.clear();
            if(!subscribers.isEmpty()) {
                Subscriber sub = subscribers.get(0);
                SubscriberQos qos = new SubscriberQos();
                sub.get_qos(qos);
                for(int i = 0; i < qos.partition.name.size(); i++) {
                    partitions.add((String) qos.partition.name.get(i));
                }
            }
        }
    }
    
    public static final class MyListModel extends AbstractListModel<String> {
        
        private final List<String> values = new ArrayList<String>();
        
        public void add(String s) {
            values.add(s);
            int sz = values.size();
            fireIntervalAdded(this, sz - 1, sz - 1);
        }
        
        public void remove(String s) {
            int idx = values.indexOf(s);
            if(idx >= 0) {
                values.remove(idx);
                fireIntervalRemoved(this, idx, idx);
            }
            
        }
        
        public void clear() {
            int sz = values.size();
            if(sz > 0) {
                values.clear();
                fireIntervalRemoved(this, 0, sz-1);
            }
        }
        
        @Override
        public int getSize() {
            return values.size();
        }

        @Override
        public String getElementAt(int index) {
            return values.get(index);
        }
    };
    
    protected final JList<String> list = new JList<String>(partitions);
    protected final JTextField field = new JTextField();
    
    
    public PartitionChooser() {
        super("Partition Chooser");
        getContentPane().setLayout(new BorderLayout());
        enableEvents(ComponentEvent.COMPONENT_SHOWN);
        
        add(new JScrollPane(list), BorderLayout.CENTER);
        JPanel controls = new JPanel(new BorderLayout());
        controls.add(field, BorderLayout.CENTER);
        controls.add(ok, BorderLayout.EAST);
        add(controls, BorderLayout.SOUTH);
        field.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String value = field.getText();
                if(value != null && !value.isEmpty()) {
                    partitions.add(value);
                    field.setText("");
                }
            }
            
        });
        
        list.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch(e.getKeyCode()) {
                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                    String val = list.getSelectedValue();
                    if(null != val) {
                        partitions.remove(val);
                    }
                    break;
                }
            }
        });
        
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SubscriberQos qos = new SubscriberQos();
                
                for(Subscriber sub : subscribers) {
                    sub.get_qos(qos);
                    qos.partition.name.clear();
                    for(String s : partitions.values) {
                        qos.partition.name.add(s);
                    }
                    sub.set_qos(qos);
                }
                PartitionChooser.this.setVisible(false);
            }
            
        });
    }
    
    public void add(Subscriber sub) {
        subscribers.add(sub);
    }
    
    public void remove(Subscriber sub) {
        subscribers.remove(sub);
    }
    
    public static void main(String[] args) {
        PartitionChooser chooser = new PartitionChooser();
        chooser.setSize(640,480);
        chooser.setVisible(true);
    }
}
