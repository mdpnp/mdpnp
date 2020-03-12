package org.mdpnp.apps.testapp;

import java.net.URL;

import javafx.scene.Parent;
import javafx.scene.image.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public interface IceApplicationProvider {

    IceApplicationProvider.AppType getAppType();
    IceApplicationProvider.IceApp create(ApplicationContext context) throws java.io.IOException;


    public interface IceApp {

        /**
         * @return metadata descriptor for the application. Cannot return null.
         */
        AppType getDescriptor();

        /**
         * @return javafx.scene.Parent or null for headless application.
         */
        Parent getUI();

        /**
         * Activate the component. It is assumed that the all heavy lifting had already been done by the
         * factory that created the component and activation should be a relatively low cost anf fast operations.
         * Note, that while there is no requirement for this API to be idempotent, activate/stop loop should be reiterant.
         * @param context spring context with "global" beans that are can be used by this component.
         */
        void activate(ApplicationContext context);

        /**
         * Temporary passivate the component. The component is "put on ice" with the potential restart request to follow.
         * No expensive resources are excepted to be freed at this point.
         */

        void stop() throws Exception;

        /**
         * Destructor for the application. resources should be freed, state cleared. app is not expected
         * to be functional once it had been destroyed.
         */
        void destroy() throws Exception;
        
        public default int getPreferredWidth() {
        	return 640;
        }
        
        public default int getPreferredHeight() {
        	return 480;
        }
        
    }

    /**
     * Meta-data for the participating application. Includes rudimentary name, id, avatar to be used in the directory listing.
     */
    public static class AppType {

        @SuppressWarnings("unused")
        private static final Logger log = LoggerFactory.getLogger(AppType.class);

        private final String id;
        private final String name;
        private final Image icon;
        private final String disableProperty;
        private final boolean coordinatorApp;

        public AppType(final String name, final String disableProperty, final URL icon, double scale, boolean coordinatorApp) {
            this(name, disableProperty, null == icon ? null : new Image(icon.toExternalForm()), scale, coordinatorApp);
        }

        public AppType(final String name, final String disableProperty, final Image icon, double scale, boolean coordinatorApp) {
            this.id   = generateId(name);
            this.name = name;
            this.icon = icon;
            this.disableProperty = disableProperty;
            this.coordinatorApp = coordinatorApp;
        }

        /**
         * @return unique id for this instance.
         */
        public String getId() {
            return id;
        }

        /**
         * @return human-readable name to be used on the title pane of the enclosing container and in debug messages.
         */
        public String getName() {
            return name;
        }

        public Image getIcon() {
            return icon;
        }

        @Override
        public String toString() {
            return name;
        }

        public boolean isDisabled() {
            return null != disableProperty && Boolean.getBoolean(disableProperty);
        }
        
        public boolean isCoordinatorApp() {
            return coordinatorApp;
        }

        private String generateId(String name) {
            if(name == null)
                throw new IllegalArgumentException("Name cannot be null");
            String s = name.toLowerCase().replaceAll("\\s+","")+(instanceCount++);
            return s;
        }
        private static int instanceCount=0;
    }
}
