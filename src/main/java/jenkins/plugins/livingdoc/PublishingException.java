package jenkins.plugins.livingdoc;

/**
 * Exception to handle errors publishing the results to confluence.
 */
@SuppressWarnings("serial")
public class PublishingException extends RuntimeException {

    private ConfluenceConfig confluenceConfig;

    public PublishingException(ConfluenceConfig confluenceConfig) {
        this.confluenceConfig = confluenceConfig;
    }

    public PublishingException(ConfluenceConfig confluenceConfig, String message) {
        super(message);
        this.confluenceConfig = confluenceConfig;
    }

    public PublishingException(ConfluenceConfig confluenceConfig, Throwable cause) {
        super(cause);
        this.confluenceConfig = confluenceConfig;
    }

    @Override
    public String getMessage() {
        return String.format("%s . %n Confluence Configuration: %s", super.getMessage(), confluenceConfig == null ? ""
            : confluenceConfig.toString());
    }

}
