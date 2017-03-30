package web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PooledWeblog {
	private BufferedReader reader;
	private BufferedWriter writer;
	private int numOfThread;
	private boolean isFinished;
	private List<String> entries = Collections.synchronizedList(new LinkedList<>());
	
	public PooledWeblog(InputStream input, OutputStream output, int numOfThread) {
		this.reader = new BufferedReader(new InputStreamReader(input));
		this.writer = new BufferedWriter(new OutputStreamWriter(output));
		this.numOfThread = numOfThread;
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	public int getNumOfThread() {
		return numOfThread;
	}
	
	public void processLogFile() {
		for (int i = 0; i < numOfThread; i++) {
			Thread thread = new LookUpThread(this, entries);
			thread.start();
		}
		try {
			String entry = reader.readLine();
			while (entry != null) {
				if (entries.size() > numOfThread) {
					try {
						TimeUnit.MILLISECONDS.sleep((long) (1000.0 / numOfThread));
					} catch (InterruptedException exception) {
						// TODO: handle exception
						continue;
					}
				}
				synchronized(entries) {
					entries.add(0, entry);
					entries.notifyAll();
				}
				entry = reader.readLine();
				Thread.yield();
			}
		} catch (IOException exception) {
			// TODO: handle exception
			exception.printStackTrace();
		}
		
		this.isFinished = true;
		
		synchronized(entries) {
			entries.notifyAll();
		}
	}
	
	public void log(String entry) throws IOException {
		writer.write(entry + System.getProperty("line.separator", "\r\n"));
		writer.flush();
	}
	
	public static void main(String[] args) {
		PooledWeblog pWeblog;
		try {
			pWeblog = new PooledWeblog(new FileInputStream("src/web/web.log"), 
					System.out, 50);
			pWeblog.processLogFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
