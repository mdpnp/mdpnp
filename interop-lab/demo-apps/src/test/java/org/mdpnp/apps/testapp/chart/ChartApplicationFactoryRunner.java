package org.mdpnp.apps.testapp.chart;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mdpnp.apps.testapp.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class ChartApplicationFactoryRunner extends javafx.application.Application {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        if(args.length!=0)
            System.setProperty("ChartApplication.fxml", args[0]);

        javafx.application.Application.launch(ChartApplicationFactoryRunner.class, args);
        Platform.exit();
        log.info("This is the end, exit code=" + 0);
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        String fxml = System.getProperty("ChartApplication.fxml", "ChartApplication.fxml");

        FXMLLoader loader = new FXMLLoader(ChartApplication.class.getResource(fxml));

        final Parent ui = loader.load();

        ChartApplicationFactory.WithVitalModel srv = loader.getController();
        srv.setModel(null);

        primaryStage.setScene(new Scene(ui));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {

    }
}
