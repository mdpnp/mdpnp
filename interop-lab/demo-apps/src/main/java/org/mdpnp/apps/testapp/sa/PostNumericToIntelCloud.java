package org.mdpnp.apps.testapp.sa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

public class PostNumericToIntelCloud {
    
    static final String utf8Name = "UTF-8";
    static final Charset utf8 = Charset.forName(utf8Name);
    static final String user, pass, opt, send, type, value, name;
    static {
        try {
            user = URLEncoder.encode("user", utf8Name);
            pass = URLEncoder.encode("pass", utf8Name);
            opt = URLEncoder.encode("opt", utf8Name);
            send = URLEncoder.encode("send", utf8Name);
            type = URLEncoder.encode("type", utf8Name);
            value = URLEncoder.encode("value", utf8Name);
            name = URLEncoder.encode("name", utf8Name);
            
        } catch (UnsupportedEncodingException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    
    private static final void writeNumericToURL(StringBuilder sb, ice.Numeric numeric, URL url, String username, String password, String patientName, Date date) throws IOException {
        sb.delete(0, sb.length());
        sb.append(PostNumericToIntelCloud.user).append("=").append(URLEncoder.encode(username, utf8Name)).append("&");
        sb.append(PostNumericToIntelCloud.pass).append("=").append(URLEncoder.encode(password, utf8Name)).append("&");
        sb.append(PostNumericToIntelCloud.opt).append("=").append(URLEncoder.encode(send, utf8Name)).append("&");
        sb.append(PostNumericToIntelCloud.type).append("=").append(URLEncoder.encode(numeric.metric_id, utf8Name)).append("&");
        sb.append(PostNumericToIntelCloud.value).append("=").append(URLEncoder.encode(Float.toString(numeric.value), utf8Name)).append("&");
        sb.append(PostNumericToIntelCloud.name).append("=").append(URLEncoder.encode(patientName, utf8Name));
        
        byte[] postBytes = sb.toString().getBytes(utf8);

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postBytes);
        conn.getOutputStream().close();
        date.setTime(System.currentTimeMillis());
        System.out.println(dateFormat.format(date)+" : sending " + numeric.metric_id + "=" + numeric.value);
        date.setTime(System.currentTimeMillis());
        System.out.println(dateFormat.format(date)+" : response " + conn.getResponseCode() + " " + conn.getResponseMessage());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), utf8));
        String line = null;
        while(null != (line = bufferedReader.readLine())) {
            date.setTime(System.currentTimeMillis());
            System.out.println(dateFormat.format(date)+" : "+line);
        }
        bufferedReader.close();
        conn.disconnect();
    }
    
    
    public static void main(final String[] args) throws MalformedURLException, UnsupportedEncodingException {
        if(args.length < 3) {
            System.err.println("Specify [url] [username] [password] [patientName] [domainId]");
            System.exit(-1);
        }
        final URL url = new URL(args[0]);
        final String username = args[1];
        final String password = args[2];
        final String patientName = args[3];
        final int domainId = args.length > 4 ? Integer.parseInt(args[4]) : 0;
        
        final StringBuilder sb = new StringBuilder();

        DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(domainId, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.NumericTypeSupport.register_type(participant, ice.NumericTypeSupport.get_type_name());
        Topic numericTopic = participant.create_topic(ice.NumericTopic.VALUE, ice.NumericTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.NumericDataReader nReader = (ice.NumericDataReader) participant.create_datareader(numericTopic, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        WaitSet ws = new WaitSet();
        nReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
        ws.attach_condition(nReader.get_statuscondition());
        ConditionSeq cond_seq = new ConditionSeq();
        Duration_t timeout = new Duration_t(Duration_t.DURATION_INFINITE_SEC, Duration_t.DURATION_INFINITE_NSEC);
        ice.NumericSeq n_data_seq = new ice.NumericSeq();
        SampleInfoSeq info_seq = new SampleInfoSeq();
        final Date date = new Date();

        for(;;) {
            ws.wait(cond_seq, timeout);
            if(cond_seq.contains(nReader.get_statuscondition())) {
                int status_changes = nReader.get_status_changes();

                if(0 != (status_changes & StatusKind.DATA_AVAILABLE_STATUS)) {
                    try {
                        nReader.read(n_data_seq,info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE);

                        for(int i = 0; i < info_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) info_seq.get(i);
                            
                            if(si.valid_data) {
                                try {
                                    writeNumericToURL(sb, (ice.Numeric) n_data_seq.get(i), url, username, password, patientName, date);
                                } catch (IOException e) {
                                    date.setTime(System.currentTimeMillis());
                                    System.out.println(dateFormat.format(date) + " : error : " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }

                        }
                    } catch (RETCODE_NO_DATA noData) {
                    } finally {
                        nReader.return_loan(n_data_seq, info_seq);
                    }
                }
            }
        }
    }
}
