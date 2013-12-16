package com.cplatform.surf.response;

import com.cplatform.surf.MemcachedException;
import com.cplatform.surf.MemcachedProtocolException;

public class GetOneResponse extends TakeResponse {

	private String key ;
	private int length ;
	private int flag ;
	private Object object ;
	protected boolean empty ;
	
	public GetOneResponse() {
		super() ;
		empty = true ;
	}
	
	
	@Override
	public int getLength() {  
		return length ;
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
	
	
	public static GetOneResponse buildResponse(String line) throws MemcachedProtocolException { //TODO 异常处理
		GetOneResponse r = new GetOneResponse() ; 
		try {
			String[] bgss = line.split(" ") ; 
			r.setKey( bgss[1] ) ; 
			r.setFlag( Integer.parseInt( bgss[2] ) ) ; 
			r.setLength(Integer.parseInt( bgss[3] )) ;
			r.empty = false ;
		} catch (Exception e) {
			throw new MemcachedProtocolException( "MemcachedPortocolException Parse Error !" , e ) ; 
		}  
		return r  ;
	}
	
	
	public static GetOneResponse buildEmptyResponse()  {
		GetOneResponse r = new GetOneResponse() ;
		r.empty = true ; 
		return r;
	}
	
	public static GetOneResponse buildErrorResponse() {
		GetOneResponse r = new GetOneResponse() ;
		r.error = true ; 
		return r;
	}
	
	public static GetOneResponse buildClientErrorResponse(String msg) {
		GetOneResponse r = new GetOneResponse() ;
		r.clientError = true ;
		r.clientErrorMsg = msg ; 
		return r ;
	}

	
	@Override
	public GetOneResponse checkException()  throws MemcachedException {  
		if( ! this.empty ) {
			return this ;
		}
		if( this.error ) {
			throw new MemcachedException("Get Data Error !") ;
		}
		if( this.clientError ) {
			throw new MemcachedException( this.clientErrorMsg );
		}
		throw new MemcachedException("Getdata Unknown Exception !");
	}
	

}
