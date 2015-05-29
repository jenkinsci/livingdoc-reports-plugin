package jenkins.plugins.livingdoc;

import hudson.FilePath;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


public class PostExecutionResultPublisherTest {

    private static File report =
        new File(
            "src/test/resources/jenkins/plugins/livingdoc/manual/test/livingdoc-reports/smoketest/smoke_test-Daimler Xentry Portal Confluence-XLD-ST - Support Anfrage.html.xml");

    @BeforeClass
    public static void init () {

        // System.setProperty("com.sun.net.ssl.checkRevocation", "false");
        // System.setProperty("javax.net.ssl.trustStore",
        // "e:/dev/keystores/daimler.git.jks");
        // System.setProperty("javax.net.ssl.trustStorePassword", "xxxx");
        // System.setProperty("file.encoding", "UTF-8");
        // System.setProperty("java.net.preferIPv4Stack", "true");

    }

    @Ignore
    @Test
    public void testPublish () throws Exception {
        String url =
            "https://s415vmmt060.detss.corpintra.net/confluence/rpc/xmlrpc?handler=livingdoc1&sut=XP-DEV0&includeStyle=true&implemented=true#XLD";
        String location = "Daimler Xentry Portal Confluence-XLD/ST - Support Anfrage";
        // String handler = "greepepper1";
        PostExecutionResultPublisher pub = new PostExecutionResultPublisher(url, location, loadRepoprt());
        XmlReportReader xmlReportReader = new XmlReportReader();
        xmlReportReader.parse(new FilePath(report));

        pub.specificationDone();
    }

    private String loadRepoprt () throws IOException {
        return FileUtils.readFileToString(report);
    }
}
