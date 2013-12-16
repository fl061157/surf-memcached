package com.cplatform.surf;

import java.util.HashMap;
import java.util.Map;

import com.cplatform.surf.command.Command;
import com.cplatform.surf.command.DeleteCommand;
import com.cplatform.surf.command.GetOneCommand;
import com.cplatform.surf.command.GetsOneCommand;
import com.cplatform.surf.command.IncrDecrComand;
import com.cplatform.surf.command.IncrDecrComand.Which;
import com.cplatform.surf.command.StoreCommand;
import com.cplatform.surf.command.StoreCommand.Store;
import com.cplatform.surf.response.DeleteResponse;
import com.cplatform.surf.response.GetOneResponse;
import com.cplatform.surf.response.GetsOneResponse;
import com.cplatform.surf.response.IncResponse;
import com.cplatform.surf.response.Response;
import com.cplatform.surf.response.StoreResponse;


/**
 * 
 * @author fangliang
 * @mail fl061157@gmail.com 
 */

public class MemcachedConnection {
	
	private static Object sync = new Object() ;
	
	private Session session ;
	
	private static Map<String , MemcachedConnection> cache = new HashMap<String , MemcachedConnection>() ;
	
	public MemcachedConnection( final String host , final int port ) {
		session = new NioSession( host , port ).register() ;
	}
	
	public void close() {
		if( this.session != null ) {
			this.session.close() ;
			this.session = null ;
		}
	}
	
	protected MemcachedConnection() {
		
	}
	
	
	abstract class Command2Response< C extends Command< R > , R extends Response > {  
		protected abstract C build() ;
		protected R response()  throws MemcachedException {
			C c = build() ;
			session.register().send( c ) ;  
			R r = c.getResponse() ; 
			return r;
		}
	}
	
	
	public static MemcachedConnection buildConnection( final String host , final int port ) { 
		String conString = String.format("%s:%d", host , port ) ;
		MemcachedConnection client = cache.get(  conString  ) ;
		if( client == null ) {
			synchronized (sync) { 
				client = cache.get( conString ) ; 
				if( client == null ) {
					client = new MemcachedConnection(host, port) ;
					cache.put(conString, client ) ;
				}
			}
		}
		return client ;
	}
	
	public Object get( final String key ) throws MemcachedException  { 
		return new Command2Response<GetOneCommand, GetOneResponse>() { 
			@Override
			protected GetOneCommand build() {
				return new GetOneCommand( ).buildKey(key); 
			}
		}.response().checkException().getObject() ; 
	}
	
	public GetsOneResponse gets( final String key ) throws MemcachedException {
		return new Command2Response<GetsOneCommand, GetsOneResponse>() { 
			@Override
			protected GetsOneCommand build() {
				return new GetsOneCommand().buildKey(key); 
			}
		}.response().checkException()  ;
	}
	
	public boolean set( final String key , final Object value  ) throws MemcachedException {
		return store(key, value, Store.SET ) ;
	}
	
	public boolean add( final String key , final Object value ) throws MemcachedException {
		return store(key, value, Store.Add ) ;
	}
	
	public boolean replace( final String key , final Object value ) throws MemcachedException {
		return store(key, value, Store.REPLACE ) ;
	}
	
	public boolean append( final String key , final Object value ) throws MemcachedException {
		return store(key, value , Store.APPEND ) ;
	}
	
	public boolean prepend( final String key , final Object value ) throws MemcachedException  {
		return store(key, value, Store.PREPEND ) ; 
	}
	
	public boolean cas( final String key , final Object value , final int cas )  throws MemcachedException {
		return new Command2Response< StoreCommand , StoreResponse>() {
			@Override
			protected StoreCommand build() { 
				StoreCommand command = new StoreCommand( )
				.buildKey( key ).buildStore( Store.CAS ).buildValue( value  ).buildCas(cas) ;  
				return command ;
			}
		}.response().checkException().isOk() ; 
	}
	
	public <T> boolean  cas( final String key , final GetsOneResponse response , final CasOperation<T> operation ) throws MemcachedException {
		int tries = operation.maxTries() ;
		GetsOneResponse gR = response ;
		tries = tries < 1 ? 1 : tries ;
		boolean b = false ;
		while( tries-- > 0 ) {
			int cas = gR.getCas() ;
			@SuppressWarnings("unchecked") 
			T newT = operation.newValue(gR.getCas(), (T)gR.getObject()) ; 
			b = cas(key, newT , cas ) ; 
			if( b ) {
				return b ;
			}
			gR = gets(key) ;
			if( gR == null || gR.getObject() == null ) { //TODO
				return false ;
			}
		}
		return false ;
	}
	
	
	private boolean store( final String key , final Object value , final Store store ) throws MemcachedException {
		return new Command2Response< StoreCommand , StoreResponse>() {
			@Override
			protected StoreCommand build() { 
				StoreCommand command = new StoreCommand()
				.buildKey( key ).buildStore( store ).buildValue( value  ) ; 
				return command ;
			}
		}.response().checkException().isOk() ; 
	}
	
	
	private long incrOrdecr( final String key, final long delta , final Which which , final long inital ) throws MemcachedException {
		IncResponse incResponse = new Command2Response<IncrDecrComand, IncResponse>() {
			@Override
			protected IncrDecrComand build() {
				IncrDecrComand command = new IncrDecrComand() ;
				return command.buildWhich( which ).buildKey(key).buildValue( delta );  
			}
		}.response().checkException()  ; 
		if( incResponse.isNotFound() ) {
			return new Command2Response<StoreCommand, StoreResponse>() {
				@Override
				protected StoreCommand build() {
					StoreCommand command = new StoreCommand(  )
					.buildKey( key ).buildStore( Store.Add ).buildValue( String.valueOf( inital  )  ) ; 
					return command ;
				}
			}.response().checkException().isOk() ? inital : -1 ;
		}
		return incResponse.getValue() ;
	}
	
	public long incr( final String key , final long delta ) throws MemcachedException { 
		return incrOrdecr(key, delta, Which.Incr , 0) ;
	}
	
	public long decr( final String key , final long delta ) throws MemcachedException {
		return incrOrdecr(key, delta, Which.Decr , 0 ) ;
	}
	
	
	public boolean delete( final String key ) throws MemcachedException {
		return new Command2Response<DeleteCommand, DeleteResponse>() {
			@Override
			protected DeleteCommand build() {
				DeleteCommand command = new DeleteCommand().buildKey(key) ;
				return command ;
			}
		}.response().checkException().isDeleteOk() ;
	}
	
}
