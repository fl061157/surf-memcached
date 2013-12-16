package com.cplatform.surf.response;

import com.cplatform.surf.MemcachedException;
import com.cplatform.surf.MemcachedProtocolException;


/**
 * 
 * 
 * 
 * <key> <flags> <bytes> \r\n
 * @author fangliang
 *
 */
public class GetResponse extends Response {
	
	private String key ;
	private byte[] value ;
	private int length ;
	private boolean empty = true ;
	private int flag ;
	private Object object ;
	
	
	public GetResponse() {
		super() ;
	}
	
	public static GetResponse build( String bgs ) throws MemcachedProtocolException {
		GetResponse r = new GetResponse() ;
		try {
			String[] bgss = bgs.split(" ") ; 
			r.setKey( bgss[1] ) ; 
			r.setFlag( Integer.parseInt( bgss[2] ) ) ; 
			r.setLength(Integer.parseInt( bgss[3] )) ;
			r.empty = false ;
		} catch (Exception e) {
			throw new MemcachedProtocolException( "MemcachedPortocolException Parse Error !" , e ) ; 
		}  
		return r  ;
	}
	
	public static GetResponse buildEmptyGetResponse() {
		GetResponse r = new GetResponse() ;
		r.empty = true ;
		return r;
	}
	
	public static GetResponse buildErrorResponse() {
		GetResponse r = new GetResponse() ;
		r.error = true ;
		return r;
	}
	
	public static GetResponse buildClientErrorResponse(String clientErrorMsg) {
		GetResponse r = new GetResponse() ;
		r.clientError = true ;
		r.clientErrorMsg = clientErrorMsg ;
		return r ;
	}
	
	public String getKey() {
		return key;
	}
	
	public byte[] getValue() {
		return value;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setValue(byte[] value) {
		this.value = value;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	public boolean isClientError() {
		return clientError;
	}
	
	public boolean isError() {
		return error;
	}
	
	public String getClientErrorMsg() {
		return clientErrorMsg;
	}
	
	public int getFlag() {
		return flag;
	}
	
	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	public Object getObject() {
		return object;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}
	
	//TODO
	@Override
	public Response checkException() throws MemcachedException {
		return this;
	}
	
	
}
