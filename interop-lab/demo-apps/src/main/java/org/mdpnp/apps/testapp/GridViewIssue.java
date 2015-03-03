package org.mdpnp.apps.testapp;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class GridViewIssue extends Application {

    public static class MyObject {
        public MyObject() {
        }
        public MyObject(String string) {
            this.stringProperty().set(string);
        }
        
        private StringProperty string;
        public StringProperty stringProperty() {
            if(null == string) {
                string = new SimpleStringProperty(this, "string", "FOO");
            }
            return string;
        }
    }
    
    public static class MyCell extends GridCell<MyObject> {
        @Override
        protected void updateItem(MyObject item, boolean empty) {
            super.updateItem(item, empty);
            if(null == item) {
                textProperty().unbind();
                textProperty().set("");
            } else {
                textProperty().bind(item.stringProperty());
            }
        }
    }
    
    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        ObservableList<MyObject> items = FXCollections.observableArrayList(new Callback<MyObject, Observable[]>() {

            @Override
            public Observable[] call(MyObject param) {
                return new Observable[] { param.stringProperty() };
            }
            
        });
        items.addListener(new ListChangeListener<MyObject>() {

            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends MyObject> c) {
                System.out.println("CHANGED:"+c);
            }
            
        });
        GridView<MyObject> gridView = new GridView<MyObject>();
        gridView.setCellHeight(120);
        gridView.setCellWidth(100);
        gridView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        gridView.setPrefSize(-1, -1);
        gridView.setCellFactory(new Callback<GridView<MyObject>,GridCell<MyObject>>() {

            @Override
            public GridCell<MyObject> call(GridView<MyObject> param) {
                return new MyCell();
            }
            
        });

        ScrollPane scrollPane = new ScrollPane(gridView);
        scrollPane.setPrefSize(-1, -1);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        final BorderPane pane = new BorderPane(scrollPane);
        pane.setPrefSize(-1, -1);
        
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.setWidth(640);
        primaryStage.setHeight(480);
        gridView.setItems(items);
        primaryStage.show();
        
        MyObject second = new MyObject();
        MyObject first = new MyObject("FIRST");
        
        
        new Thread(new Runnable() {
            public void run() {
                sleep(400L);
                Platform.runLater(new Runnable() {
                    public void run() {
                        items.add(0, second);
                        second.stringProperty().set("SECOND");
                    }
                });
                sleep(400L);
                Platform.runLater(new Runnable() {
                    public void run() {
                        items.add(0, first);
                    }
                });

            }
        }).start();

    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
