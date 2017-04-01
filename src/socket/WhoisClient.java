package socket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class WhoisClient {
	
	public final static int DEFAULT_PORT = 43;
	public final static String DEFAULT_HOST = "whois.internic.com";
	
	public static void main(String[] args) {
		
		String serverName = System.getProperty("WHOIS_SERVER", DEFAULT_HOST);
		
		InetAddress address = null;
		
		try {
			address = InetAddress.getByName(serverName);
		} catch (UnknownHostException exception) {
			System.err.println("Error: Could not locate whois server " + serverName);
		}
		
		try {
			Socket socket = new Socket(address, DEFAULT_PORT);
			Writer out = new OutputStreamWriter(socket.getOutputStream(), "ASCII");
			for (int i = 0; i < args.length; i++) {
				out.write(args[i] + " ");
			}
			out.write("\r\n");
			out.flush();
			InputStream inputStream = new BufferedInputStream(socket.getInputStream());
			int c;
			while ((c = inputStream.read()) != -1) {
				System.out.write(c);
			}
		} catch (IOException exception) {
			System.err.println(exception);
		}
	}
}
