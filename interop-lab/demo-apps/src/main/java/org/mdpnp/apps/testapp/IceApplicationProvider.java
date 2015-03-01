package org.mdpnp.apps.testapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.awt.Component;
import java.io.IOException;
import java.net.URL;

import javafx.scene.Parent;
import javafx.scene.image.Image;

public interface IceApplicationProvider {

    IceApplicationProvider.AppType getAppType();
    IceApplicationProvider.IceApp create(ApplicationContext context) throws java.io.IOException;


    public interface IceApp {

        /**
         * @return metadata descriptor for the application. Cannot return null.
         */
        AppType getDescriptor();

        /**
         * @return java.awt.Component (either JFrame or Panel) or null for headless application.
         */
        // TODO this will be needed
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

        void stop() throws Exception;;

        /**
         * Destructor for the application. resources should be freed, state cleared. app is not expected
         * to be functional once it had been destroyed.
         */
        void destroy() throws Exception;
    }

    /**
     * Meta-data for the participating application. Includes rudimentary name, id, avatar to be used in the directory listing.
     */
    public static class AppType {

        private static final Logger log = LoggerFactory.getLogger(AppType.class);

        private final String id;
        private final String name;
        private final Image icon;
        private final String disableProperty;

        public AppType(final String name, final String disableProperty, final URL icon, double scale) {
            this(name, disableProperty, null == icon ? null : new Image(icon.toExternalForm()), scale);
        }

        public AppType(final String name, final String disableProperty, final Image icon, double scale) {
            this.id   = generateId(name);
            this.name = name;
            this.icon = icon;
            this.disableProperty = disableProperty;
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

        private String generateId(String name) {
            if(name == null)
                throw new IllegalArgumentException("Name cannot be null");
            String s = name.toLowerCase().replaceAll("\\s+","")+(instanceCount++);
            return s;
        }
        private static int instanceCount=0;

//        private static BufferedImage read(URL url) {
//            try {
//                return url==null?null: ImageIO.read(url);
//            } catch (IOException e) {
//                log.error("Failed to load image url:" + url.toExternalForm(), e);
//                return null;
//            }
//        }

//        private static BufferedImage scale(BufferedImage before, double scale) {
//            if (null == before) {
//                return null;
//            }
//            if (0 == Double.compare(scale, 0.0)) {
//                return before;
//            }
//            int width = before.getWidth();
//            int height = before.getHeight();
//
//            BufferedImage after = new BufferedImage((int) (scale * width), (int) (scale * height), BufferedImage.TYPE_INT_ARGB);
//            java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
//            at.scale(scale, scale);
//
//            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//            after = scaleOp.filter(before, after);
//            return after;
//        }
    }
}
