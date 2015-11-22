package jenkins.plugins.livingdoc;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;


/**
 * @author Anis Ben Hamidene
 * 
 */
public class ConfluenceConfig {
    private String confluenceUrl = null;
    private String confluenceSpaceKey = null;
    private String confluenceSiteTitle = null;
    private String filenamePrefix = null;
    private String systemProperties;
    private String sut;

    @DataBoundConstructor
    public ConfluenceConfig (String confluenceUrl, String confluenceSpaceKey, String confluenceSiteTitle, String sut,
        String filenamePrefix, String systemProperties) {
        super();
        this.confluenceUrl = StringUtils.trimToNull(confluenceUrl);
        this.confluenceSpaceKey = StringUtils.trimToNull(confluenceSpaceKey);
        this.confluenceSiteTitle = StringUtils.trimToNull(confluenceSiteTitle);
        this.sut = StringUtils.trimToNull(sut);
        this.filenamePrefix = StringUtils.trimToNull(filenamePrefix);
        this.systemProperties = StringUtils.trimToNull(systemProperties);
    }

    public String getConfluenceUrl () {
        return confluenceUrl;
    }

    public void setConfluenceUrl (String confluenceUrl) {
        this.confluenceUrl = confluenceUrl;
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

    public String getFilenamePrefix () {
        return filenamePrefix;
    }

    public void setFilenamePrefix (String filenamePrefix) {
        this.filenamePrefix = filenamePrefix;
    }

    public String getSystemProperties () {
        return systemProperties;
    }

    public void setSystemProperties (String systemProperties) {
        this.systemProperties = systemProperties;
    }

    public String getSut () {
        return sut;
    }

    public void setSut (String sut) {
        this.sut = sut;
    }

}
