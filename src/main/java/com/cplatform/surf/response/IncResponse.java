package com.cplatform.surf.response;

import com.cplatform.surf.MemcachedException;

public class IncResponse extends Response {
	
	private long value ;
	
	
	public IncResponse() {
		super() ;
	}
	
	public static IncResponse buildIncResponse( long value ) {
		IncResponse incResponse = new IncResponse() ;
		incResponse.value = value ;
		incResponse.ok = true ;
		incResponse.notFound = false ;
		return incResponse ;
	}
	
	public static IncResponse buildNotFoundResponse() {
		IncResponse incResponse = new IncResponse() ;
		incResponse.notFound = true ;
		return incResponse ;
	}
	
	public static IncResponse buildErrorResponse() {
		IncResponse incResponse = new IncResponse() ;
		incResponse.error = true ;
		return incResponse ;
	}
	
	public static IncResponse buildClientErrorResponse( String msg ) {
		IncResponse incResponse = new IncResponse() ;
		incResponse.clientError = true ;
		incResponse.clientErrorMsg = msg ; 
		return incResponse ;
	}
	
	
	public long getValue() {
		return value;
	}
	
	public boolean isNotFound() {
		return notFound;
	}
	
	public boolean isClientError() {
		return clientError;
	}
	
	public boolean isError() {
		return error;
	}
	
	public boolean isServerError() {
		return serverError;
	}
	
	
	@Override
	public IncResponse checkException() throws MemcachedException {
		if( this.ok ) {
			return this ;
		}
		if( this.error ) {
			throw new MemcachedException(" IncOrDec Error ! ");
		}
		if( this.clientError ) {
			throw new MemcachedException( this.clientErrorMsg ) ;
		}
		if( this.notFound ) {
			return this ;
		}
		throw new MemcachedException(" IncOrDecr Unknwon Exception ! ");
	}
	
	
	
}
