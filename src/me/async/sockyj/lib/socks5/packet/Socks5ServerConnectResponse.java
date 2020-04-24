package me.async.sockyj.lib.socks5.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.async.sockyj.lib.packet.Packet;
import me.async.sockyj.lib.socks5.Socks5Address;
import me.async.sockyj.lib.socks5.enums.Socks5ConnectStatus;

public class Socks5ServerConnectResponse extends Packet {

	private Socks5ConnectStatus status;
	private Socks5Address address;
	private int port;
	
	
	public Socks5ServerConnectResponse(Socks5ConnectStatus status, Socks5Address address, int port)
	{
		this.status = status;
		this.address = address;
		this.port = port;
	}
	
	public Socks5ConnectStatus getStatus() {
		return this.status;
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
		stream.write(this.status.getCode());
		stream.write(0); //reserved
		this.address.WriteToStream(stream);
		stream.write(port >> 8);
		stream.write(port);
		
	}

	@Override
	public void ReadFromStream(InputStream stream) throws IOException {
		stream.read();
		int statusCode = stream.read();
		this.status = Socks5ConnectStatus.getByCode(statusCode);
		stream.read();
		this.address = new Socks5Address();
		this.address.ReadFromStream(stream);
		this.port = (stream.read() & 0xFF) << 8;
		this.port += stream.read() & 0xFF;
	}
	
}
