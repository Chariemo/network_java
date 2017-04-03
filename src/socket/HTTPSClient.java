package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class HTTPSClient {
	
	public static void main(String[] args) {
		
		if (args.length == 0) {
			System.out.println("Usage: java HTTPSClient host");
			return;
		}
		
		int port = 443;
		String host = args[0];
		
		try {
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
			
			String[] supported = socket.getSupportedCipherSuites();
			socket.setEnabledCipherSuites(supported);
			
			Writer writer = new OutputStreamWriter(socket.getOutputStream());
			
			writer.write("GET http:"
					+ "//" + host + "/ HTTP/1.1\r\n");
			writer.write("Host: " + host + "\r\n");
			writer.write("\r\n");
			writer.flush();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String string;
			while ((string = reader.readLine()).equals("")) {
				System.out.println(string);
			}
			
			System.out.println();
			
			String contentLength = reader.readLine();
			int length = Integer.MAX_VALUE;
			try {
				length = Integer.parseInt(contentLength.trim(), 16);
			} catch (NumberFormatException  exception) {
				
			}
			System.out.println(contentLength);
			
			int c;
			int i = 0;
			while ((c = reader.read()) != -1 && i++ < length) {
				System.out.write((char)c);
			}
			System.out.println();
			writer.close();
			reader.close();
			socket.close();
		} catch (IOException exception) {
			System.err.println(exception);
		}
	}
}
