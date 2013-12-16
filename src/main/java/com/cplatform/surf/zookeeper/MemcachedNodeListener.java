package com.cplatform.surf.zookeeper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.cplatform.surf.Address;
import com.cplatform.surf.ConsistentHash;
import com.cplatform.surf.MemcachedConnection;
import com.cplatform.surf.MemcachedException;

/**
 * 
 * @author fangliang
 *
 */
public class MemcachedNodeListener implements IZkChildListener,
		IZkStateListener {
	
	private Set< Address > addressSet = new CopyOnWriteArraySet< Address >(); 
	
	private ZkClient zkClient ;
	
	private final String basePath ;
	
	private final String path ;
	
	private ConsistentHash<Address> consistentHash ;
	
	private volatile boolean newSession = false ;
	
	
	public MemcachedNodeListener( String basePath , String path , ZkClient zkClient ) { 
		this.basePath = basePath ;
		this.path = path ;
		this.zkClient = zkClient ;
	}
	
	public MemcachedNodeListener register() {
		if( zkClient == null  ) {
			throw new RuntimeException( "ZkClient Is Empty Error !" ) ;
		}
		zkClient.subscribeStateChanges( this ) ; 
		zkClient.subscribeChildChanges( fullPath() , this ) ;
		consistentHash = new ConsistentHash<Address>( new HashSet< Address >() ) ; //TODO   
		build() ;
		return this ;
	}
	
	@Override
	public void handleStateChanged(KeeperState state) throws Exception { //TODO
		if( state == KeeperState.SyncConnected ) {
			build() ;
		}
	}

	@Override
	public void handleNewSession() throws Exception { //TODO
		build() ;
	}

	@Override
	public void handleChildChange(String parentPath, List<String> currentChilds)
			throws Exception {
		build( currentChilds ) ; 
	}
	
	protected void build(  ) {
		List<String> paths = zkClient.getChildren( fullPath() ) ; 
		build(paths) ;
	}
	
	
	public MemcachedConnection getConnection( String key ) throws MemcachedException {
		Address address = waitNewSessionEndAndGet(key) ;
		return AddressRefernceManger.getInstance().getConnection(address) ;
	}
	
	
	protected void build( List<String> paths ) {
		newSessionBegin() ;
		try {
			Set< Address > current = parseAddress(paths) ; 
			Set<Address> add = new HashSet<Address>() ;
			Set<Address> sub = new HashSet<Address>() ;
			for( Address address : current ) { 
				if( ! addressSet.contains( address ) ) {
					add.add( address ) ; 
					addressSet.add( address ) ;
				}
			}
			for( Address address : addressSet ) {
				if( ! current.contains( address ) ) {
					sub.add( address ) ;
					addressSet.remove( address ) ;
				}
			}
			for( Address address : add ) {
				AddressRefernceManger.getInstance().addConnection(address) ;
				consistentHash.add( address ) ;
			}
			for( Address address : sub ) {
				AddressRefernceManger.getInstance().closeConnection(address) ;
				consistentHash.remove( address ) ;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			newSessionEnd() ;
		}
	}
	
	
	protected void newSessionBegin() {
		synchronized (this) { 
			newSession = false ;
		}
	}
	
	protected void newSessionEnd( ) {
		synchronized ( this ) { 
			newSession = true   ; 
			this.notifyAll() ;
		}
	}
	
	protected Address waitNewSessionEndAndGet( String key ) { 
		synchronized ( this ) {
			while( ! newSession ) {
				try {
					this.wait( 10) ;
				} catch (InterruptedException e) {
				}
			}
			return consistentHash.get(key) ;
		}
	}
	
	
	protected Address parseAddress( String path ) { 
		String[] array = path.split(":") ;
		return new Address( array[0] , Integer.parseInt( array[1] ) ) ; 
	}

	protected Set<Address> parseAddress( List<String> paths ) {
		Set<Address> set = new HashSet<Address>() ;
		for( String path : paths  ) {
			set.add( parseAddress(path) ) ; 
		}
		return set ;
	}
	
	protected String fullPath() {
		return String.format("%s/%s", basePath , path ) ;
	}

}
