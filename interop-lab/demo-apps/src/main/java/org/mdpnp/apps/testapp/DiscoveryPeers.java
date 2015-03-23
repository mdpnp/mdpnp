package org.mdpnp.apps.testapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantQos;

public class DiscoveryPeers extends BorderPane {

    protected DomainParticipant participant;
    protected final ObservableList<String> peers = FXCollections.observableArrayList();
    protected final Button ok = new Button("Ok");

    protected final ListView<String> list = new ListView<String>(peers);
    protected final TextField field = new TextField();

    public DiscoveryPeers() {
        // setTitle("Discovery Peers");
        setCenter(new ScrollPane(list));
        setTop(new Label("Current Discovery Peers"));

        BorderPane controls = new BorderPane();
        controls.setCenter(field);
        controls.setRight(ok);
        TextArea jText = new TextArea(
                "Type a peer address and press <enter> to add.\nHighlight a discovery peer and press <backspace> to remove.\nPress Ok when you're done.");
        // jText.setWrapStyleWord(true);
        jText.setEditable(false);
        // jText.setLineWrap(true);
        controls.setTop(jText);
        setBottom(controls);
        field.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                String value = field.getText();
                if (value != null && !value.isEmpty()) {
                    peers.add(value);
                    participant.add_peer(value);
                    field.setText("");
                }
            }

        });

        list.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                case BACK_SPACE:
                case DELETE:
                    String val = list.getSelectionModel().getSelectedItem();
                    if (null != val) {
                        participant.remove_peer(val);
                        peers.remove(val);
                    }
                    break;
                default:
                }
            }

        });
        ok.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                DiscoveryPeers.this.setVisible(false);
            }

        });
    }

    public void set(DomainParticipant participant) {
        this.participant = participant;
        DomainParticipantQos qos = new DomainParticipantQos();
        participant.get_qos(qos);
        for (int i = 0; i < qos.discovery.initial_peers.size(); i++) {
            peers.add((String) qos.discovery.initial_peers.get(i));
        }
    }
}
