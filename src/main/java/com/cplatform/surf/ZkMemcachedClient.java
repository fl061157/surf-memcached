package com.cplatform.surf;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.I0Itec.zkclient.ZkClient;

import com.cplatform.surf.response.GetsOneResponse;
import com.cplatform.surf.zookeeper.MemcachedNodeListener;
import com.cplatform.surf.zookeeper.ZkClientFactory;

public class ZkMemcachedClient extends MemcachedConnection {
	
	
	private ZkClient zkClient ;
	
	private MemcachedNodeListener listener ;
	
	private final static Map< String , MemcachedNodeListener > LISTENER_CACHE = new HashMap<String, MemcachedNodeListener>() ;
	
	public ZkMemcachedClient( String zkConString , String basePath ,  String path  ) {
		try {
			zkClient = ZkClientFactory.getInstance().getZkClient( zkConString ) ;
		} catch (Exception e) {
			throw new RuntimeException( e )  ;
		}
		
		String fullPath = fullPath(basePath, path) ;
		synchronized ( fullPath.intern() ) {
			listener = LISTENER_CACHE.get( fullPath ) ; 
			if( listener == null ) {
				listener = new MemcachedNodeListener(basePath, path , zkClient).register() ;
				LISTENER_CACHE.put( fullPath , listener ) ;
			}
			if( listener == null ) {
				throw new RuntimeException("Listener Not Exists Exception !");
			}
		}
	}
	
	@Override
	public Object get(String key) throws MemcachedException {
		return listener.getConnection(key).get(key) ;
	}
	
	@Override
	public boolean add(String key, Object value) throws MemcachedException {
		return listener.getConnection(key).add(key, value) ;
	}
	
	@Override
	public boolean append(String key, Object value) throws MemcachedException {
		return listener.getConnection(key).append(key, value) ;
	}
	
	@Override
	public <T> boolean cas(String key, GetsOneResponse response,
			CasOperation<T> operation) throws MemcachedException {
		return listener.getConnection(key).cas(key, response, operation); 
	}
	
	@Override
	public boolean cas(String key, Object value, int cas)
			throws MemcachedException {
		return listener.getConnection(key).cas(key, value, cas) ;
	}
	
	@Override
	public long decr(String key, long delta) throws MemcachedException {
		return listener.getConnection(key).decr(key, delta); 
	}
	
	
	@Override
	public boolean delete(String key) throws MemcachedException { 
		return listener.getConnection(key).delete(key) ;
	}
	
	@Override
	public GetsOneResponse gets(String key) throws MemcachedException {
		return listener.getConnection(key).gets(key) ; 
	}
	
	@Override
	public long incr(String key, long delta) throws MemcachedException {
		return listener.getConnection(key).incr(key, delta); 
	}
	
	@Override
	public boolean prepend(String key, Object value) throws MemcachedException {
		return listener.getConnection(key).prepend(key, value); 
	}
	
	@Override
	public boolean replace(String key, Object value) throws MemcachedException {
		return listener.getConnection(key).replace(key, value); 
	}
	
	@Override
	public boolean set(String key, Object value) throws MemcachedException {
		return listener.getConnection(key).set(key, value); 
	}
	
	
	protected String fullPath( String basePath , String path ) {
		if( basePath.endsWith( File.separator ) ) {
			return String.format("%s%s", basePath , path ) ;
		} else {
			return String.format("%s%s%s", basePath , File.separator , path ) ;
		}
	}
	
}
