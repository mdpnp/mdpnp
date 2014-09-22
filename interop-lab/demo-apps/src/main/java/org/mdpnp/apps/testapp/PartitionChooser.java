package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

@SuppressWarnings("serial")
public class PartitionChooser extends JDialog {
    
    protected Subscriber subscriber;
    protected Publisher publisher;
    protected final MyListModel partitions = new MyListModel();
    protected final JButton ok = new JButton("Ok");
        
    public void refresh() {
        partitions.clear();
        Set<String> parts = new HashSet<String>();
        if(subscriber != null) {
            SubscriberQos qos = new SubscriberQos();
            subscriber.get_qos(qos);
            for(int i = 0; i < qos.partition.name.size(); i++) {
                parts.add((String)qos.partition.name.get(i));
            }
        }
        if(publisher != null) {
            PublisherQos qos = new PublisherQos();
            publisher.get_qos(qos);
            for(int i = 0; i < qos.partition.name.size(); i++) {
                parts.add((String)qos.partition.name.get(i));
            }
        }
        for(String s : parts) {
            partitions.add(s);
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
    
    
    public PartitionChooser(Window window) {
        super(window);
        setTitle("Partition Chooser");
        getContentPane().setLayout(new BorderLayout());
        enableEvents(ComponentEvent.COMPONENT_SHOWN);
        
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(new JLabel("Current Partitions"), BorderLayout.NORTH);
        
        JPanel controls = new JPanel(new BorderLayout());
        controls.add(field, BorderLayout.CENTER);
        controls.add(ok, BorderLayout.EAST);
        JTextArea jText = new JTextArea("Type a partition name and press <enter> to add.\nHighlight a partition and press <backspace> to remove.\nPress Ok when you're done.");
        jText.setWrapStyleWord(true);
        jText.setEditable(false);
        jText.setLineWrap(true);
        controls.add(jText, BorderLayout.NORTH);
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
                String value = field.getText();
                if(value != null && !value.isEmpty()) {
                    partitions.add(value);
                    field.setText("");
                }
                {
                    SubscriberQos qos = new SubscriberQos();
                    
                    subscriber.get_qos(qos);
                    qos.partition.name.clear();
                    for(String s : partitions.values) {
                        qos.partition.name.add(s);
                    }
                    subscriber.set_qos(qos);
                }
                {
                    PublisherQos qos = new PublisherQos();
                    publisher.get_qos(qos);
                    qos.asynchronous_publisher.thread.priority = Thread.NORM_PRIORITY;
                    qos.asynchronous_publisher.asynchronous_batch_thread.priority = Thread.NORM_PRIORITY;
                    qos.partition.name.clear();
                    for(String s : partitions.values) {
                        qos.partition.name.add(s);
                    }
                    publisher.set_qos(qos);
                }
                PartitionChooser.this.setVisible(false);
            }
            
        });
    }
    
    public void set(Subscriber sub) {
        this.subscriber = sub;
    }
    public void set(Publisher pub) {
        this.publisher = pub;
    }
}
