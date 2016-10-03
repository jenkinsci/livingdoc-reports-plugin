package jenkins.plugins.livingdoc;

import java.util.logging.Logger;

import hudson.model.TaskListener;


/**
 * This logger writes information to the build console.
 * 
 * @author Anis Ben Hamidene
 *
 */
public class BuildLogger {
    private static TaskListener taskListener = null;
    private static Logger LOGGER = Logger.getLogger(BuildLogger.class.getCanonicalName());

    private static final String LD_MARKER = "[LIVINGDOC] ";
    private static final String LD_INFO_MARKER = LD_MARKER + " INFO :";
    private static final String LD_WARN_MARKER = LD_MARKER + " WARNING :";
    private static final String LD_SEVERE_MARKER = LD_MARKER + " SEVERE :";

    public static void intialize (TaskListener listener) {
        taskListener = listener;
    }

    public static void info (String message) {
        if (taskListener != null && taskListener.getLogger() != null) {
            taskListener.getLogger().println(LD_INFO_MARKER + message);
        } else {
            LOGGER.info(message);
        }
    }

    public static void warn (String message) {
        if (taskListener != null && taskListener.getLogger() != null) {
            taskListener.getLogger().println(LD_WARN_MARKER + message);
        } else {
            LOGGER.warning(message);
        }
    }

    public static void severe (String message) {
        if (taskListener != null && taskListener.getLogger() != null) {
            taskListener.getLogger().println(LD_SEVERE_MARKER + message);
        } else {
            LOGGER.severe(message);
        }
    }
}
