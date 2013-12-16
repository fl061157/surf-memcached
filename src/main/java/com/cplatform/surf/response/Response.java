package com.cplatform.surf.response;

import com.cplatform.surf.MemcachedException;

/**
 * 
 * @author fangliang
 *
 */
public abstract class Response {
	
	protected boolean ok ;
	protected boolean error ;
	protected boolean clientError ;
	protected String clientErrorMsg ;
	protected boolean serverError ;
	protected String serverErrorMsg ;
	protected boolean notFound ;
	
	public Response() {
		this.error = false ;
		this.clientError = false ;
		this.serverError = false ;
		this.notFound = false ;
	}
	
	public abstract Response  checkException() throws MemcachedException ;
	
	public void ok() {
		this.ok = true ;
		this.error = false ;
		this.clientError = false ;
		this.clientErrorMsg = null ;
		this.serverError = false ;
		this.serverErrorMsg = null ;
		this.notFound = false ;
	}
	
	public void error() {
		this.ok = false ;
		this.error = true ; 
		this.clientError = false ;
		this.clientErrorMsg = null ;
		this.serverError = false ;
		this.serverErrorMsg = null ;
		this.notFound = false ;
	}
	
	public void clientError(String clientErrorMsg) {
		this.ok = false ;
		this.error = false ;
		this.clientError = true ;
		this.clientErrorMsg = clientErrorMsg ;
		this.serverError = false ;
		this.serverErrorMsg = null ;
	}
	
	
	public void notFound() {
		this.ok = false ;
		this.notFound = true ;
		this.clientError = false ;
		this.clientErrorMsg = null ;
		this.serverError = false ;
		this.serverErrorMsg = null ;
	}
	
	
}
