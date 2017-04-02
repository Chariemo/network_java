package socket;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JHTTP extends Thread{

	private File documentRootDirection;
	private String indexFileName = "index.html";
	private ServerSocket serverSocket;
	private int numThreads = 50;

	public JHTTP(File documentRootDirection, int port, String indexFileName) throws IOException {
		
		if (!documentRootDirection.isDirectory()) {
			throw new IOException(documentRootDirection + " is not a direction");
		}
		this.documentRootDirection = documentRootDirection;
		this.indexFileName = indexFileName;
		this.serverSocket = new ServerSocket(port);
	}
	
	public JHTTP(File documentRootDirection, int port) throws IOException {
		this(documentRootDirection, port, "index.html");
	}
	
	public JHTTP(File documentRootDirection) throws IOException {
		this(documentRootDirection, 80, "index.html");
	}
	
	public void run() {
		
		for (int i = 0; i < numThreads; i++) {
			Thread thread = new Thread(new RequestProcessor(documentRootDirection, indexFileName));
			thread.start();
		}
		System.out.println("Accepting connections on port " + serverSocket.getLocalPort());
		System.out.println("Document Root: " + documentRootDirection);
		
		while (true) {
			
			try {
				Socket request = serverSocket.accept();
				RequestProcessor.processRequest(request);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
			}
			
		}
	}
	
	public static void main(String[] args) {
		
		File docRoot = null;
		try {
			docRoot = new File(args[0]);
		} catch (IndexOutOfBoundsException exception) {
			System.err.println("Usage: java JHTTP docroot port indexFile");
		}
		
		int port;
		try {
			port = Integer.parseInt(args[1]);
			if (port < 0 || port > 65535) {
				port = 80;
			}
		} catch (Exception exception) {
			port = 80;
		}
		
		JHTTP server;
		try {
			server = new JHTTP(docRoot, port);
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Server could not start because of an " + e.getClass());
			System.out.println(e);
		}
		
	
	}
}
