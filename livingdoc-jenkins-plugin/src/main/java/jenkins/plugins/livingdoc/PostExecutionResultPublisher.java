package jenkins.plugins.livingdoc;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcRequest;

import com.greenpepper.repository.DocumentNotFoundException;
import com.greenpepper.util.CollectionUtil;
import com.greenpepper.util.URIUtil;

public class PostExecutionResultPublisher {

	private static Logger LOGGER = Logger.getLogger(PostExecutionResultPublisher.class.getCanonicalName());
	
	private Vector<String> definitionRef;
	private final String xmlReport;
	private String username = "";
	private String password = "";
	private String sut;
	private URI root;
	private String handler;

	public PostExecutionResultPublisher(String url, String location, String xmlReport) throws Exception {
		this.xmlReport = xmlReport;
		init(url,location);
		

	}

	public void specificationDone() throws Exception {
		String[] args1 = args(definitionRef);
		URI location = URI.create(URIUtil.raw(definitionRef.get(1)));
		Vector args = CollectionUtil.toVector(args1[1], args1[2],
				CollectionUtil.toVector(location.getFragment(), definitionRef.get(4), sut, xmlReport));

		String msg = (String) getXmlRpcClient().execute(new XmlRpcRequest(handler + ".saveExecutionResult", args));

		if (!("<success>".equals(msg))) {
			throw new Exception(msg);
		}

	}
	
	private void init(String url, String location) throws Exception {
		LOGGER.entering(getClass().getCanonicalName(), "init", new String[]{url,location});
		this.root = URI.create(URIUtil.raw(url));

		handler = URIUtil.getAttribute(root, "handler");
		if (handler == null)
			throw new IllegalArgumentException("No handler specified");

		sut = URIUtil.getAttribute(root, "sut");
		if (sut == null)
			throw new IllegalArgumentException("No sut specified");

		this.definitionRef = getDefinition(location);
		/**
		 * [com.greenpepper.runner.repository.AtlassianRepository,
		 * https://s415vmmt060.detss.corpintra.net/confluence/rpc/xmlrpc?handler=greenpepper1#XLD, greenpepper, 5c23h8kt, ST - TV -
		 * Kundenwunsch E4-E6]
		 */
		LOGGER.exiting(getClass().getCanonicalName(), "init");
		

	}	
	private String[] args(Vector<String> definition) {
		String[] args = new String[3];
		args[0] = definition.get(1);// includeStyle ? definition.get(1) : withNoStyle(definition.get(1));
		args[1] = StringUtils.isEmpty(username) ? definition.get(2) : username;
		args[2] = StringUtils.isEmpty(password) ? definition.get(3) : password;
		return args;
	}

	private Vector<String> getDefinition(String location) throws Exception {
		String path = getPath(location);
		String[] parts = path.split("/", 2);
		String repoUID = parts[0];
		if (parts.length == 1)
			throw new DocumentNotFoundException(location);

		Vector<Vector<String>> definitions = downloadSpecificationsDefinitions(repoUID);
		return getDefinitionFor(definitions, parts[1]);
	}

	private String getPath(String uri) {
		return URI.create(URIUtil.raw(uri)).getPath();
	}

	@SuppressWarnings("unchecked")
	private Vector<Vector<String>> downloadSpecificationsDefinitions(String repoUID) throws Exception {
		Vector<Vector<String>> definitions = (Vector<Vector<String>>) getXmlRpcClient().execute(
				new XmlRpcRequest(handler + ".getListOfSpecificationLocations", CollectionUtil.toVector(repoUID, sut)));
		checkForErrors(definitions);
		return definitions;
	}

	private XmlRpcClient getXmlRpcClient() throws MalformedURLException {
		return new XmlRpcClient(root.getScheme() + "://" + root.getAuthority() + root.getPath());
	}

	private void checkErrors(Object object) throws Exception {
		if (object instanceof Exception) {
			throw (Exception) object;
		}

		if (object instanceof String) {
			String msg = (String) object;
			if (!StringUtils.isEmpty(msg) && msg.indexOf("<exception>") > -1)
				throw new Exception(msg.replace("<exception>", ""));
		}
	}

	private Vector<String> getDefinitionFor(Vector<Vector<String>> definitions, String location) throws DocumentNotFoundException {
		for (Vector<String> def : definitions) {
			if (def.get(4).equals(location))
				return def;
		}
		throw new DocumentNotFoundException(location);
	}

	@SuppressWarnings("unchecked")
	private void checkForErrors(Object xmlRpcResponse) throws Exception {
		if (xmlRpcResponse instanceof Vector) {
			Vector temp = (Vector) xmlRpcResponse;
			if (!temp.isEmpty()) {
				checkErrors(temp.elementAt(0));
			}
		} else if (xmlRpcResponse instanceof Hashtable) {
			Hashtable<String, ?> table = (Hashtable<String, ?>) xmlRpcResponse;
			if (!table.isEmpty()) {
				checkForErrors(table.get("<exception>"));
			}
		} else {
			checkErrors(xmlRpcResponse);
		}
	}
}