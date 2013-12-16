package com.cplatform.surf;

import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class ZookeeperMemcachedClientTest {
	private ZkMemcachedClient client = null ;  
		
	@Before
	public void setUp() {
		client = new ZkMemcachedClient("zk1.com:2181,zk2.com:2181,zk2.com:2182,zk3.com:2181,zk3.com:2182"
				, "/zk/memcached", "wap") ; 
	} 
		
	private String key_1 = "zk_key_i_1";
	private Long value_1 = 98765432l ;
	
	@Test
	public void testSetAndGet() {
		try {
			client.delete(key_1) ; 
			boolean b1 = client.set( key_1 , value_1 ) ; 
			Assert.assertTrue( b1 ) ; 
			Long v_1_1 = (Long)client.get(key_1) ;
			Assert.assertTrue( value_1.longValue() == v_1_1.longValue()  ) ;
			System.out.println( v_1_1 ) ;
			try {
				client.add(key_1, 100l) ;
				Assert.assertTrue( false ) ;
			} catch (Exception e) {
			}
		} catch (MemcachedException e) {
			e.printStackTrace();
			Assert.assertTrue( false ) ;
		}
 	}
	
	private String base_key_2 = "zk_key_2_"; 
	private String value_2 = "wolegecao_";
	@Test
	public void testZk() {
//		for( int i = 0 ; i < 1000 ; i++ ) {
//			try {
//				client.set(base_key_2 + i , value_2 + i) ;
//			} catch (MemcachedException e) {
//				e.printStackTrace();
//			}
//		}
		for( int j = 0  ; j < 5000 ; j++ ) {
			
			for( int i = 0 ; i < 1000  ; i++ ) {
				try {
					String s  = (String)client.get(base_key_2 + i) ;
					System.out.println( s ) ;
				} catch (MemcachedException e) {
					e.printStackTrace();
				}
			}
			
			
		}
		
	}
	
	
	
	
	
}
