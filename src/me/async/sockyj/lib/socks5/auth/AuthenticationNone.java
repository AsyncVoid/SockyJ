package me.async.sockyj.lib.socks5.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.async.sockyj.lib.socks5.enums.Socks5AuthMethod;
import me.async.sockyj.lib.socks5.packet.Socks5ServerAuthChoice;

public class AuthenticationNone extends Authentication {

	public AuthenticationNone() {
		super(Socks5AuthMethod.NONE);
	}

	@Override
	public boolean Authorize(InputStream is, OutputStream os) throws IOException {
		Socks5ServerAuthChoice s5sac = new Socks5ServerAuthChoice(Socks5AuthMethod.NONE);
		s5sac.WriteToStream(os);
		return true;
	}
}
