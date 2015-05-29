/**
 * 
 */
package jenkins.plugins.livingdoc;

import java.io.Serializable;


/**
 * Contains configuration parameter for the publisher.
 * 
 * @author Anis Ben Hamidene (NovaTec Consulting GmbH)
 * 
 */
public class LivingDocPublisherConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    private String testResultsPattern = null;
    private boolean publishToConfluence = false;
    private String confluenceUrl = null;
    private String filenamePrefix = null;
    private String confluenceSpaceKey = null;
    private String confluenceSiteTitle = null;
    private int failureThreshold = 0;

    /**
     * @return the filenamePrefix
     */
    public String getFilenamePrefix () {
        return filenamePrefix;
    }

    /**
     * @return the failureThreshold
     */
    public int getFailureThreshold () {
        return failureThreshold;
    }

    /**
     * @param failureThreshold the failureThreshold to set
     */
    public void setFailureThreshold (int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }

    /**
     * @param filenamePrefix the filenamePrefix to set
     */
    public void setFilenamePrefix (String filenamePrefix) {
        this.filenamePrefix = filenamePrefix;
    }

    public String getConfluenceSpaceKey () {
        return confluenceSpaceKey;
    }

    public void setConfluenceSpaceKey (String confluenceSpaceKey) {
        this.confluenceSpaceKey = confluenceSpaceKey;
    }

    public String getConfluenceSiteTitle () {
        return confluenceSiteTitle;
    }

    public void setConfluenceSiteTitle (String confluenceSiteTitle) {
        this.confluenceSiteTitle = confluenceSiteTitle;
    }

    /**
     * @return the testResultsPattern
     */
    public String getTestResultsPattern () {
        return testResultsPattern;
    }

    /**
     * @param testResultsPattern the testResultsPattern to set
     */
    public void setTestResultsPattern (String testResultsPattern) {
        this.testResultsPattern = testResultsPattern;
    }

    /**
     * @return the publishToConfluence
     */
    public boolean isPublishToConfluence () {
        return publishToConfluence;
    }

    /**
     * @param publishToConfluence the publishToConfluence to set
     */
    public void setPublishToConfluence (boolean publishToConfluence) {
        this.publishToConfluence = publishToConfluence;
    }

    /**
     * @return the confluenceUrl
     */
    public String getConfluenceUrl () {
        return confluenceUrl;
    }

    /**
     * @param confluenceUrl the confluenceUrl to set
     */
    public void setConfluenceUrl (String confluenceUrl) {
        this.confluenceUrl = confluenceUrl;
    }

}
