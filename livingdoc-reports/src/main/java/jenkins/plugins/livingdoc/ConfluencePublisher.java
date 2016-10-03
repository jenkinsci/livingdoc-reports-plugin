package jenkins.plugins.livingdoc;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcRequest;

import info.novatec.testit.livingdoc.repository.DocumentNotFoundException;
import info.novatec.testit.livingdoc.server.rpc.xmlrpc.XmlRpcDataMarshaller;
import info.novatec.testit.livingdoc.server.transfer.ExecutionResult;
import info.novatec.testit.livingdoc.server.transfer.SpecificationLocation;
import info.novatec.testit.livingdoc.util.CollectionUtil;
import info.novatec.testit.livingdoc.util.URIUtil;


public class ConfluencePublisher {

    private static final Charset DEF_CHARSET = Charset.forName("UTF-8");
    private static final String[] REMOVEABLE_POSTFIXES = { ".html.xml", ".xml", ".html", ".htm" };
    private static final String LD_HANDLER = "livingdoc1";
    private static Logger LOGGER = Logger.getLogger(ConfluencePublisher.class.getCanonicalName());

    private ConfluenceConfig confluenceConfig;

    public ConfluencePublisher(ConfluenceConfig confluenceConfig) {
        super();
        this.confluenceConfig = confluenceConfig;

    }

    public void publishToConfluence(SummaryBuildReportBean summaryBuildReport) {

        if (StringUtils.isNotEmpty(confluenceConfig.getSystemProperties())) {
            setSystemProperties();
        }
        for (BuildReportBean report : summaryBuildReport.getBuildReports()) {
            publishReportToConfluence(report);
        }
    }

    private void publishReportToConfluence(BuildReportBean report) {
        LOGGER.entering(getClass().getCanonicalName(), "publishToConfluence");
        try {
            LOGGER.fine("Publishing to Confluence : Url=" + confluenceConfig.getConfluenceUrl());
            LOGGER.finer("Current system properties:\n" + System.getProperties());

            String normalizedXmlReport = normalizeContent(report.getXmlReport());
            String location = getLocation(report.getName());
            
            SpecificationLocation specificationLocation = downloadSpecificationDefinition(location);

            saveExecutionResult(specificationLocation, normalizedXmlReport);
            LOGGER.fine("Publishing to Confluence succeeded");
            BuildLogger.info("Results successfully published to confluence:\n\t\tFile:" + report.getName());
        } catch (Exception e) {
            LOGGER.severe("Error publishing to Confluence " + e.getMessage());
            BuildLogger.severe("Error publishing results to confluence: " + e.getMessage() + "\n\t\tFile:"
                + report.getName() + "\n\t\tConfluence-URL:" + confluenceConfig.getConfluenceUrl());

        }
        LOGGER.exiting(getClass().getCanonicalName(), "publishToConfluence");
    }

    String getLocation(String filename) {
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

    private String normalizeContent(String original) {
        return StringUtils.toEncodedString(original.getBytes(DEF_CHARSET), DEF_CHARSET);

    }

    private void setSystemProperties() {
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

    private void saveExecutionResult(SpecificationLocation specificationLocation, String normalizedXmlReport) throws Exception {
        URI location = URI.create(URIUtil.raw(specificationLocation.getBaseTestUrl()));

        ExecutionResult execResult = new ExecutionResult();
        execResult.setSpaceKey(location.getFragment());
        execResult.setPageTitle(specificationLocation.getSpecificationName());
        execResult.setSut(confluenceConfig.getSut());
        execResult.setXmlReport(normalizedXmlReport);
        
        Vector<Serializable> args = CollectionUtil.toVector(specificationLocation.getUsername(), specificationLocation.getPassword(), execResult.marshallize());
        LOGGER.finest(String.format("Publishing execution rsult to confluence :%n %s", execResult));
        String msg =
            ( String ) getXmlRpcClient().execute(new XmlRpcRequest(LD_HANDLER + ".saveExecutionResult", args));

        if ( ! ( ExecutionResult.SUCCESS.equals(msg) )) {
            throw new Exception(msg);
        }

    }

    private SpecificationLocation downloadSpecificationDefinition(String location) throws Exception {
        String path = URI.create(URIUtil.raw(location)).getPath();
        String[] parts = path.split("/", 2);
        String repoUID = parts[0];
        if (parts.length == 1)
            throw new DocumentNotFoundException(location);

        List<SpecificationLocation> specLocationList = downloadSpecificationLocations(repoUID);
        return findSpecificationLocationFor(specLocationList, parts[1]);
    }

    @SuppressWarnings("unchecked")
    private List<SpecificationLocation> downloadSpecificationLocations(String repoUID) throws Exception {
        Vector<Vector<String>> rawSpecLocationList =
            ( Vector<Vector<String>> ) getXmlRpcClient().execute(
                new XmlRpcRequest(LD_HANDLER + ".getListOfSpecificationLocations", CollectionUtil.toVector(repoUID,
                    confluenceConfig.getSut())));
        checkForErrors(rawSpecLocationList);

        List<SpecificationLocation> specLocationList = new ArrayList<SpecificationLocation>(rawSpecLocationList.size());

        for (Vector<String> rawSpecLoc : rawSpecLocationList) {
            specLocationList.add(XmlRpcDataMarshaller.toSpecificationLocation(rawSpecLoc));
        }
        return specLocationList;
    }

    private XmlRpcClient getXmlRpcClient() throws MalformedURLException {
        URI xmlRpcUri = generateXmlRpcUrl();
        return new XmlRpcClient(xmlRpcUri.getScheme() + "://" + xmlRpcUri.getAuthority() + xmlRpcUri.getPath());
    }

    private SpecificationLocation findSpecificationLocationFor(List<SpecificationLocation> specLocationList, String location)
        throws DocumentNotFoundException {
        for (SpecificationLocation loc : specLocationList) {
            if (loc.getSpecificationName().equals(location))
                return loc;
        }
        throw new DocumentNotFoundException(location);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void checkForErrors(Object xmlRpcResponse) throws Exception {
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

    private void checkErrors(Object object) throws Exception {
        if (object instanceof Exception) {
            throw ( Exception ) object;
        }

        if (object instanceof String) {
            String msg = ( String ) object;
            if ( ! StringUtils.isEmpty(msg) && msg.indexOf("<exception>") > - 1)
                throw new Exception(msg.replace("<exception>", ""));
        }
    }

    private URI generateXmlRpcUrl() {
        // rpc/xmlrpc?handler=greenpepper1&amp;sut=XP-CiDEV&amp;includeStyle=true&amp;implemented=true#lderepko
        String baseUrl =
            confluenceConfig.getConfluenceUrl().endsWith("/") ? confluenceConfig.getConfluenceUrl() : confluenceConfig
                .getConfluenceUrl()
                + "/";

        String xmlRPCUrl =
            String.format("%srpc/xmlrpc?handler=%s&amp;sut=%s&amp;includeStyle=true&amp;implemented=true#%s", baseUrl,
                LD_HANDLER, confluenceConfig.getSut(), confluenceConfig.getConfluenceSpaceKey());
        return URI.create(URIUtil.raw(xmlRPCUrl));
    }
}
