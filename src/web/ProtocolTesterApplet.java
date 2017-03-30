package web;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.TextArea;
import java.net.MalformedURLException;
import java.net.URL;

public class ProtocolTesterApplet extends Applet{
	/*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TextArea results = new TextArea();
	
	public void init() {
		this.setLayout(new BorderLayout());
		this.add("Center", results);
	}
	
	public void start() {
		String host = "www.peacefire.org";
		String file = "/bypass/SurfWatch";
		
		String[] schemes = {"http", "https", "ftp", "mailto", "telnet", "file", "ldap", "gopher", "jdbc",
				"rmi", "jndi", "jar", "doc", "netdoc", "nfs", "verbatim", "finger", "daytime", "systemresource"};
		for (int i = 0; i < schemes.length; i++) {
			try {
				URL url = new URL(schemes[i], host, file);
				results.append(schemes[i] + " is supported\r\n");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				results.append(schemes[i] + " is not supported\r\n");
			}
		}
	}
}
