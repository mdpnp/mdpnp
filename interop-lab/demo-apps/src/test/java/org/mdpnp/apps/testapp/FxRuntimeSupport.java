package org.mdpnp.apps.testapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 * Necessity for this utility comes from ‘minor’ detail of the fx internals -
 * it is a singleton that can only be instantiated once.
 */
public class FxRuntimeSupport {

    private static final Logger log = LoggerFactory.getLogger(FxRuntimeSupport.class);

    static FxRuntimeSupport instance = null;

    public static synchronized FxRuntimeSupport initialize() {
        if(instance == null) {
            try {
                instance = new FxRuntimeSupport();
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to create FX run time", ex);
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            @Override
            public void run() {
                log.warn("Shutting down FX Runtime");
                Platform.exit();
            }
        }));
        return instance;
    }

    static final CountDownLatch latch = new CountDownLatch(1);

    public FxRuntimeSupport() throws Exception{
        Platform.setImplicitExit(false);
        new Thread(new Runnable() {
            public void run() {
                Application.launch(FakeApp.class);
            }
        }).start();
        latch.await();
    }

    public Stage show(final IceApplication appNode) {
        try {
            return run((new Callable<Stage>() {
                @Override
                public Stage call() throws Exception {
                    return showInFxThread(appNode);
                }
            }));
        } catch (Exception e) {
            throw new RuntimeException("Can't create stage", e);
        }
    }

    public Stage show(final Parent appNode) {
        try {
            return run((new Callable<Stage>() {
                @Override
                public Stage call() throws Exception {
                    return showInFxThread(appNode);
                }
            }));
        } catch (Exception e) {
            throw new RuntimeException("Can't create stage", e);
        }
    }

    private <T> T run(Callable<T> callable) throws Exception {
        if(Platform.isFxApplicationThread()) {
            return callable.call();
        } else {
            FutureTask<T> future = new FutureTask<>(callable);
            Platform.runLater(future);
            return future.get();
        }
    }

    public static class FakeApp extends Application {

        Stage primaryStage;

        @Override
        public void start(Stage primaryStage) throws Exception {
            this.primaryStage = primaryStage;
            latch.countDown();
        }
    }

    private Stage showInFxThread(IceApplication iceApp) throws Exception{

        if(lastStage != null) {
            lastStage.close();
        }

        lastStage = new Stage(StageStyle.DECORATED);
        lastStage.setAlwaysOnTop(false);

        iceApp.init();
        iceApp.start(lastStage);

        lastStage.sizeToScene();
        lastStage.show();
        return lastStage;
    }

    private Stage showInFxThread(Parent appNode) {

        if(lastStage != null) {
            lastStage.close();
        }

        lastStage = new Stage(StageStyle.DECORATED);
        lastStage.setAlwaysOnTop(false);

        Scene scene = new Scene(appNode);
        lastStage.setScene(scene);

        lastStage.sizeToScene();
        lastStage.show();
        return lastStage;

    }

    Stage lastStage;
}
