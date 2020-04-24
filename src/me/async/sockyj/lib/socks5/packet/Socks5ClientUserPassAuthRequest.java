package me.async.sockyj.lib.socks5.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.async.sockyj.lib.packet.Packet;

public class Socks5ClientUserPassAuthRequest extends Packet {

	private String username;
	private String password;
	
	public Socks5ClientUserPassAuthRequest(String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
	/**
	 * FOR DESERIALIZATION USE ONLY
	 */
	public Socks5ClientUserPassAuthRequest() {
		
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	@Override
	public void WriteToStream(OutputStream stream) throws IOException {
		stream.write(1); //user/pass auth version
		stream.write(username.length());
		stream.write(username.getBytes());
		stream.write(password.length());
		stream.write(password.getBytes());
	}

	@Override
	public void ReadFromStream(InputStream stream) throws IOException {
		stream.read();
		int userlen = stream.read();
		byte[] userbyt = new byte[userlen];
		stream.read(userbyt);
		this.username = new String(userbyt);
		int passlen = stream.read();
		byte[] passbyt = new byte[passlen];
		stream.read(passbyt);
		this.password = new String(passbyt);
	}
}
