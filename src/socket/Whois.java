package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Whois {

	public final static int DEFAULT_PORT = 43;
	public final static String DEFAULT_HOST = "whois.internic.net";
	
	private int port = DEFAULT_PORT;
	private InetAddress host;
	
	public Whois(InetAddress host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public Whois(InetAddress host) {
		this(host, DEFAULT_PORT);
	}
	
	public Whois(String hostname, int port) throws UnknownHostException {
		this(InetAddress.getByName(hostname), port);
	}
	
	public Whois(String hostname) throws UnknownHostException {
		this(InetAddress.getByName(hostname), DEFAULT_PORT);
	}
	
	public Whois() throws UnknownHostException {
		this(DEFAULT_HOST, DEFAULT_PORT);
	}
	
	public static class SearchFor {
		
		public static SearchFor ANY = new SearchFor();
		public static SearchFor NETWORK = new SearchFor();
		public static SearchFor PERSON = new SearchFor();
		public static SearchFor HOST = new SearchFor();
		public static SearchFor DOMAIN = new SearchFor();
		public static SearchFor ORGANIZATION = new SearchFor();
		public static SearchFor GROUP  = new SearchFor();
		public static SearchFor GATEWAY = new SearchFor();
		public static SearchFor ASN = new SearchFor();
		
		private SearchFor() {
			
		}
	}
	
	public static class SearchIn {
		
		public static SearchIn ALL = new SearchIn();
		public static SearchIn	NAME = new SearchIn();
		public static SearchIn MAILBOX = new SearchIn();
		public static SearchIn HANDLE = new SearchIn();
		
		private SearchIn() {
			
		}
	}
	
	public String lookUnNames(String target, SearchFor category, SearchIn group, boolean exactMatch) throws IOException {
		
		String suffix = "";
		if (!exactMatch) {
			suffix = ".";
		}
		
		String searchInlabel = "";
		String searchForlabel = "";
		
		if (group == SearchIn.ALL) {
			searchInlabel = "";
		}
		else if (group == SearchIn.NAME) {
			searchInlabel = "Name ";
		}
		else if (group == SearchIn.MAILBOX) {
			searchInlabel = "Mailbox ";
		}
		else if (group == SearchIn.HANDLE) {
			searchInlabel = "!";
		}
		
		if (category == SearchFor.ASN) {
			searchForlabel = "ASN ";
		}
		else if (category == SearchFor.DOMAIN) {
			searchForlabel = "Domain ";
		}
		else if (category == SearchFor.GROUP) {
			searchForlabel = "Group ";
		}
		else if (category == SearchFor.HOST) {
			searchForlabel = "Host ";
		}
		else if (category == SearchFor.NETWORK) {
			searchForlabel = "Network ";
		}
		else if (category == SearchFor.GATEWAY) {
			searchForlabel = "Gateway ";
		}
		else if (category == SearchFor.ORGANIZATION) {
			searchForlabel = "Organization ";
		}
		else if (category == SearchFor.PERSON) {
			searchForlabel = "Person ";
		}
		
		String prefix = searchForlabel + searchInlabel;
		String query = prefix + target + suffix;
		
		Socket socket = new Socket(host, port);
		Writer out = new OutputStreamWriter(socket.getOutputStream(), "ASCII");
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ASCII"));
		out.write(query + "\r\n");
		out.flush();
		StringBuffer response = new StringBuffer();
		String line = null;
		while ((line = reader.readLine()) != null) {
			response.append(line);
			response.append("\r\n");
		}
		socket.close();
		
		return response.toString();
	}
	
	public InetAddress getHost() {
		return host;
	}
	
	public void setHost(String host) throws UnknownHostException {
		this.host = InetAddress.getByName(host);
	}
}
