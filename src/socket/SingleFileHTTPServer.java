package socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleFileHTTPServer extends Thread{
	
	private byte[] content;
	private byte[] header;
	private int port = 80;
	
	public SingleFileHTTPServer(String data, String encoding, String MIMEType, int port) throws UnsupportedEncodingException {
		
		this(data.getBytes(encoding), encoding, MIMEType, port);
	}
	
	public SingleFileHTTPServer(byte[] data, String encoding, String MIMEType, int port) throws UnsupportedEncodingException {
		
		this.content = data;
		this.port = port;
		
		String header = "HTTP/1.0 200 OK\r\n" + "Server : OneFile 1.0\r\n"
				+ "Content-length: " + this.content.length + "\r\n" + "Content-type: " 
				+ MIMEType + "\r\n\r\n";
		this.header = header.getBytes("ASCII");
	}
	
	
	public void run() {
		
		try {
			
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Accepting connections on port " + serverSocket.getLocalPort());
			System.out.println("Data to be sent:");
			System.out.write(this.content);
			while (true) {
				
				Socket connection = null;
				try {
					
					connection = serverSocket.accept();
					System.out.println(connection.getInetAddress() + " has connected!");
					OutputStream writer = new BufferedOutputStream(connection.getOutputStream());
					InputStream reader = new BufferedInputStream(connection.getInputStream());
					
					StringBuffer request = new StringBuffer();
					while (true) {
						
						int c = reader.read();
						if (c == '\r' || c == '\n' || c == -1) {
							break;
						}
						request.append((char)c);
						
					}
					if (request.toString().indexOf("HTTP/") != -1) {
						writer.write(this.header);
					}
					writer.write(this.content);
					writer.flush();
				} catch (IOException exception) {
					
				} finally {
					if (connection != null) {
						connection.close();
					}
				}
			}
		} catch (IOException exception) {
			System.err.println("Could not start server. Port Occupied");
		}
	}
	
	public static void main(String[] args) {
		
		try {
			String contentType = "text/plain";
			if (args[0].endsWith(".html") || args[0].endsWith(".htm")) {
				contentType = "text/html";
			}
			
			InputStream reader = new FileInputStream(args[0]);
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			int b;
			while ((b = reader.read()) != -1) {
				writer.write(b);
			}
			
			byte[] data = writer.toByteArray();
			
			int port;
			try {
				port = Integer.parseInt(args[1]);
				if (port < 1 || port > 65535) {
					port = 80;
				} 
			} catch(Exception exception) {
				port = 80;
			}
			
			String encoding = "ASCII";
			if (args.length > 2) {
				encoding = args[2];
			}
			
			Thread thread = new SingleFileHTTPServer(data, encoding, contentType, port);
			thread.start();
		} catch (ArrayIndexOutOfBoundsException exception) {
			System.out.println("Usage: java SingleFileHTTPServer filename port encoding");
		} catch (Exception exception) {
			System.err.println(exception);
		}
	}
}
