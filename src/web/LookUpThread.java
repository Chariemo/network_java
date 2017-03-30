package web;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class LookUpThread extends Thread {
	private List<String> entries;
	PooledWeblog pooledWeblog;
	
	public LookUpThread(PooledWeblog pooledWeblog, List<String> entries) {
		this.pooledWeblog = pooledWeblog;
		this.entries = entries;
	}
	
	public void run() {
		String entry;
		synchronized (entries) {
			while (entries.size() == 0) {
				if (pooledWeblog.isFinished()) {
					return;
				}
				try {
					entries.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			entry = entries.remove(entries.size() - 1);
		}
		
		int index = entry.indexOf(' ', 0);
		
		String remoteHost = entry.substring(0, index);
		String theRest = entry.substring(index, entry.length());
		
		try {
			remoteHost = InetAddress.getByName(remoteHost).getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			
		}
		
		try {
			pooledWeblog.log(remoteHost + theRest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread.yield();
	}
}
