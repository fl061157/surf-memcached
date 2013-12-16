package com.cplatform.surf;

/**
 * 
 * @author fangliang
 *
 */
public class Address {
	
	private final String host ;
	private final int port ;
	public Address( String host , int port ) {
		this.host = host ;
		this.port = port ;
	}
	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj == null ) {
			return false ;
		}
		if( obj == this ) {
			return true ;
		}
		if( !( obj instanceof Address ) ) {
			return false ;
		}
		return ((Address)obj).getHost().equals( this.getHost() ) 
				&&  ( ((Address)obj).getPort() == this.port ) ; 
	}
	
	@Override
	public int hashCode() {
		 return 31 * host.hashCode() + port ;  
	}
	
	
	@Override
	public String toString() {
		return String.format("%s-%s:%d", "com.cplatform.surf.Address" , this.host , this.port );  
	}
	
	
}
