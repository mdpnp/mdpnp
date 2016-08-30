package org.mdpnp.apps.testapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * I cannot stand mixing UI code with headless controllers like database engines. Yes, sometimes
 * there is a need for user feedback when running in interactive mode. So here comes this little
 * utility that would work as a bridge between the two worlds.
 */
public abstract class ControlFlowHandler {

    private static final Logger log = LoggerFactory.getLogger(ControlFlowHandler.class);

    public abstract boolean confirm(String header, String message, boolean defaultCode);
    public abstract boolean confirmError(String header, String message, boolean defaultCode);

    public static class HeadlessControlFlowHandler extends ControlFlowHandler {
        @Override
        public boolean confirm(String header, String message, boolean defaultCode) {
            log.warn(header + "\n" + message);
            return defaultCode;
        }

        @Override
        public boolean confirmError(String header, String message, boolean defaultCode) {
            log.warn(header + "\n" + message);
            return defaultCode;
        }
    }

    public static class UIControlFlowHandler extends ControlFlowHandler {
        @Override
        public boolean confirm(String header, String message, boolean defaultCode) {
            log.warn(header + "\n" + message);
            return DialogUtils.OkCancelDialog(header, message);
        }

        @Override
        public boolean confirmError(String header, String message, boolean defaultCode) {
            log.error(header + "\n" + message);
            message = message + "\n\n'OK' to continue, 'Cancel' to terminate the application";
            return DialogUtils.OkCancelDialog(header, message);
        }
    }

    public static class ConfirmedError extends IllegalStateException {
        public ConfirmedError(String message, Throwable cause) {
            super(message, cause);
        }

        public ConfirmedError(String s) {
            super(s);
        }
    }
}
