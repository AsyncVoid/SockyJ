package me.async.sockyj.lib.socks5.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.async.sockyj.lib.packet.Packet;

public class Socks5ServerUserPassAuthResponse extends Packet {

	private boolean success;
	
	public Socks5ServerUserPassAuthResponse(boolean success)
	{
		this.success = success;
	}
	
	/**
	 * FOR DESERIALIZATION PURPOSES ONLY
	 */
	public Socks5ServerUserPassAuthResponse() {
		
	}

	public boolean isSuccessful()
	{
		return this.success;
	}
	
	@Override
	public void WriteToStream(OutputStream stream) throws IOException {
		stream.write(1); //user/pass version
		stream.write(this.success == true ? 0 : 0xFF);
	}

	@Override
	public void ReadFromStream(InputStream stream) throws IOException {
		stream.read();
		int code = stream.read();
		this.success = code == 0;
	}
}