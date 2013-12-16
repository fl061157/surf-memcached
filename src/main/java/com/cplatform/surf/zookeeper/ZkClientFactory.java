package com.cplatform.surf.zookeeper;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

/**
 * 
 * @author fangliang //TODO 
 *
 */
public class ZkClientFactory {
	
	private Map<String, Future< ZkClient >> cache = new HashMap<String, Future< ZkClient > >() ;
	private ZkClientFactory(  ) {
	}
	private static ZkClientFactory instance = new ZkClientFactory();
	public static ZkClientFactory getInstance() {
		return instance ;
	}
	
	public ZkClient getZkClient( final String conString ) throws Exception {
		ZkClient zkClient = null ;
		Future< ZkClient > future = null ;
		synchronized ( conString.intern() ) {
			future = cache.get( conString ) ;
			if( future == null ) {
				FutureTask<ZkClient> task = new FutureTask<ZkClient>( new Callable<ZkClient>() {
					@Override
					public ZkClient call() throws Exception { 
						return new ZkClient( conString , 30000, 30000 , new ZkSerializer() {
							@Override
							public Object deserialize(byte[] bytes)
									throws ZkMarshallingError {
								 try {
								    	return new String(bytes, "utf-8");
								    }
								    catch (final UnsupportedEncodingException e) {
								    	throw new ZkMarshallingError(e);
								    }
							}
							
							@Override
							public byte[] serialize(Object data)
									throws ZkMarshallingError {
								try {
									return ((String) data).getBytes("utf-8");
								}
								catch (final UnsupportedEncodingException e) {
									throw new ZkMarshallingError(e);
								}
							}
							
						} ) ;
					}
				} ) ;
				cache.put(conString, task) ; 
				future = task ;
				Thread t = new Thread( task ) ;
				t.start() ;
			}
		}
		if( future != null ) {
			zkClient = future.get() ;
		}
		return zkClient ;
	}
	
}
