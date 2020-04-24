package me.async.sockyj.lib.socks5.auth;

import java.util.LinkedList;
import java.util.List;

import me.async.sockyj.lib.socks5.enums.Socks5AuthMethod;

public class AuthenticationManager {
	private final List<Authentication> methods = new LinkedList<Authentication>();
	
	public AuthenticationManager()
	{
		
	}
	
	public void AddAuth(Authentication auth)
	{
		methods.add(auth);
	}
	
	public Authentication getChosenAuth(Socks5AuthMethod[] available) throws Exception
	{
		for(Authentication auth : methods)
		{
			for(Socks5AuthMethod s5am : available)
			{
				if(auth.getMethod().equals(s5am))
				{
					return auth;
				}
			}
		}
		throw new Exception("No auth methods available");
	}
	
}
