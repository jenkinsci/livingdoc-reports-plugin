package jenkins.plugins.livingdoc;

import info.novatec.testit.livingdoc.repository.DocumentNotFoundException;
import info.novatec.testit.livingdoc.util.CollectionUtil;
import info.novatec.testit.livingdoc.util.URIUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcRequest;


public class ConfluencePublisher {

    private static final String[] REMOVEABLE_POSTFIXES = { ".html.xml", ".xml", ".html", ".htm" };
    private static final String LD_HANDLER = "livingdoc1";
    private static Logger LOGGER = Logger.getLogger(ConfluencePublisher.class.getCanonicalName());

    private ConfluenceConfig confluenceConfig;

    private Vector<String> definitionRef;
    private String normalizedXmlReport;
    private String username = "";
    private String password = "";
    private URI xmlRpcUri;

    public ConfluencePublisher (ConfluenceConfig confluenceConfig) {
        super();
        this.confluenceConfig = confluenceConfig;

    }

    public void publishToConfluence (SummaryBuildReportBean summaryBuildReport) {

        if (StringUtils.isNotEmpty(confluenceConfig.getSystemProperties())) {
            setSystemProperties();
        }
        for (BuildReportBean report : summaryBuildReport.getBuildReports()) {
            publishReportToConfluence(report);
        }
    }

    private void publishReportToConfluence (BuildReportBean report) {
        LOGGER.entering(getClass().getCanonicalName(), "publishToConfluence");
        try {
            LOGGER.fine("Publishing to Confluence : Url=" + confluenceConfig.getConfluenceUrl());
            LOGGER.finer("Current system properties:\n" + System.getProperties());

            String location = getLocation(report.getName());
            normalizedXmlReport = normalizeContent(report.getResultFile());

            this.xmlRpcUri = generateXmlRpcUrl();
            this.definitionRef = downloadSpecificationDefinition(location);

            saveExecutionResult();
            LOGGER.fine("Publishing to Confluence succeeded");
            BuildLogger.info("Results successfully published to confluence:\n\t\tFile:" + report.getName());
        } catch (Exception e) {
            LOGGER.severe("Error publishing to Confluence " + e.getMessage());
            BuildLogger.severe("Error publishing results to confluence: " + e.getMessage() + "\n\t\tFile:"
                + report.getName() + "\n\t\tConfluence-URL:" + confluenceConfig.getConfluenceUrl());

        }
        LOGGER.exiting(getClass().getCanonicalName(), "publishToConfluence");
    }

    String getLocation (String filename) {
        LOGGER.entering(getClass().getCanonicalName(), "getLocation", filename);
        StringBuilder location = new StringBuilder(filename);
        if (StringUtils.isNotEmpty(confluenceConfig.getFilenamePrefix())
            && filename.startsWith(confluenceConfig.getFilenamePrefix())) {
            location.delete(0, confluenceConfig.getFilenamePrefix().length());
        }

        for (String postfix : REMOVEABLE_POSTFIXES) {
            if (location.toString().endsWith(postfix)) {
                int postfixLength = postfix.length();
                int filenameLength = location.length();
                location.delete(filenameLength - postfixLength, filenameLength);
                break;
            }
        }
        if (location.indexOf(confluenceConfig.getConfluenceSiteTitle()) >= 0) {
            int slashIndex = location.indexOf("-");
            slashIndex = location.indexOf("-", slashIndex + 1);
            location.replace(slashIndex, slashIndex + 1, "/");
        } else {
            location.insert(0, "/").insert(0, confluenceConfig.getConfluenceSpaceKey()).insert(0, "-").insert(0,
                confluenceConfig.getConfluenceSiteTitle());
        }
        LOGGER.exiting(getClass().getCanonicalName(), "getLocation", location);
        return location.toString();
    }

    private String normalizeContent (String original) {
        return StringUtils.toEncodedString(original.getBytes(), Charset.forName("UTF-8"));

    }

    private void setSystemProperties () {
        Properties sysProps = System.getProperties();
        StringInputStream sis = new StringInputStream(confluenceConfig.getSystemProperties());

        try {
            sysProps.load(sis);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                sis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @SuppressWarnings ( "rawtypes" )
    private void saveExecutionResult () throws Exception {
        URI location = URI.create(URIUtil.raw(definitionRef.get(1)));
        Vector args =
            CollectionUtil.toVector(getUserName(), getPassword(), CollectionUtil.toVector(location.getFragment(),
                definitionRef.get(4), confluenceConfig.getSut(), normalizedXmlReport));

        String msg = ( String ) getXmlRpcClient().execute(new XmlRpcRequest(LD_HANDLER + ".saveExecutionResult", args));

        if ( ! ( "<success>".equals(msg) )) {
            throw new Exception(msg);
        }

    }

    private String getUserName () {
        return StringUtils.isEmpty(username) ? definitionRef.get(2) : username;
    }

    private String getPassword () {
        return StringUtils.isEmpty(password) ? definitionRef.get(3) : password;
    }

    private Vector<String> downloadSpecificationDefinition (String location) throws Exception {
        String path = URI.create(URIUtil.raw(location)).getPath();
        String[] parts = path.split("/", 2);
        String repoUID = parts[0];
        if (parts.length == 1)
            throw new DocumentNotFoundException(location);

        Vector<Vector<String>> definitions = downloadSpecificationsDefinitions(repoUID);
        return findDefinitionFor(definitions, parts[1]);
    }

    @SuppressWarnings ( "unchecked" )
    private Vector<Vector<String>> downloadSpecificationsDefinitions (String repoUID) throws Exception {
        Vector<Vector<String>> definitions =
            ( Vector<Vector<String>> ) getXmlRpcClient().execute(
                new XmlRpcRequest(LD_HANDLER + ".getListOfSpecificationLocations", CollectionUtil.toVector(repoUID,
                    confluenceConfig.getSut())));
        checkForErrors(definitions);
        return definitions;
    }

    private XmlRpcClient getXmlRpcClient () throws MalformedURLException {
        return new XmlRpcClient(xmlRpcUri.getScheme() + "://" + xmlRpcUri.getAuthority() + xmlRpcUri.getPath());
    }

    private void checkErrors (Object object) throws Exception {
        if (object instanceof Exception) {
            throw ( Exception ) object;
        }

        if (object instanceof String) {
            String msg = ( String ) object;
            if ( ! StringUtils.isEmpty(msg) && msg.indexOf("<exception>") > - 1)
                throw new Exception(msg.replace("<exception>", ""));
        }
    }

    private Vector<String> findDefinitionFor (Vector<Vector<String>> definitions, String location)
        throws DocumentNotFoundException {
        for (Vector<String> def : definitions) {
            if (def.get(4).equals(location))
                return def;
        }
        throw new DocumentNotFoundException(location);
    }

    @SuppressWarnings ( { "unchecked", "rawtypes" } )
    private void checkForErrors (Object xmlRpcResponse) throws Exception {
        if (xmlRpcResponse instanceof Vector) {
            Vector temp = ( Vector ) xmlRpcResponse;
            if ( ! temp.isEmpty()) {
                checkErrors(temp.elementAt(0));
            }
        } else if (xmlRpcResponse instanceof Hashtable) {
            Hashtable<String, ? > table = ( Hashtable<String, ? > ) xmlRpcResponse;
            if ( ! table.isEmpty()) {
                checkForErrors(table.get("<exception>"));
            }
        } else {
            checkErrors(xmlRpcResponse);
        }
    }

    private URI generateXmlRpcUrl () {
        // rpc/xmlrpc?handler=greenpepper1&amp;sut=XP-CiDEV&amp;includeStyle=true&amp;implemented=true#lderepko
        StringBuilder baseUrl = new StringBuilder(confluenceConfig.getConfluenceUrl());
        if ( ! confluenceConfig.getConfluenceUrl().endsWith("/")) {
            baseUrl.append("/");
        }
        baseUrl.append("rpc/xmlrpc?handler=" + LD_HANDLER).append("&amp;sut=" + confluenceConfig.getSut()).append(
            "&amp;includeStyle=true&amp;implemented=true#").append(confluenceConfig.getConfluenceSpaceKey());
        return URI.create(URIUtil.raw(baseUrl.toString()));
    }
}
