package org.mdpnp.apps.testapp.export;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 */
public class DataCollectorAppFactoryRunner extends javafx.application.Application {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        System.setProperty("DataCollectorApp.debug", "true");
        javafx.application.Application.launch(DataCollectorAppFactoryRunner.class, args);
        Platform.exit();
        log.info("This is the end, exit code=" + 0);
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        final AbstractApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"IceAppContainerContext.xml"});
        context.registerShutdownHook();


        DataCollectorAppFactory factory = new DataCollectorAppFactory();
        final IceApplicationProvider.IceApp app = factory.create(context);

        app.activate(context);
        final Parent ui = app.getUI();

        primaryStage.setScene(new Scene(ui));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {

    }
}
