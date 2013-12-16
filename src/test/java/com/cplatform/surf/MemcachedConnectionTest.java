package com.cplatform.surf;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.cplatform.surf.response.GetsOneResponse;


public class MemcachedConnectionTest {
	
   private MemcachedClient client = null ;  
	
	@Before
	public void setUp() {
		String host = "127.0.0.1";
	    int port = 12000;
	    client = new MemcachedClient(host, port).build(host, 12001).build(host, 12002) ;
	} 
	
	private String key_1 = "key_i_1";
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
	
	
	private String key_2 = "key_i_2"; 
	private Double value_2 = 999.999 ;
	
	@Test
	public void testAddAndGet() {
		try {
			client.delete( key_2 ) ;
			boolean b = client.add(key_2, value_2) ;
			Assert.assertTrue( b ) ;
			Double d = (Double)client.get(key_2) ;
			Assert.assertTrue( d.doubleValue() == value_2.doubleValue() ) ;
			System.out.println( "Value: " + d ) ;
		} catch (MemcachedException e) {
			e.printStackTrace();
			Assert.assertTrue( false ) ;
		}
	}
	
	private String key_3 = "key_i_3";
	private String value_3 = "{'a':'aa',b:'bb'}";
	@Test
	public void testDelete() {
		try {
			boolean b = client.set(key_3, value_3 ) ;
			Assert.assertTrue(b) ;
			String v3 = (String)client.get( key_3 ) ;
			System.out.println( "v3 : " + v3 ) ;
			Assert.assertTrue(v3.equals( value_3 )) ; 
			b = client.delete( key_3) ;
			Assert.assertTrue( b ) ;
			try {
				v3 = (String)client.get(key_3) ;
				Assert.assertTrue(v3 == null) ;
			} catch (Exception e) {
			}
			System.out.println("v3: null ") ;
		} catch (MemcachedException e) {
			Assert.assertTrue( false ) ;
		}
	}
	
	private String key_4 = "key_i_4" ;
	private long value4  = 0l ;
	
	@Test
	public void testIncOrDecr() {
		try {
			client.delete(key_4) ;
			long l1 = client.incr( key_4 , value4 ) ;
			Assert.assertTrue( l1 == 0 ) ;
			l1 = client.incr(key_4, 100l) ;
			Assert.assertTrue( l1 == 100l )  ;
			System.out.println(" 0 + 100 =  " + l1 ) ;
			l1 = client.incr(key_4, 200l) ;
			Assert.assertTrue( l1 == 300l ) ;
			System.out.println( " 100 + 200 =  " + l1 ) ; 
			l1 = client.incr(key_4, 300l) ;
			Assert.assertTrue( l1 == 600l ) ; 
			System.out.println( " 300 + 300 =  " + l1 ) ; 
			l1 = client.decr(key_4, 400) ;
			Assert.assertTrue( l1 == 200l ) ; 
			System.out.println( " 600 - 400 =  " + l1 ) ; 
			l1 = client.decr(key_4, 300 ) ;
			Assert.assertTrue( l1 == 0l ) ;
			System.out.println( " 200 - 300 =  " + l1 ) ;
		} catch (MemcachedException e) {
			Assert.assertTrue(  false  ) ;
		} 
	}
	

	public static class User implements java.io.Serializable {
		private static final long serialVersionUID = 5063960610361352217L;
		
		public User( long id , String name ) {
			this.id = id ;
			this.name = name ;
		}
		
		private long id ;
		private String name ;
		public long getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		public void setId(long id) {
			this.id = id;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	
	private String key_5 = "key_i_5";
	private User value_5 = new User( 1002, "fangliang_1002" ) ; 
	@Test
	public void testInsertObject() {
		try {
			client.delete( key_5 ) ; 
			boolean b = client.set(key_5, value_5 )  ;
			Assert.assertTrue( b ) ;
			User u = (User)client.get(key_5) ;
			Assert.assertTrue( u.getName().equals( value_5.getName() ) && u.getId() == value_5.getId()  ) ;
			System.out.print( u.getId() + "  |   " + u.getName()  ) ;
		} catch (MemcachedException e) {
			e.printStackTrace();
			Assert.assertTrue( false ) ;
		}
	}

	
	private String key_6 = "key_i_6" ;
	private User value_6 = new User( 1003, "fangliang_1003" ) ; 
	@Test
	public void testGets() {
		try {
			client.delete( key_6 ) ; 
			client.set(key_6, value_6) ;
			GetsOneResponse gR = client.gets(key_6) ;
			Assert.assertTrue( gR != null ) ;
			System.out.println( ((User)gR.getObject()).getName()  ) ; 
			System.out.println( gR.getCas() ) ;
		} catch (MemcachedException e) {
			e.printStackTrace();
			Assert.assertTrue( false ) ;
		}
	}
	
	private String key_7 = "key_i_7"; 
	private User value_7 = new User( 1004 , "fangliang_1004" ) ; 
	@Test
	public void testCas() {
		try {
			client.delete( key_7 ) ;
			boolean b = client.set( key_7 , value_7 ) ;
			Assert.assertTrue( b ) ;
			GetsOneResponse  response = client.gets( key_7 ) ; 
			Assert.assertTrue( response != null ) ;
			int cas = response.getCas() ;
			System.out.println( cas ) ;
			User v77 = new User(1004, "fangliang_1005") ;
			boolean b1 = client.cas( key_7, v77 , cas) ;
			Assert.assertTrue( b1 ) ;
			response = client.gets( key_7 ) ;
			System.out.println(response.getCas()) ; 
			System.out.println( ((User)response.getObject()).getName()  ) ;
			
			User v777 = new User(1004, "fangliang_1006") ;
			try {
				client.cas( key_7, v777 , 10) ;
			} catch (Exception e) {
				b1 = false ;
			}
			Assert.assertFalse( b1 ) ;
			response = client.gets( key_7 ) ;
			System.out.println(response.getCas()) ; 
			System.out.println( ((User)response.getObject()).getName()  ) ;
		} catch (MemcachedException e) {
			e.printStackTrace();
			Assert.assertTrue( false ) ;
		}
	}
	
	private String key_8 = "key_i_8"; 
	private User value_8 = new User( 1005 , "fangliang_1005" ) ; 
	@Test
	public void testCas1() {
		try {
			client.delete( key_8 ) ;
			boolean b = client.set( key_8 , value_8 ) ;
			Assert.assertTrue( b ) ;
			GetsOneResponse  response = client.gets( key_8 ) ; 
			Assert.assertTrue( response != null ) ;
			b = client.cas(key_8 , response, new CasOperation<User>() {
				@Override
				public int maxTries() {
					return 5;
				}
				
				@Override
				public User newValue(int currentCAS, User currentValue) {
					return new User( 1006 , "fangliang_1006" );
				}
				
			}) ;
			Assert.assertTrue( b ) ;
			User u = (User)client.get(key_8) ;
			Assert.assertTrue( u.getName().equals( "fangliang_1006" ) ) ; 
			System.out.println( u.getId() + " |  " + u.getName() ) ;
		} catch (MemcachedException e) {
			e.printStackTrace();
			Assert.assertTrue( false ) ;
		}
	}
	
	private String key_9 = "key_9";
	private String value_9 = "value_9";
	@Test
	public void testReplace() {
		try {
			client.delete( key_9 ) ;
			boolean b = client.set( key_9 , value_9 ) ;
			Assert.assertTrue( b ) ;
			b = client.replace(key_9, "value_99") ;
			Assert.assertTrue( b ) ;
			String s  =(String)client.get(key_9) ;
			System.out.println(s) ;
			Assert.assertTrue(s.equals( "value_99" )); 
			
			String key_10 = "key_10" ;
			client.delete(key_10) ; 
			try {
				b = client.replace(key_10, "cao") ;
			} catch (Exception e) {
				b = false ;
			}
			Assert.assertFalse( b ) ;
			System.out.println("END ----") ;
		} catch (MemcachedException e) {
			e.printStackTrace();
			Assert.assertTrue( false ) ;
		}
	}
	
	private String key_10 = "key_i_10" ;
	private String value_10 = "value_10";
	@Test
	public void testAppend() {
		try {
			client.delete(key_10) ;
			boolean b = client.set(key_10, value_10) ;
			Assert.assertTrue(b) ;
			b = client.append(key_10, "_10") ;
			Assert.assertTrue(b) ;
			String s =(String)client.get(key_10) ;
			System.out.println(s) ;
		} catch (MemcachedException e) {
			e.printStackTrace();
			Assert.assertTrue( false ) ;
		}
	}
	
	
	
	
	
}
