package com.cplatform.surf;

import com.cplatform.surf.command.Command;
import com.cplatform.surf.response.Response;

public interface Session {
	
	public < C extends Command< R > , R extends Response >  void send( C command ) throws MemcachedException ; 
	
	public Session register( ) ;
	
	public void close() ;
	
}
