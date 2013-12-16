package com.cplatform.surf.response;

import com.cplatform.surf.MemcachedException;

public class DeleteResponse extends Response {
	
	private boolean deleteOk = false ;
	private boolean notFound = false ;
	private String msg ;
	public DeleteResponse(){
		super() ;
	}
	
	public static DeleteResponse buildOkResponse() {
		DeleteResponse deleteResponse = new DeleteResponse() ;
		deleteResponse.deleteOk = true ;
		return deleteResponse ;
	}
	
	public static DeleteResponse buildErrorResponse( String msg ) {
		DeleteResponse deleteResponse = new DeleteResponse() ;
		deleteResponse.deleteOk = false ;
		deleteResponse.msg = msg ;
		return deleteResponse ;
				 
	}
	
	public static DeleteResponse buildNotFoundResponse( ) {
		DeleteResponse response = new DeleteResponse() ;
		response.deleteOk = false ;
		response.notFound = true ;
		return response; 
	}
 	
	
	public String getMsg() {
		return msg;
	}
	
	public boolean isDeleteOk() {
		return deleteOk;
	}
	
	
	@Override
	public DeleteResponse checkException() throws MemcachedException {
		if( deleteOk ) {
			return this ;
		}
		if( notFound ) {
			return this ;
		}
		throw new MemcachedException("Delete Error Exception !");
	}
	
	
}
