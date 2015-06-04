package org.mdpnp.apps.testapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
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
        return run((new Callable<Stage>() {
            @Override
            public Stage call() throws Exception {
                return showInFxThread(appNode);
            }
        }));
    }

    public Stage show(final Parent appNode) {
        return run((new Callable<Stage>() {
            @Override
            public Stage call() throws Exception {
                return showInFxThread(appNode);
            }
        }));
    }

    public void close(final Stage appNode) {
        run((new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if(lastStage != appNode)
                    throw new RuntimeException("FxRuntimeSupport should only deal with one stage at a time");

                lastStage.close();
                lastStage=null;
                return true;
            }
        }));
    }

    public <T> T run(Callable<T> callable) {
        try {
            if(Platform.isFxApplicationThread()) {
                return callable.call();
            } else {
                FutureTask<T> future = new FutureTask<>(callable);
                Platform.runLater(future);
                return future.get();
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Can't execute callable", e);
        }
    }

    public static class FakeApp extends Application {

        Stage primaryStage;

        @Override
        public void start(Stage primaryStage) throws Exception {
            this.primaryStage = primaryStage;
            latch.countDown();
        }

        @Override
        public void stop() throws Exception {
            super.stop();
        }
    }

    private Stage showInFxThread(final IceApplication iceApp) throws Exception{

        if(lastStage != null) {
            lastStage.close();
            lastStage = null;
        }

        lastStage = new Stage(StageStyle.DECORATED);
        lastStage.setAlwaysOnTop(false);
        lastStage.addEventHandler(WindowEvent.WINDOW_HIDING, new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent window)
            {
                // this is a dialog - the application's 'close' event
                // wont happen
                try {
                    iceApp.stop();
                } catch (Exception e) {
                    log.error("Failed to stop device adapter");
                }
            }
        });
        iceApp.start(lastStage);
        lastStage.show();
        return lastStage;
    }

    private Stage showInFxThread(Parent appNode) {

        if(lastStage != null) {
            lastStage.close();
            lastStage = null;
        }

        lastStage = new Stage(StageStyle.DECORATED);
        lastStage.setAlwaysOnTop(false);

        Scene scene = new Scene(appNode);
        lastStage.setScene(scene);
        lastStage.show();
        return lastStage;

    }

    Stage lastStage;


    /**
     * to be used in test cases that do not need full-blown fx platform to be initiated to run
     */
    public static class CurrentThreadExecutor implements Executor {

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}
