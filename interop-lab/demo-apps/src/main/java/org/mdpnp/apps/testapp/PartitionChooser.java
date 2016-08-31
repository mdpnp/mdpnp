package org.mdpnp.apps.testapp;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class PartitionChooser {
    
    private PartitionChooserModel model;
    
    @FXML
    protected Button ok;
    
    @FXML
    protected TextField field;
    
    @FXML
    protected ListView<String> list;
    
    @FXML
    protected BorderPane partitionChooser;
    
    private Runnable hide;
        
    public void refresh() {
        model.get(list.getItems());
    }
    
   
   
    @FXML public void releaseTextKey(KeyEvent evt) {
        String value = field.getText();
        if(value != null && !value.isEmpty()) {
            switch(evt.getCode()) {
            case ENTER:
                 list.getItems().add(value);
                field.setText("");
            default:
            }
        }
    }
    
    @FXML public void releaseListKey(KeyEvent evt) {
        switch(evt.getCode()) {
        case BACK_SPACE:
        case DELETE:
            String val = list.getSelectionModel().getSelectedItem();
            if(null != val) {
                list.getItems().remove(val);
            }
            break;
        default:
        }
    }
    
    @FXML public void clickOk(ActionEvent evt) {
        String value = field.getText();
        if(value != null && !value.isEmpty()) {
            list.getItems().add(value);
            field.setText("");
        }
        model.activate(list.getItems());
        if(null != hide) {
            hide.run();
        }
    }
    
    public PartitionChooser() {
    }
    
    public PartitionChooser setModel(PartitionChooserModel model) {
        this.model = model;
        model.get(list.getItems());
        return this;
    }
    public PartitionChooser setItems(ObservableList<String> items) {
        list.setItems(items);
        return this;
    }
    public PartitionChooser setHide(Runnable hide) {
        this.hide = hide;
        return this;
    }
    
}
