package jenkins.plugins.livingdoc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class ConfluencePublisherTest {

	@Test
	public void getLocation() throws IOException, InterruptedException {
		String[][] values = new String[][] {
				{
						"prefix-Confluence Site Title-KEY-LL - My Executable Spec.html",
						"prefix-", 
						"Confluence Site Title", 
						"Confluence Site Title-KEY/LL - My Executable Spec" },
				{ "prefix-LL - My Executable Spec.html", 
						"prefix-",
						"Confluence Site Title", 
						"Confluence Site Title-KEY/LL - My Executable Spec" },
				{ "prefix-LL - My Executable Spec.html.xml",
						"prefix-",
						"Confluence Site Title", 
						"Confluence Site Title-KEY/LL - My Executable Spec" },
				{ "prefix-LL - My Executable Spec.htm", 
						"prefix-",
						"Confluence Site Title", 
						"Confluence Site Title-KEY/LL - My Executable Spec" },
				{ "prefix-LL - My Executable Spec.xml",
						"prefix-",
						"Confluence Site Title", 
						"Confluence Site Title-KEY/LL - My Executable Spec" },
				{ "LL - My Executable Spec.xml", null, "Confluence Site Title",
						
						"Confluence Site Title-KEY/LL - My Executable Spec" } };

		for(int i = 0; i < values.length; i++) {
			ConfluenceConfig config = new ConfluenceConfig(null,  "KEY", values[i][2], null, values[i][1], null);
			ConfluencePublisher pub = new ConfluencePublisher(config);
			String expectedLocation = values[i][3];
			String currLocation = pub.getLocation(values[i][0]);
			assertEquals(expectedLocation, currLocation);

		}
	}
}
