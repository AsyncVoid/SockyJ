package me.async.sockyj.lib.socks5.enums;

import java.util.HashMap;
import java.util.Map;

public enum Socks5ConnectStatus {
	SUCCEED(0),
	GENERAL_FAILURE(1), //general failure
	NOT_ALLOWED(2),
	NETWORK_UNREACHABLE(3),
	HOST_UNREACHABLE(4),
	CONNECTION_REFUSED(5),
	TTL_EXPIRED(6),
	COMMAND_NOT_SUPPORTED(7),
	ADDRESS_NOT_SUPPORTED(8),
	UNKNOWN(0xFF);
	
	private final int code;
	public static final Map<Integer, Socks5ConnectStatus> codeMap = new HashMap<Integer, Socks5ConnectStatus>();
	static {
        for (Socks5ConnectStatus s5cs : Socks5ConnectStatus.values()) {
        	codeMap.put(s5cs.getCode(), s5cs);
        }
    }
	
	private Socks5ConnectStatus(int code)
	{
		this.code = code;
	}
	
	public int getCode()
	{
		return this.code;
	}
	
	public static Socks5ConnectStatus getByCode(int code){
		Socks5ConnectStatus s5cs = codeMap.get(code);
		if(s5cs == null)
			return UNKNOWN;
		return s5cs;
	}
}
