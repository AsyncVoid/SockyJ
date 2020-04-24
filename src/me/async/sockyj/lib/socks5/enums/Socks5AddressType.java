package me.async.sockyj.lib.socks5.enums;

import java.util.HashMap;
import java.util.Map;

public enum Socks5AddressType {
	IPV4(1),
	DOMAIN(3),
	IPV6(4),
	UNKNOWN(0xFF);
	
	private final int code;
	public static final Map<Integer, Socks5AddressType> codeMap = new HashMap<Integer, Socks5AddressType>();
	static {
        for (Socks5AddressType s5at : Socks5AddressType.values()) {
        	codeMap.put(s5at.getCode(), s5at);
        }
    }
	
	private Socks5AddressType(int code)
	{
		this.code = code;
	}
	
	public int getCode()
	{
		return this.code;
	}
	
	public static Socks5AddressType getByCode(int code){
		Socks5AddressType s5at = codeMap.get(code);
		if(s5at == null)
			return UNKNOWN;
		return s5at;
	}
}
