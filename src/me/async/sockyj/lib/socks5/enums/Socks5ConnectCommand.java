package me.async.sockyj.lib.socks5.enums;

import java.util.HashMap;
import java.util.Map;

public enum Socks5ConnectCommand {
	TCP_STREAM(1),
	TCP_BIND(2),
	UDP_ASSOCIATE(3),
	UNKNOWN(0xFF);
	
	private final int code;
	public static final Map<Integer, Socks5ConnectCommand> codeMap = new HashMap<Integer, Socks5ConnectCommand>();
	static {
        for (Socks5ConnectCommand s5cc : Socks5ConnectCommand.values()) {
        	codeMap.put(s5cc.getCode(), s5cc);
        }
    }
	
	private Socks5ConnectCommand(int code)
	{
		this.code = code;
	}
	
	public int getCode()
	{
		return this.code;
	}
	
	public static Socks5ConnectCommand getByCode(int code){
		Socks5ConnectCommand s5cc = codeMap.get(code);
		if(s5cc == null)
			return UNKNOWN;
		return s5cc;
	}
}
