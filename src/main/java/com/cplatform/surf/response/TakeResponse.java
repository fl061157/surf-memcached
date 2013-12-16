package com.cplatform.surf.response;


/**
 * 
 * @author fangliang
 *
 */
public abstract class TakeResponse extends Response {

	public abstract int getLength() ;
	
	public abstract void setValues( byte[] values ) ; 
	
	public abstract void setObject( Object object ) ; 
	
	public abstract int getFlag() ;  
	
}
