/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataDataReader;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataSeq;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataTypeSupport;
import com.rti.dds.infrastructure.Locator_t;
import com.rti.dds.infrastructure.Property_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.subscription.ViewStateKind;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class ParticipantOnly extends JPanel implements TableModel, DataReaderListener {
    public static void start(int domainId, boolean cmdline) {
        final ParticipantOnly panel = new ParticipantOnly(domainId);
        if (!cmdline) {
            JFrame frame = new JFrame("ICE Participant Only (" + domainId + ")");
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    panel.stop();
                    super.windowClosing(e);
                }
            });
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(panel, BorderLayout.CENTER);
            frame.setSize(800, 600);
            frame.setVisible(true);
        } else {
            panel.addTableModelListener(new TableModelListener() {

                @Override
                public void tableChanged(TableModelEvent e) {
                    String verb = "??";
                    switch (e.getType()) {
                    case TableModelEvent.DELETE:
                        verb = "DELETE";
                        break;
                    case TableModelEvent.UPDATE:
                        verb = "UPDATE";
                        break;
                    case TableModelEvent.INSERT:
                        verb = "INSERT";
                        break;
                    }
                    TableModel model = (TableModel) e.getSource();
                    for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
                        System.out.print(verb + "\t");
                        for (int j = 0; j < model.getColumnCount(); j++) {
                            System.out.print(model.getValueAt(i, j) + "\t");
                        }
                        System.out.println();
                    }

                }

            });
            System.out.println("Type <exit> to exit");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            try {
                while (null != (line = reader.readLine())) {
                    if ("exit".equals(line)) {
                        panel.stop();
                        return;
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private final int domainId;
    private final JTable participantTable;
    private final List<TableModelListener> listeners = new CopyOnWriteArrayList<TableModelListener>();
    private final List<ParticipantBuiltinTopicData> data = Collections.synchronizedList(new ArrayList<ParticipantBuiltinTopicData>());

    private final DomainParticipant participant;
    private final ParticipantBuiltinTopicDataDataReader reader;

    public ParticipantOnly(final int domainId) {
        super(new BorderLayout());
        this.domainId = domainId;
        this.participantTable = new JTable(this);

        add(new JScrollPane(participantTable), BorderLayout.CENTER);
        DomainParticipantFactory factory = DomainParticipantFactory.get_instance();
        DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
        factory.get_qos(qos);
        qos.entity_factory.autoenable_created_entities = false;
        factory.set_qos(qos);

        DomainParticipantQos dpQos = new DomainParticipantQos();
        factory.get_default_participant_qos(dpQos);
        dpQos.participant_name.name = "ICE  Participant Only";

        participant = factory.create_participant(this.domainId, dpQos, null, StatusKind.STATUS_MASK_NONE);
        reader = (ParticipantBuiltinTopicDataDataReader) participant.get_builtin_subscriber().lookup_datareader(
                ParticipantBuiltinTopicDataTypeSupport.PARTICIPANT_TOPIC_NAME);
        reader.set_listener(this, StatusKind.DATA_AVAILABLE_STATUS);

        participant.enable();

    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return "Key";
        case 1:
            return "Name";
        case 2:
            return "Hostname";
        default:
            return "???";

        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
        case 1:
        case 2:
            return String.class;
        default:
            return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        ParticipantBuiltinTopicData data = null;
        synchronized (this.data) {
            data = rowIndex < this.data.size() ? this.data.get(rowIndex) : null;
        }
        if (null == data) {
            return null;
        }
        switch (columnIndex) {
        case 0:
            return data.key.toString();
        case 1:
            return data.participant_name.name;
        case 2:
            return getHostname(data);
        default:
            return null;
        }
    }

    public static final String getHostname(ParticipantBuiltinTopicData participantData) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < participantData.property.value.size(); i++) {
            Property_t prop = (Property_t) participantData.property.value.get(i);
            if ("dds.sys_info.hostname".equals(prop.name)) {
                sb.append(prop.value).append(" ");
            }
        }

        for (int i = 0; i < participantData.default_unicast_locators.size(); i++) {
            Locator_t locator = (Locator_t) participantData.default_unicast_locators.get(i);
            try {
                InetAddress addr = null;
                switch (locator.kind) {
                case Locator_t.KIND_TCPV4_LAN:
                case Locator_t.KIND_TCPV4_WAN:
                case Locator_t.KIND_TLSV4_LAN:
                case Locator_t.KIND_TLSV4_WAN:
                case Locator_t.KIND_UDPv4:
                    addr = InetAddress
                            .getByAddress(new byte[] { locator.address[12], locator.address[13], locator.address[14], locator.address[15] });
                    break;
                case Locator_t.KIND_UDPv6:
                default:
                    addr = InetAddress.getByAddress(locator.address);
                    break;
                }
                sb.append(addr.getHostAddress()).append(" ");
            } catch (UnknownHostException e) {
                e.printStackTrace();
//                log.error("getting locator address", e);
            }
        }
        return sb.toString();
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    public void stop() {
        DomainParticipantFactory.get_instance().delete_participant(participant);
    }

    private final int indexOf(ParticipantBuiltinTopicData data) {
        synchronized (this.data) {
            for (int i = 0; i < this.data.size(); i++) {
                if (data.key.equals(this.data.get(i).key)) {
                    return i;
                }
            }
            return -1;
        }
    }

    @Override
    public void on_data_available(DataReader arg0) {
        final ParticipantBuiltinTopicDataSeq data_seq = new ParticipantBuiltinTopicDataSeq();
        final SampleInfoSeq sample_info = new SampleInfoSeq();
        try {
            for (;;) {
                try {
                    reader.read(data_seq, sample_info, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE,
                            ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                    for (int i = 0; i < data_seq.size(); i++) {
                        ParticipantBuiltinTopicData d = (ParticipantBuiltinTopicData) data_seq.get(i);
                        SampleInfo si = (SampleInfo) sample_info.get(i);
                        TableModelEvent tme = null;

                        if (0 != (InstanceStateKind.ALIVE_INSTANCE_STATE & si.instance_state)) {
                            if (si.valid_data) {
                                int idx = -1;
                                boolean inserted = false;
                                synchronized (data) {
                                    idx = indexOf(d);
                                    if (idx < 0) {
                                        ParticipantBuiltinTopicData d1 = new ParticipantBuiltinTopicData();
                                        d1.copy_from(d);
                                        idx = data.size();
                                        data.add(d1);
                                        inserted = true;
                                    } else {
                                        ParticipantBuiltinTopicData d1 = data.get(idx);
                                        d1.copy_from(d);
                                    }
                                }
                                if (inserted) {
                                    tme = new TableModelEvent(this, idx, idx, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
                                } else {
                                    tme = new TableModelEvent(this, idx, idx, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
                                }
                            }
                        } else {
                            ParticipantBuiltinTopicData d1 = new ParticipantBuiltinTopicData();
                            reader.get_key_value(d1, si.instance_handle);
                            int idx = -1;
                            synchronized (data) {
                                idx = indexOf(d1);
                                if (idx >= 0) {
                                    data.remove(idx);
                                }
                            }
                            if (idx >= 0) {
                                tme = new TableModelEvent(this, idx, idx, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
                            }
                        }
                        if (tme != null) {
                            for (TableModelListener l : this.listeners) {
                                l.tableChanged(tme);
                            }
                        }
                    }
                } finally {
                    reader.return_loan(data_seq, sample_info);
                }
            }
        } catch (RETCODE_NO_DATA noData) {

        }
    }

    @Override
    public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {

    }

    @Override
    public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {

    }

    @Override
    public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {

    }

    @Override
    public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {

    }

    @Override
    public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {

    }

    @Override
    public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {

    }

}
