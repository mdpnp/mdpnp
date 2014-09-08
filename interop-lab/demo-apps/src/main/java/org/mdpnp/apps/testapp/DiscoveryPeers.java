package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantQos;

@SuppressWarnings("serial")
public class DiscoveryPeers extends JDialog {
    
    protected DomainParticipant participant;
    protected final MyListModel peers = new MyListModel();
    protected final JButton ok = new JButton("Ok");
    
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
    
    protected final JList<String> list = new JList<String>(peers);
    protected final JTextField field = new JTextField();
    
    
    public DiscoveryPeers(Window window) {
        super(window);
        setTitle("Discovery Peers");
        getContentPane().setLayout(new BorderLayout());
        enableEvents(ComponentEvent.COMPONENT_SHOWN);
        
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(new JLabel("Current Discovery Peers"), BorderLayout.NORTH);
        
        JPanel controls = new JPanel(new BorderLayout());
        controls.add(field, BorderLayout.CENTER);
        controls.add(ok, BorderLayout.EAST);
        JTextArea jText = new JTextArea("Type a peer address and press <enter> to add.\nHighlight a discovery peer and press <backspace> to remove.\nPress Ok when you're done.");
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
                    peers.add(value);
                    participant.add_peer(value);
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
                        participant.remove_peer(val);
                        peers.remove(val);
                    }
                    break;
                }
            }
        });
        
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DiscoveryPeers.this.setVisible(false);
            }
            
        });
    }
    
    public void set(DomainParticipant participant) {
        this.participant = participant;
        DomainParticipantQos qos = new DomainParticipantQos();
        participant.get_qos(qos);
        for(int i = 0; i < qos.discovery.initial_peers.size(); i++) {
            peers.add((String) qos.discovery.initial_peers.get(i));
        }
    }
}
