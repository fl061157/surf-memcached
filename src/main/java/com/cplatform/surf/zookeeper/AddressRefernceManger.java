package com.cplatform.surf.zookeeper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.cplatform.surf.Address;
import com.cplatform.surf.MemcachedConnection;
import com.cplatform.surf.MemcachedException;

/**
 * 
 * @author fangliang
 *
 */
public class AddressRefernceManger {
	
	private final Map< Address , ConnectionRefernce > refrenceCache ;
	
	private AddressRefernceManger() {  
		refrenceCache = new HashMap<Address, AddressRefernceManger.ConnectionRefernce>() ; 
	}
	
	private static AddressRefernceManger instance = new AddressRefernceManger() ;
	
	public static AddressRefernceManger getInstance() {
		return instance ;
	}
	
	public void addConnection( Address address ) { 
		synchronized (address.toString().intern()) { 
			ConnectionRefernce refrence  = refrenceCache.get( address ) ;
			if( refrence == null ) {
				refrence = new ConnectionRefernce(address) ;
				refrenceCache.put( address , refrence ) ;
			} else {
				refrence.addRefrence() ;
			}
		}
	}
	
	public void closeConnection( Address address ) {
		synchronized (address.toString().intern()) { 
			ConnectionRefernce refrence = refrenceCache.get( address ) ;
			if( refrence != null ) {
				refrence.close() ;
			}
		}
	}
	
	public MemcachedConnection getConnection( Address address ) throws MemcachedException {
		synchronized (address.toString().intern() ) { 
			ConnectionRefernce refrence = refrenceCache.get( address ) ;
			if( refrence == null ) {
				throw new MemcachedException("Connection Not Exists Exception !");
			}
			return refrence.getConnection() ;
		}
	}
	
	protected class ConnectionRefernce {
		private final Address address ;
		private final AtomicInteger reference ;
		private MemcachedConnection connection ;
		public ConnectionRefernce( Address address ) {
			this.address = address ;
			this.connection = new MemcachedConnection( address.getHost() , address.getPort() ) ;
			reference = new AtomicInteger( 1 ) ;
		}
		public void close() {
			int count = reference.decrementAndGet() ;
			if( count <= 0 ) {
				this.connection.close() ;
				this.connection = null ;
				refrenceCache.remove( address ) ; 
			}
		}
		public MemcachedConnection getConnection() throws MemcachedException {
			if( this.connection == null ) {
				throw new MemcachedException("Connection Null Exception !") ;
			}
			return this.connection ;
		}
		
		public void addRefrence() {
			reference.incrementAndGet() ;
		}
	}

}
