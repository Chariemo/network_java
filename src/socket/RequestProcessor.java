package socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.text.html.HTML;

public class RequestProcessor implements Runnable {

	private static List pool = Collections.synchronizedList(new LinkedList<>());
	private File documentRootDirection;
	private String indexFileName;
	
	public RequestProcessor(File documentRootDirection, String indexFileName) {
		
		this.documentRootDirection = documentRootDirection;
		try {
			this.documentRootDirection = documentRootDirection.getCanonicalFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
		this.indexFileName = indexFileName;
	}
	@Override
	public void run() {
		
		String root = documentRootDirection.getPath();
		while (true) {
			Socket connection;
			synchronized (pool) {
				while (pool.isEmpty()) {
					try {
						pool.wait();
					} catch (InterruptedException e) {
					
					}
				}
				connection = (Socket) pool.remove(0);
			}
			try {
				String filename;
				String contentType;
				OutputStream raw = new BufferedOutputStream(connection.getOutputStream());
				Writer writer = new OutputStreamWriter(raw);
				Reader reader = new InputStreamReader(new BufferedInputStream(connection.getInputStream()));
				
				StringBuffer requestLine = new StringBuffer();
				int c;
				while (true) {
					c = reader.read();
					if (c == '\r' || c == '\n' || c == -1) {
						break;
					}
					requestLine.append((char)c);
				}
				
				String get = requestLine.toString();
				System.out.println(get);
				
				StringTokenizer stringTokenizer = new StringTokenizer(get);
				String method = stringTokenizer.nextToken();
				String version = "";
				
				if (method.equals("GET")) {
					filename = stringTokenizer.nextToken();
					if (filename.endsWith("/")) {
						filename += indexFileName;
					}
					contentType = guessContentTypeFromName(filename);
					if (stringTokenizer.hasMoreTokens()) {
						version = stringTokenizer.nextToken();
					}
					
					File theFile = new File(documentRootDirection, filename.substring(1, filename.length()));
					
					if (theFile.canRead() && theFile.getCanonicalPath().startsWith(root)) {
						DataInputStream fInputStream = new DataInputStream(new BufferedInputStream(
								new FileInputStream(theFile)));
						byte[] theData = new byte[(int) theFile.length()];
						
						fInputStream.readFully(theData);
						fInputStream.close();
						if (version.startsWith("HTTP")) {
							writer.write("HTTP/1.2 200 OK\r\n");
							Date now = new Date();
							writer.write("Date: " + now + "\r\n");
							writer.write("Server: JHTTP/1.0\r\n");
							writer.write("Content-length: " + theData.length + "\r\n");
							writer.write("Content-type: " + contentType + "\r\n\r\n");
							writer.flush();
						}
						
						raw.write(theData);
						raw.flush();
					}
					else {
						if (version.startsWith("HTTP")) {
							writer.write("HTTP/1.2 404 File not Found\r\n");
							Date now = new Date();
							writer.write("Date: " + now + "\r\n");
							writer.write("Server: JHTTP/1.0\r\n");
							writer.write("Content-type: " + contentType + "\r\n\r\n");
						}
						writer.write("<HTML>\r\n");
						writer.write("<HEAD><TITLE>File Not Found</TITLE>\r\n");
						writer.write("</HEAD>\r\n");
						writer.write("<BODY>\r\n");
						writer.write("<H1>HTTP Error 404: File Not Found</H1>\r\n");
						writer.write("</BODY></HTML>\r\n");
						writer.flush();
					}
				}
				else {
					if (version.startsWith("HTTP")) {
						writer.write("HTTP/1.2 501 NOT Implemented\r\n");
						Date now = new Date();
						writer.write("Date: " + now + "\r\n");
						writer.write("Server: JHTTP 1.0\r\n");
						writer.write("Content-type: text/html\r\n\r\n");
					}
					
					writer.write("<HTML>\r\n");
					writer.write("<HEAD><TITLE>Not Implemented</TITLE>\r\n");
					writer.write("</HEAD>\r\n");
					writer.write("<BODY>\r\n");
					writer.write("<H1>HTTP Error 501: Not Implemented</H1>\r\n");
					writer.write("</BODY></HTML>\r\n");
					writer.flush();
				}
				
			} catch (IOException exception) {
				
			} finally {
				try {
					connection.close();
				} catch (IOException exception2) {
					
				}
			}
		}
		
	}
	
	public static void processRequest(Socket socket) {
		
		synchronized(pool) {
			pool.add(pool.size(), socket);
			pool.notify();
		}
	}
	
	public static String guessContentTypeFromName(String name) {
		
		if (name.endsWith(".html") || name.endsWith(".htm")) {
			return "text/html";
		}
		else if (name.endsWith(".txt") || name.endsWith(".java")) {
			return "text/plain";
		}
		else if (name.endsWith(".class")) {
			return "application/octet-stream";
		}
		else if (name.endsWith(".gif")) {
			return "image/gif";
		}
		else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
			return "image/jpeg";
		}
		else {
			return "text/plain";
		}
	}

}
