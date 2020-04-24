package me.async.sockyj.lib.socks5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import me.async.sockyj.lib.IStreamReadable;
import me.async.sockyj.lib.IStreamWritable;
import me.async.sockyj.lib.socks5.enums.Socks5AddressType;

public class Socks5Address implements IStreamWritable, IStreamReadable {
	
	public static Socks5Address EMPTY;
	static {
		try {
			EMPTY = new Socks5Address(InetAddress.getByAddress(new byte[4]));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private Socks5AddressType addressType;
	private InetAddress ip;
	private String host;
	
	/**
	 * For deserialization purposes only
	 */
	public Socks5Address()
	{
		
	}
	
	public Socks5Address(InetAddress ip)
	{
		if(ip instanceof Inet4Address)
			this.addressType = Socks5AddressType.IPV4;
		else
			this.addressType = Socks5AddressType.IPV6;
		this.ip = ip;
	}
	
	public Socks5Address(String host)
	{
		this.addressType = Socks5AddressType.DOMAIN;
		this.host = host;
	}

	public Socks5AddressType getAddressType() {
		return addressType;
	}
	
	public String getHost() {
		if(this.addressType == Socks5AddressType.DOMAIN)
			return this.host;
		return this.ip.getHostAddress();
	}
	
	public InetAddress getInetAddress() throws UnknownHostException {
		if(this.addressType != Socks5AddressType.DOMAIN) 
			return this.ip;
		return (this.ip=InetAddress.getByName(this.host));
	}
	
	@Override
	public void ReadFromStream(InputStream stream) throws IOException {
		int typecode = stream.read();
		this.addressType = Socks5AddressType.getByCode(typecode);
		if(this.addressType == Socks5AddressType.DOMAIN)
		{
			int hostlen = stream.read();
			byte[] hostbyt = new byte[hostlen];
			stream.read(hostbyt);
			this.host = new String(hostbyt);
		}
		else if(this.addressType == Socks5AddressType.IPV4)
		{
			byte[] ipv4 = new byte[4];
			stream.read(ipv4);
			this.ip = InetAddress.getByAddress(ipv4);
		}
		else if(this.addressType == Socks5AddressType.IPV6)
		{
			byte[] ipv6 = new byte[16];
			stream.read(ipv6);
			this.ip = InetAddress.getByAddress(ipv6);
		}
		else
		{
			byte[] ipv4 = new byte[4];
			stream.read(ipv4);
			this.ip = InetAddress.getByAddress(ipv4);
			throw new IOException("Unknown AddressType [" + typecode + "]");
		}
	}

	@Override
	public void WriteToStream(OutputStream stream) throws IOException {
		stream.write(this.addressType.getCode());
		if(this.addressType == Socks5AddressType.DOMAIN)
		{
			stream.write(this.host.length());
			stream.write(this.host.getBytes());
		}
		else
		{
			stream.write(this.ip.getAddress());
		}
	}
	
	@Override
	public String toString()
	{
		return this.getHost();
	}
}