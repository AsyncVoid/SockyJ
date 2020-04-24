package me.async.sockyj.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import me.async.sockyj.lib.socks5.Socks5Address;
import me.async.sockyj.lib.socks5.auth.Authentication;
import me.async.sockyj.lib.socks5.auth.AuthenticationManager;
import me.async.sockyj.lib.socks5.enums.Socks5AddressType;
import me.async.sockyj.lib.socks5.enums.Socks5AuthMethod;
import me.async.sockyj.lib.socks5.enums.Socks5ConnectStatus;
import me.async.sockyj.lib.socks5.packet.Socks5ClientConnectRequest;
import me.async.sockyj.lib.socks5.packet.Socks5ClientGreeting;
import me.async.sockyj.lib.socks5.packet.Socks5ServerConnectResponse;

public class ClientHandler implements Runnable{

	private final Client client;
	private final AuthenticationManager authManager;
	private static final int SO_TIMEOUT = 10000;
	
	public ClientHandler(Client client, AuthenticationManager authManager)
	{
		this.client = client;
		this.authManager = authManager;
	}
	
	@Override
	public void run() {
		while(true)
		{
			switch(client.getStage())
			{
			case INIT:
				doGreet();
				break;
			case AUTH:
				doAuth();
				break;
			case CONNECT:
				doConnect();
				break;
			case PIPE:
				doPipe();
				break;
			default:
				return;
			
			}
		}
	}
	
	private void doGreet()
	{
		try {
			InputStream is = client.getSocket().getInputStream();
			client.getSocket().setSoLinger(true, 1); //Once client's connection is killed don't wait around with a socket in memory, do an abortive close, linger for 1s
			
			Socks5ClientGreeting s5cg = new Socks5ClientGreeting();
			s5cg.ReadFromStream(is);
			
			StringBuilder auths = new StringBuilder("Client " + client + " supports auths: [");
			for(Socks5AuthMethod s5am : s5cg.getAuthMethods())
			{
				auths.append(s5am.name() + ",");
			}
			auths.deleteCharAt(auths.length()-1);
			auths.append(']');
			SockyJServer.log(auths.toString());
			
			client.setAuthMethods(s5cg.getAuthMethods());
			
			client.setStage(ClientStage.AUTH);
			
		} catch (IOException ex)
		{
			SockyJServer.log("Greeting failed for client " + client + ": " + ex);
			client.kill();
		}
	}
	
	private void doAuth()
	{
		try {
			OutputStream os = client.getSocket().getOutputStream();
			InputStream is = client.getSocket().getInputStream();
			
			Authentication auth = this.authManager.getChosenAuth(client.getAuthMethods());
			SockyJServer.log("Client " + client + " authenticating with " + auth.getMethod().name());
			client.setChosenAuthMethod(auth.getMethod());
			boolean authed = auth.Authorize(is, os);
			
			if(!authed)
			{
				SockyJServer.log("Client " + client + " failed to auth.");
				client.kill();
				return;
			}
			
			SockyJServer.log("Client " + client + " successfully authed.");
			client.setStage(ClientStage.CONNECT);
		} catch (Exception ex) {
			SockyJServer.log("Auth failed for client " + client + ": " + ex);
			client.kill();
		}
	}
	
	private void doConnect()
	{
		try {
			OutputStream os = client.getSocket().getOutputStream();
			InputStream is = client.getSocket().getInputStream();
			
			Socks5ClientConnectRequest s5ccr = new Socks5ClientConnectRequest();
			try {
				s5ccr.ReadFromStream(is);
			} catch (Exception ex)
			{
				client.kill();
				ex.printStackTrace();
				SockyJServer.log(s5ccr.toString());
				return;
			}
			
			switch(s5ccr.getCommand())
			{
			case TCP_STREAM:
				Socket target = null;
				SockyJServer.log("Client " + client + " wants to connect to " + s5ccr.getAddress() + ":" + s5ccr.getPort());
				if(s5ccr.getAddress().getAddressType() == Socks5AddressType.DOMAIN)
					target = new Socket(s5ccr.getAddress().getHost(), s5ccr.getPort());
				else
					target = new Socket(s5ccr.getAddress().getInetAddress(), s5ccr.getPort());
				
				target.setSoTimeout(SO_TIMEOUT);
				client.setTargetSocket(target);
				
				if(target.isConnected() && !target.isClosed())
				{
					SockyJServer.log("Client " + client + " connected to " + target.getInetAddress().getHostAddress() + ":" + target.getPort() +", piping...");
					Socks5ServerConnectResponse s5scr_success = new Socks5ServerConnectResponse(Socks5ConnectStatus.SUCCEED, Socks5Address.EMPTY, 0);
					s5scr_success.WriteToStream(os);
					client.setStage(ClientStage.PIPE);
				}
				else
				{
					SockyJServer.log("Couldn't connect to target " + target.getInetAddress().getHostAddress() + ":" + target.getPort());
					Socks5ServerConnectResponse s5scr_fail = new Socks5ServerConnectResponse(Socks5ConnectStatus.HOST_UNREACHABLE, Socks5Address.EMPTY, 0);
					s5scr_fail.WriteToStream(os);
					client.kill();
				}
				break;
			case TCP_BIND:
				SockyJServer.log("Client " + client + " wants to bind to " + s5ccr.getAddress() + ":" + s5ccr.getPort());
				client.setStage(ClientStage.BIND);
				break;
			case UDP_ASSOCIATE:
				SockyJServer.log("Client " + client + " wants to associate to " + s5ccr.getAddress() + ":" + s5ccr.getPort());
				client.setStage(ClientStage.ASSOCIATE);
				break;
			default:
				SockyJServer.log("Client " + client + " sent an unsupported command.");
				Socks5ServerConnectResponse s5scr_fail = new Socks5ServerConnectResponse(Socks5ConnectStatus.COMMAND_NOT_SUPPORTED, Socks5Address.EMPTY, 0);
				s5scr_fail.WriteToStream(os);
				client.kill();
				break;
			}
		} catch (IOException ex)
		{
			SockyJServer.log("Connect failed for client " + client + ": " + ex);
			client.kill();
			ex.printStackTrace();
		}
	}
	
	public void doPipe()
	{
		try {
			client.getSocket().setSoTimeout(SO_TIMEOUT);
			client.getTargetSocket().setSoTimeout(SO_TIMEOUT);
			client.getTargetSocket().setSoLinger(true, 1);
			
			Object lock = new Object();
			
			PipeRunnable clientToTargetPipe = new PipeRunnable(client.getSocket().getInputStream(), client.getTargetSocket().getOutputStream(), lock);
			Thread clientToTargetThread = new Thread(clientToTargetPipe);
			PipeRunnable targetToClientPipe = new PipeRunnable(client.getTargetSocket().getInputStream(), client.getSocket().getOutputStream(), lock);
			Thread targetToClientThread = new Thread(targetToClientPipe);
			clientToTargetThread.start();
			targetToClientThread.start();
			
			synchronized(lock)
			{
				while(!(clientToTargetPipe.hasFinished() && targetToClientPipe.hasFinished()))
				{
					lock.wait();
				}
				client.kill();
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
