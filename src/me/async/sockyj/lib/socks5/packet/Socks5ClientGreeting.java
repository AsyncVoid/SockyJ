package me.async.sockyj.lib.socks5.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.async.sockyj.lib.packet.Packet;
import me.async.sockyj.lib.socks5.enums.Socks5AuthMethod;

public class Socks5ClientGreeting extends Packet {

	private Socks5AuthMethod[] authMethods;
	
	/**
	 * FOR DESERIALIZATION USE ONLY
	 */
	public Socks5ClientGreeting()
	{
		
	}
	
	public Socks5ClientGreeting(Socks5AuthMethod authMethod)
	{
		this(new Socks5AuthMethod[] { authMethod });
	}
	
	public Socks5ClientGreeting(Socks5AuthMethod[] authMethods)
	{
		this.authMethods = authMethods;
	}
	
	public Socks5AuthMethod[] getAuthMethods()
	{
		return this.authMethods;
	}
	
	@Override
	public void WriteToStream(OutputStream stream) throws IOException {
		stream.write(5);
		stream.write(authMethods.length);
		for(int i = 0; i < authMethods.length; i++)
		{
			stream.write(authMethods[i].getCode());
		}
	}

	@Override
	public void ReadFromStream(InputStream stream) throws IOException {
		stream.read(); //version
		int methodlen = stream.read();
		this.authMethods = new Socks5AuthMethod[methodlen];
		for(int i = 0; i < methodlen; i++)
		{
			this.authMethods[i] = Socks5AuthMethod.getByCode(stream.read());
		}
	}
}
