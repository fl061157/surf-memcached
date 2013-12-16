package com.cplatform.surf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cplatform.surf.response.GetsOneResponse;

/**
 * 
 * @author fangliang
 *
 */
public class MemcachedClient extends MemcachedConnection {
	
	private static Map<Address , MemcachedConnection> CONNECTION_CACHE = new HashMap<Address , MemcachedConnection>() ;
	private Set<Address> addressSet ; 
	private ConsistentHash<Address> consistentHash ;//一致性 Hash
	
	public MemcachedClient( String host , int port ) {
		Address address = new Address(host, port) ;
		initConnection(address) ;
		addressSet = new HashSet<Address>();  
		addressSet.add( address ) ; 
		consistentHash = new ConsistentHash<Address>( addressSet ) ; 
	}
	
	public MemcachedClient( Address[] addresses ) {
		addressSet = new HashSet<Address>();  
		for( Address address : addresses ) {
			initConnection(address) ;
			addressSet.add( address ) ; 
		}
		consistentHash = new ConsistentHash<Address>( addressSet ) ; 
	} 
	
	public MemcachedClient build( String host , int port ) {
		Address address = new Address( host , port  ) ; 
		this.initConnection(address) ;
		if( ! this.addressSet.contains( address ) ) {
			this.addressSet.add( address ) ;
			consistentHash.add( address ) ; 
		}
		return this ;
	}
	
	public MemcachedClient build( Address[] addresses ) {
		for( Address address : addresses ) {
			this.initConnection(address) ;
			if( ! this.addressSet.contains( address ) ) {
				this.addressSet.add( address ) ;
				consistentHash.add( address ) ;
			}
		}
		return this ;
	}
	
	
	@Override
	public Object get(String key) throws MemcachedException {
		return getConnection(key).get(key);
	}
	
	@Override
	public boolean set(String key, Object value) throws MemcachedException {
		return getConnection(key).set(key, value);
	}
	
	@Override
	public boolean add(String key, Object value) throws MemcachedException {
		return getConnection(key).add(key, value);
	}
	
	@Override
	public <T> boolean cas(String key, GetsOneResponse response,
			CasOperation<T> operation) throws MemcachedException {
		return getConnection(key).cas(key, response, operation);
	}
	
	
	MemcachedConnection getConnection(String key) throws MemcachedException {
		Address address = consistentHash.get(key ) ;
		MemcachedConnection connection = CONNECTION_CACHE.get( address ) ;
		if( connection == null ) {
			throw new MemcachedException("Connection Not Exists Exception") ; 
		}
		return connection ;
	}
 	
	@Override
	public boolean cas(String key, Object value, int cas)
			throws MemcachedException {
		return getConnection(key).cas(key, value, cas);  
	}
	
	@Override
	public long decr(String key, long delta) throws MemcachedException {
		return getConnection(key).decr(key, delta);
	}
	
	@Override
	public boolean delete(String key) throws MemcachedException {
		return getConnection(key).delete(key);
	}
	
	@Override
	public GetsOneResponse gets(String key) throws MemcachedException {
		return getConnection(key).gets(key);
	}
	
	@Override
	public long incr(String key, long delta) throws MemcachedException {
		return getConnection(key).incr(key, delta);
	}
	
	
	@Override
	public boolean append(String key, Object value) throws MemcachedException {
		return getConnection(key).append(key, value);
	}
	
	@Override
	public boolean prepend(String key, Object value) throws MemcachedException {
		return getConnection(key).prepend(key, value);
	}
	
	@Override
	public boolean replace(String key, Object value) throws MemcachedException {
		return getConnection(key).replace(key, value);
	}
	
	private void initConnection( Address address ) { 
		MemcachedConnection connection = CONNECTION_CACHE.get( address ) ;
		if( connection == null ) {
			synchronized ( address.toString().intern() ) { 
				connection = CONNECTION_CACHE.get( address ) ; 
				if( connection == null ) {
					connection = new MemcachedConnection( address.getHost(), address.getPort() ) ;
					CONNECTION_CACHE.put( address , connection ) ;
				}
			}
		}
	}
	
}
