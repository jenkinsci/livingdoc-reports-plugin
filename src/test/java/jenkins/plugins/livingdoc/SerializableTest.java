package jenkins.plugins.livingdoc;

import hudson.XmlFile;
import hudson.model.FreeStyleBuild;
import info.novatec.testit.livingdoc.Statistics;
import info.novatec.testit.livingdoc.TimeStatistics;
import jenkins.plugins.livingdoc.results.BuildResult;
import jenkins.plugins.livingdoc.results.BuildSummaryResult;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SerializableTest  {

    @ClassRule
    public static JenkinsRule rule = new JenkinsRule();

    @ClassRule
    public static TemporaryFolder tmp = new TemporaryFolder();

    @Mock
    FreeStyleBuild mockRun;

    @Test
    public void testBuildReportBeanSerializable() throws IOException {
        BuildReportBean buildReportBean = createBuildReportBean();

        shouldBeSerializable(buildReportBean);
    }

    @Test
    public void testSummaryBuildReportBeanSerializable() throws IOException {
        SummaryBuildReportBean summaryBuildReportBean = createSummaryBuildResult();

        shouldBeSerializable(summaryBuildReportBean);
    }

    @Test
    public void testBuildResultSerializable() throws IOException {
        BuildResult buildResult = new BuildResult(mockRun, createBuildReportBean());

        shouldBeSerializable(buildResult);
    }

    @Test
    public void testBuildSummaryResultSerializable() throws IOException {
        BuildSummaryResult buildSummaryResult = new BuildSummaryResult(mockRun, createSummaryBuildResult());

        shouldBeSerializable(buildSummaryResult);
    }

    private void shouldBeSerializable(Object serializable) throws IOException {
        XmlFile f = new XmlFile(new File(tmp.getRoot(), serializable.getClass().getCanonicalName() + ".xml"));
        f.write(serializable);
        assertTrue(f.exists());
    }

    private SummaryBuildReportBean createSummaryBuildResult() {
        SummaryBuildReportBean summaryBuildReportBean = new SummaryBuildReportBean(1);
        summaryBuildReportBean.addBuildReport(createBuildReportBean());
        return summaryBuildReportBean;
    }

    private BuildReportBean createBuildReportBean() {
        BuildReportBean buildReportBean = new BuildReportBean(1, 1);
        buildReportBean.setStatistics(new Statistics());
        buildReportBean.setTimeStatistics(new TimeStatistics());
        buildReportBean.setXmlReport("abc");
        buildReportBean.setExternalUrl("def");
        buildReportBean.setName("buildReportBean");
        buildReportBean.setResultFile("resultfile");
        buildReportBean.setResult("success");
        return buildReportBean;
    }
}
