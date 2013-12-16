package com.cplatform.surf.response;

import com.cplatform.surf.MemcachedException;

public class StoreResponse extends Response {
	
	private boolean ok = false ;
	private boolean notStored = false ;
	private boolean casModified = false ;
	private boolean casNotExists = false ;
	
	
	public StoreResponse( ) {
		super() ;
	}
	
	public static StoreResponse buildStoredResponse() {
		StoreResponse r = new StoreResponse(  ) ;
		r.ok = true ;
		return r ;
	}
	
	public static StoreResponse buildNotStoredResponse() {
		StoreResponse r = new StoreResponse() ;
		r.ok = false ;
		r.notStored = true ;
		return r ;
	}
	
	public static StoreResponse buildErrorResponse() {
		StoreResponse r = new StoreResponse(  ) ;
		r.ok = false ;
		r.error = true ;
		return r;
	}
	
	public static StoreResponse buildClientErrorResponse(String clientErrorMsg ) {
		StoreResponse r = new StoreResponse() ;
		r.ok = false ;
		r.clientError = true ;
		r.clientErrorMsg = clientErrorMsg ;
		return r ;
 	}
	
	public static StoreResponse buildCasModifiedResponse() {
		StoreResponse r = new StoreResponse(  ) ;
		r.ok = false ;
		r.casModified = true ;
		return r;
	}
	
	
	public static StoreResponse buildCasNotFoundResponse() {
		StoreResponse r = new StoreResponse(  ) ; 
		r.ok = false ;
		r.casNotExists = true ;
		return r;
	}
	
	
	public boolean isOk() {
		return ok;
	}
	
	public void setOk(boolean ok) {
		this.ok = ok;
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
	
	public String getClientErrorMsg() {
		return clientErrorMsg;
	}
	
	public boolean isCasModified() {
		return casModified;
	}
	
	public boolean isCasNotExists() {
		return casNotExists;
	}
	
	@Override
	public StoreResponse checkException() throws MemcachedException {
		if( this.ok ) {
			return this ;
		}
		if( this.error ) {
			throw new MemcachedException("Set Error Exception !");
		}
		if( this.clientError ) {
			throw new MemcachedException( this.clientErrorMsg ); 
		}
		if( this.notStored ) {
			throw new MemcachedException(" Not Stored Exception ! ");
		}
		throw new MemcachedException("Set Unknwon Exception !");
	}
	
	
}
