package me.async.sockyj.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

public class PipeRunnable implements Runnable {

	private static final int iddleTimeout = 11000;
	private static final int BUFFER_SIZE = 8192;
	
	private InputStream is;
	private OutputStream os;
	private long lastReadTime;
	private boolean eof;
	private boolean finished;
	private Object lock;
	
	public PipeRunnable(InputStream is, OutputStream os, Object lock)
	{
		this.is = is;
		this.os = os;
		this.eof = false;
		this.finished = false;
		this.lock = lock;
		lastReadTime = System.currentTimeMillis();
	}
	
	public long getLastReadTime()
	{
		return this.lastReadTime;
	}
	
	@Override
	public void run() {
		lastReadTime = System.currentTimeMillis();
    	byte[] buf = new byte[BUFFER_SIZE];
    	int len = 0;
    	try {
	    	while(len >= 0){
	    		try{
	    			if(len > 0){
	    				os.write(buf, 0, len);
	    				os.flush(); //The flush method of OutputStream does nothing.
	    				len = 0;
	    			}
	    			
	    			len = is.read(buf, 0, BUFFER_SIZE);
	    			
	    			if(len > 0)
	    			{
	    				lastReadTime = System.currentTimeMillis();
	    				//SockyJServer.log("lastReadTime: " + lastReadTime);
	    			}
	    			else if (len < 0)
	    			{
	    				this.eof = true;
	    				break;
	    			}
	    		} catch(SocketTimeoutException e) { }
	            if(this.hasTimedOut())
	            	break;
	    	}
    	} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	this.finished = true;
    	synchronized(this.lock)
    	{
    		this.lock.notifyAll();
    	}
	}

	public boolean isEOF() {
		return eof;
	}

	public Object getLock() {
		return lock;
	}
	
	public boolean hasTimedOut() {
		long timeSinceRead = System.currentTimeMillis() - lastReadTime;
        return timeSinceRead >= iddleTimeout;
	}
	
	public boolean hasFinished() {
		return this.finished;
	}
}
