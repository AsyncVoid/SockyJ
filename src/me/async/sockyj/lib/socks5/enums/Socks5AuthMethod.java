package me.async.sockyj.lib.socks5.enums;

import java.util.HashMap;
import java.util.Map;

public enum Socks5AuthMethod {
	NONE(0),
	GSSAPI(1),
	USER_PASS(2),
	NO_ACCEPTABLE(0xFF);
	
	private final int code;
	
	public static final Map<Integer, Socks5AuthMethod> codeMap = new HashMap<Integer, Socks5AuthMethod>();
	static {
        for (Socks5AuthMethod s5am : Socks5AuthMethod.values()) {
        	codeMap.put(s5am.getCode(), s5am);
        }
    }
	
	private Socks5AuthMethod(int code)
	{
		this.code = code;
	}
	
	public int getCode()
	{
		return this.code;
	}
	
	public static Socks5AuthMethod getByCode(int code){
	    /*for(Socks5AuthMethod v : values()){
	        if(v.getCode() == code){
	            return v;
	        }
	    }*/
		/*switch(code)
		{
		case 0:
			return NONE;
		case 1:
			return GSSAPI;
		case 2:
			return USER_PASS;
		default:
			return NO_ACCEPTABLE; //throw new IllegalArgumentException("Invalid Socks5AuthMethod code");
		}*/
		Socks5AuthMethod s5am = codeMap.get(code);
		if(s5am == null)
			return NO_ACCEPTABLE;
		return s5am;
	}
}
