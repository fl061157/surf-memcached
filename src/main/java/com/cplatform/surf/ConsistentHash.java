package com.cplatform.surf;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 
 * 提供 一个 一致性 Hash 的方法
 * @author fangliang
 *
 * @param <T>
 */
public class ConsistentHash< T > {
	
	private Set< T > nodes ;
	
	static Hashing hashFunction = new Md5Hashing() ; 
	
	static int NUMBER_OF_REPLICAS = 256  ;
	
	private final SortedMap< Long, T > circle = new TreeMap< Long, T >();

	
	public ConsistentHash( Set< T > nodes ) { 
		this.nodes =  new HashSet< T >( nodes )   ;
		for( T node : this.nodes ) {
			for (int i = 0; i < NUMBER_OF_REPLICAS ; i++) {
				long key = hashFunction.hash(node.toString() + i) ;
			     circle.put(key , node);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private ConsistentHash () { }
	
	public void add( T node ) { 
		nodes.add( node ); 
		for (int i = 0; i < NUMBER_OF_REPLICAS ; i++) {
		     circle.put(hashFunction.hash(node.toString() + i), node);
		}
	}
	
	public void remove(T node) {
		nodes.remove( node );
	   for (int i = 0; i < NUMBER_OF_REPLICAS ; i++) {
	     circle.remove(hashFunction.hash(node.toString() + i));
	   }
	 }
	
	
	public Set<T> getNodes() {
		return nodes;
	}
	
	
	public T get( String key ) { 
		if (circle.isEmpty()) {
			return null;
		}
		long hash = hashFunction.hash(key);
		if (!circle.containsKey(hash)) {
			SortedMap<Long, T> tailMap = circle.tailMap(hash);
		    hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
		}
		return circle.get(hash);
	}
	
	
	
	public static void main(String[] args) {
		Set<Address> nodes = new HashSet<Address>() ;
		nodes.add( new Address("127.0.0.1", 8080) ) ;
		nodes.add( new Address("127.0.0.1", 8081) ) ;
		nodes.add( new Address("127.0.0.1", 8082) ) ;
		nodes.add( new Address("127.0.0.1", 8083) ) ;
		nodes.add( new Address("127.0.0.1", 8084) ) ;
		nodes.add( new Address("127.0.0.1", 8085) ) ;
		nodes.add( new Address("127.0.0.1", 8086) ) ;
		nodes.add( new Address("127.0.0.1", 8087) ) ;
		nodes.add( new Address("127.0.0.1", 8088) ) ;
		ConsistentHash< Address > ch = new ConsistentHash<Address>(nodes) ;
		String key1 = "k_1_1_1";
		for( int i = 0 ; i < 10 ; i++) {
			Address add = ch.get(key1) ;
			System.out.println(add.toString() ) ;
		}
		
		String key2 = "k_2_2_2";
		for( int i = 0 ; i < 10 ; i++) {
			Address add = ch.get(key2) ;
			System.out.println(add.toString() ) ;
		}
		
	}
	
	
}
