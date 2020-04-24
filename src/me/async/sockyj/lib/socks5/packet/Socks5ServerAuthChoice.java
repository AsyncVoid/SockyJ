package me.async.sockyj.lib.socks5.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.async.sockyj.lib.packet.Packet;
import me.async.sockyj.lib.socks5.enums.Socks5AuthMethod;

public class Socks5ServerAuthChoice extends Packet {

	private Socks5AuthMethod authMethod;
	
	public Socks5ServerAuthChoice(Socks5AuthMethod authMethod)
	{
		this.authMethod = authMethod;
	}
	
	public Socks5AuthMethod getAuthMethod()
	{
		return this.authMethod;
	}
	
	@Override
	public void WriteToStream(OutputStream stream) throws IOException {
		stream.write(5); //version
		stream.write(this.authMethod.getCode());
	}

	@Override
	public void ReadFromStream(InputStream stream) throws IOException {
		stream.read(); //version
		int code = stream.read();
		this.authMethod = Socks5AuthMethod.getByCode(code);
	}
}
