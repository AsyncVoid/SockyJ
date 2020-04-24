package me.async.sockyj.lib.socks5.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.async.sockyj.lib.socks5.enums.Socks5AuthMethod;
import me.async.sockyj.lib.socks5.packet.Socks5ClientUserPassAuthRequest;
import me.async.sockyj.lib.socks5.packet.Socks5ServerAuthChoice;
import me.async.sockyj.lib.socks5.packet.Socks5ServerUserPassAuthResponse;

public class AuthenticationUserPass extends Authentication {

	private final String username;
	private final String password;
	
	public AuthenticationUserPass(String username, String password) {
		super(Socks5AuthMethod.USER_PASS);
		this.username = username;
		this.password = password;
	}

	@Override
	public boolean Authorize(InputStream is, OutputStream os) throws IOException {
		Socks5ServerAuthChoice s5sac = new Socks5ServerAuthChoice(Socks5AuthMethod.USER_PASS);
		s5sac.WriteToStream(os);
		
		Socks5ClientUserPassAuthRequest s5cupaur = new Socks5ClientUserPassAuthRequest();
		s5cupaur.ReadFromStream(is);
		
		if(s5cupaur.getUsername().equals(this.username) && s5cupaur.getPassword().equals(this.password))
		{
			Socks5ServerUserPassAuthResponse s5aupar = new Socks5ServerUserPassAuthResponse(true);
			s5aupar.WriteToStream(os);
			return true;
		}
		Socks5ServerUserPassAuthResponse s5aupar = new Socks5ServerUserPassAuthResponse(false);
		s5aupar.WriteToStream(os);
		return false;
	}

}
