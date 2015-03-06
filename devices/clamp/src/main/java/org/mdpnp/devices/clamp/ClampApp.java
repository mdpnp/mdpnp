package org.mdpnp.devices.clamp;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClampApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Clampy.class.getResource("Clampy.fxml"));
        Parent node = loader.load();
        Clampy controller = loader.getController();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent> () {

            @Override
            public void handle(WindowEvent event) {
                controller.stop();
            }
            
        });
        controller.initialize();
        primaryStage.setTitle("Mr. Clampy");
        primaryStage.setScene(new Scene(node));
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
