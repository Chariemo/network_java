package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;

public class ClientTester {
	
	public static void main(String[] args) {
		
		int port = 5678;
		ServerSocket serversocket = null;
		try {
			serversocket = new ServerSocket(port);
			System.out.println("Listening for connections on port " + serversocket.getLocalPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("The port " + port + "has been used!");
		}
		
		
		while (true) {
			try {
				Socket connection = serversocket.accept();
				try {
					System.out.println("The host " + connection.getInetAddress() + " has been connected");
					Thread input = new Thread(new InputThread(connection.getInputStream()));
					input.start();
					Thread output = new Thread(new OutputThread(connection.getOutputStream()));
					output.start();
					
					try {
						input.join();
						output.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						System.out.println("input or output Thread has been interrupted");
					}
				} catch (IOException exception) {
					exception.printStackTrace();
				} finally {
					if (connection != null) {
						try {
							connection.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
						
		}
	}
	
}

class InputThread implements Runnable {
	
	private InputStream reader;
	
	public InputThread(InputStream reader) {
		this.reader = reader;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
				int i = reader.read();
				if (i == -1) {
					break;
				}
				System.out.write(i);
			}
		} catch (SocketException exception) {
			
		} catch (IOException exception) {
			System.err.println(exception);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
			}
		}
	}
	
}

class OutputThread implements Runnable {

	private Writer writer;
	
	public OutputThread(OutputStream writer) {
		this.writer = new OutputStreamWriter(writer);
	}
	
	@Override
	public void run() {
		String line;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			while (true) {
				line = bufferedReader.readLine();
				if (line.equals(".")) {
					break;
				}
				writer.write(line + "\r\n");
				writer.flush();
			}
		} catch (IOException exception) {
			// TODO: handle exception
			
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
