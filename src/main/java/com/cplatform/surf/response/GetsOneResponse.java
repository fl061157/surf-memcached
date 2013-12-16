package com.cplatform.surf.response;

import com.cplatform.surf.MemcachedException;
import com.cplatform.surf.MemcachedProtocolException;

public class GetsOneResponse extends TakeResponse {

	private String key ;
	private int length ;
	private int flag ;
	private int cas ;
	private boolean empty = true ;
	private Object object ;
	
	public GetsOneResponse() {
		super() ;
	}
	
	
	@Override
	public int getLength() {  
		return length ;
	}

	public int getCas() {
		return cas;
	}
	
	@Override
	public void setValues(byte[] values) {
	}

	@Override
	public void setObject(Object object) {
		this.object = object ;
	}

	@Override
	public int getFlag() {
		return flag ;
	}
	
	
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	
	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public String getKey() {
		return key;
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	public Object getObject() {
		return object;
	}
	
	public boolean isError() {
		return error;
	}
	
	public String getClientErrorMsg() {
		return clientErrorMsg;
	}
	
	public boolean isClientError() {
		return clientError;
	}
	
	
	public void setCas(int cas) {
		this.cas = cas;
	}
	
	
	public static GetsOneResponse buildResponse(String line) throws MemcachedProtocolException { //TODO 异常处理
		GetsOneResponse r = new GetsOneResponse() ;
		try {
			String[] bgss = line.split(" ") ; 
			r.setKey( bgss[1] ) ; 
			r.setFlag( Integer.parseInt( bgss[2] ) ) ; 
			r.setLength(Integer.parseInt( bgss[3] )) ;
			r.setCas( Integer.parseInt( bgss[4] ) ) ;  
			r.empty = false ;
		} catch (Exception e) {
			throw new MemcachedProtocolException( "MemcachedPortocolException Parse Error !" , e ) ; 
		}  
		return r  ;
	}
	
	
	public static GetsOneResponse buildEmptyResponse()  {
		GetsOneResponse r = new GetsOneResponse() ;
		r.empty = true ; 
		return r;
	}
	
	public static GetsOneResponse buildErrorResponse() {
		GetsOneResponse r = new GetsOneResponse() ;
		r.error = true ; 
		return r;
	}
	
	public static GetsOneResponse buildClientErrorResponse(String msg) {
		GetsOneResponse r = new GetsOneResponse() ;
		r.clientError = true ;
		r.clientErrorMsg = msg ; 
		return r ;
	}
	
	@Override
	public GetsOneResponse checkException() throws MemcachedException {
		if( ! this.isEmpty() ) {
			return this ;
		}
		if( this.error ) {
			throw new MemcachedException("Get Data Error Exception !");
		}
		if( this.clientError  ) {
			throw new MemcachedException( clientErrorMsg );
		}
		throw new MemcachedException(" Unknwon GetsOne Error ! ");
	}
	


}
