package jenkins.plugins.livingdoc;

import java.util.logging.Logger;

import hudson.model.BuildListener;


/**
 * This logger writes information to the build console.
 * 
 * @author Anis Ben Hamidene
 *
 */
public class BuildLogger {
    private static BuildListener buildListener = null;
    private static Logger LOGGER = Logger.getLogger(BuildLogger.class.getCanonicalName());

    private static final String LD_MARKER = "[LIVINGDOC] ";
    private static final String LD_INFO_MARKER = LD_MARKER + " INFO :";
    private static final String LD_WARN_MARKER = LD_MARKER + " WARNING :";
    private static final String LD_SEVERE_MARKER = LD_MARKER + " SEVERE :";

    public static void intialize (BuildListener listener) {
        buildListener = listener;
    }

    /**
     * Writes an Info message to the build logger;
     * 
     * @param message
     */
    public static void info (String message) {
        if (buildListener != null && buildListener.getLogger() != null) {
            buildListener.getLogger().println(LD_INFO_MARKER + message);
        } else {
            LOGGER.info(message);
        }
    }

    /**
     * Writes an Info message to the build logger;
     * 
     * @param message
     */
    public static void warn (String message) {
        if (buildListener != null && buildListener.getLogger() != null) {
            buildListener.getLogger().println(LD_WARN_MARKER + message);
        } else {
            LOGGER.warning(message);
        }
    }

    /**
     * Writes an Info message to the build logger;
     * 
     * @param message
     */
    public static void severe (String message) {
        if (buildListener != null && buildListener.getLogger() != null) {
            buildListener.getLogger().println(LD_SEVERE_MARKER + message);
        } else {
            LOGGER.severe(message);
        }
    }
}
