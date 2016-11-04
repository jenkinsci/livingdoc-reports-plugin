package jenkins.plugins.livingdoc;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;


/**
 * @author Anis Ben Hamidene
 * 
 */
public class ConfluenceConfig {
    private String confluenceSiteTitle;
    private String confluenceSpaceKey;
    private String confluenceUrl;
    private String filenamePrefix;
    private String sut;
    private String systemProperties;

    @DataBoundConstructor
    public ConfluenceConfig(String confluenceUrl, String confluenceSpaceKey, String confluenceSiteTitle, String sut,
        String filenamePrefix, String systemProperties) {
        super();
        this.confluenceUrl = StringUtils.trimToNull(confluenceUrl);
        this.confluenceSpaceKey = StringUtils.trimToNull(confluenceSpaceKey);
        this.confluenceSiteTitle = StringUtils.trimToNull(confluenceSiteTitle);
        this.sut = StringUtils.trimToNull(sut);
        this.filenamePrefix = StringUtils.trimToNull(filenamePrefix);
        this.systemProperties = StringUtils.trimToNull(systemProperties);
    }

    public String getConfluenceSiteTitle() {
        return confluenceSiteTitle;
    }

    public String getConfluenceSpaceKey() {
        return confluenceSpaceKey;
    }

    public String getConfluenceUrl() {
        return confluenceUrl;
    }

    public String getFilenamePrefix() {
        return filenamePrefix;
    }

    public String getSut() {
        return sut;
    }

    public String getSystemProperties() {
        return systemProperties;
    }

    public void setConfluenceSiteTitle(String confluenceSiteTitle) {
        this.confluenceSiteTitle = confluenceSiteTitle;
    }

    public void setConfluenceSpaceKey(String confluenceSpaceKey) {
        this.confluenceSpaceKey = confluenceSpaceKey;
    }

    public void setConfluenceUrl(String confluenceUrl) {
        this.confluenceUrl = confluenceUrl;
    }

    public void setFilenamePrefix(String filenamePrefix) {
        this.filenamePrefix = filenamePrefix;
    }

    public void setSut(String sut) {
        this.sut = sut;
    }

    public void setSystemProperties(String systemProperties) {
        this.systemProperties = systemProperties;
    }

}
