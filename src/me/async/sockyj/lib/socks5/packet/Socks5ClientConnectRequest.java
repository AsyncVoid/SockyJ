package me.async.sockyj.lib.socks5.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.async.sockyj.lib.packet.Packet;
import me.async.sockyj.lib.socks5.Socks5Address;
import me.async.sockyj.lib.socks5.enums.Socks5ConnectCommand;

public class Socks5ClientConnectRequest extends Packet {

	private Socks5ConnectCommand command;
	private Socks5Address address;
	private int port;
	
	public Socks5ClientConnectRequest(Socks5ConnectCommand command, Socks5Address address, int port)
	{
		this.command = command;
		this.address = address;
		this.port = port;
	}
	
	/**
	 * FOR DESERIALIZATION USE ONLY
	 */
	public Socks5ClientConnectRequest() {
		
	}

	public Socks5ConnectCommand getCommand()
	{
		return this.command;
	}
	
	public Socks5Address getAddress() {
		return this.address;
	}

	public int getPort() {
		return this.port;
	}
	
	@Override
	public void WriteToStream(OutputStream stream) throws IOException {
		stream.write(5); //socks version
		stream.write(this.command.getCode());
		stream.write(0); //reserved
		this.address.WriteToStream(stream);
		stream.write(port >> 8);
		stream.write(port);
	}

	@Override
	public void ReadFromStream(InputStream stream) throws IOException {
		stream.read();
		int commandCode = stream.read();
		this.command = Socks5ConnectCommand.getByCode(commandCode);
		stream.read(); //reserved
		this.address = new Socks5Address();
		this.address.ReadFromStream(stream);
		this.port = (stream.read() & 0xFF) << 8;
		this.port += stream.read() & 0xFF;
	}
	
	@Override
	public String toString()
	{
		return "Socks5ClientConnectRequest { Command: " + this.command.name() + ", Address: " + address + ", Port: " + this.port + "}";
	}
}
