package me.async.sockyj.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import me.async.sockyj.lib.socks5.auth.AuthenticationManager;
import me.async.sockyj.lib.socks5.auth.AuthenticationNone;
import me.async.sockyj.lib.socks5.auth.AuthenticationUserPass;

public class SockyJServer {

	public static void main(String[] args) throws IOException
	{
		AuthenticationManager am = new AuthenticationManager();
		am.AddAuth(new AuthenticationUserPass("admin", "password")); //Allow authentication with a username and password
		am.AddAuth(new AuthenticationNone());                        //Also allow non authenticated clients
		Start(8080, 10, InetAddress.getLocalHost(), am);             //Start a SOCKS5 proxy on port 8080
	}
	
	public static void log(String s)
	{
		System.out.println(s);
	}
	
	public static void Start(int port, int backlog, InetAddress localIP, AuthenticationManager am) throws IOException {
		ServerSocket serverSocket = null;
    	try{
    		serverSocket = new ServerSocket(port,backlog,localIP);
    		log("Starting SOCKS Proxy on:"+serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
	        while(true) {
	        	Socket socket = serverSocket.accept();
	        	log("Accepted from: " + socket.getInetAddress().getHostName() + ":" + socket.getPort());
	        	Client client = new Client(socket);
	        	
	        	ClientHandler nm = new ClientHandler(client, am);
	        	(new Thread(nm)).start();
	        }
    	} catch(IOException ioe){
    		ioe.printStackTrace();
        }finally{
        	serverSocket.close();
        }
	}
}
