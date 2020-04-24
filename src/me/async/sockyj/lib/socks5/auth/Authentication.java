package me.async.sockyj.lib.socks5.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.async.sockyj.lib.socks5.enums.Socks5AuthMethod;

public abstract class Authentication {
	
	private final Socks5AuthMethod method;
	
	public Authentication(Socks5AuthMethod method)
	{
		this.method = method;
	}
	
	public abstract boolean Authorize(InputStream is, OutputStream os) throws IOException;

	public Socks5AuthMethod getMethod() {
		return method;
	}
}