package me.async.sockyj.server;

import java.io.IOException;
import java.net.Socket;

import me.async.sockyj.lib.socks5.enums.Socks5AuthMethod;

public class Client {
	
	private ClientStage stage = ClientStage.INIT;
	private Socket socket;
	private Socket targetSocket;
	private Socks5AuthMethod[] authMethods;
	private Socks5AuthMethod chosenAuthMethod;
	
	public Client(Socket socket)
	{
		this.socket = socket;
	}

	public ClientStage getStage() {
		return this.stage;
	}

	public void setStage(ClientStage stage) {
		this.stage = stage;
	}

	public Socket getSocket() {
		return this.socket;
	}
	
	@Override
	public String toString()
	{
		return this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort();
	}

	public Socks5AuthMethod[] getAuthMethods() {
		return authMethods;
	}

	public void setAuthMethods(Socks5AuthMethod[] authMethods) {
		this.authMethods = authMethods;
	}
	
	public Socks5AuthMethod getChosenAuthMethod() {
		return this.chosenAuthMethod;
	}

	public void setChosenAuthMethod(Socks5AuthMethod chosenAuthMethod) {
		this.chosenAuthMethod = chosenAuthMethod;
	}

	public Socket getTargetSocket() {
		return this.targetSocket;
	}

	public void setTargetSocket(Socket remoteSocket) {
		this.targetSocket = remoteSocket;
	}
	
	public void kill()
	{
		SockyJServer.log("Killing client " + this.toString());
		if(this.socket != null)
			try {
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if(this.targetSocket != null)
			try {
				this.targetSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		this.stage = ClientStage.DEAD;
	}
}
