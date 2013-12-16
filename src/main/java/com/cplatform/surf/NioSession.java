package com.cplatform.surf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.cplatform.surf.command.Command;
import com.cplatform.surf.protocol.MemcachedProtocol;
import com.cplatform.surf.response.Response;

/**
 * 
 * @author fangliang
 *
 */
public class NioSession implements Session {
	
	private final String host ; 
	private final int port ;
	private Bootstrap boot ;
	private Channel channel ;
	private MemcachedProtocol protocol ;
	private Object lock = new Object() ;
	private Thread checkerThread ; 
	
	
	
	public NioSession( final String host , final int port ) {
		this.host = host ;
		this.port = port ;
	}
	
	
	public NioSession register() {
		if( boot == null ) {
			synchronized (lock) {
				if( boot == null ) {
					protocol = new MemcachedProtocol() ;
					boot = new Bootstrap() ;
					boot.group(new NioEventLoopGroup()).channel(NioSocketChannel.class)
					.remoteAddress(host, port).handler(new MemClientInitializer( protocol ));  
					try {
						channel = boot.connect().sync().channel() ;
						startChecker() ;
					} catch (InterruptedException e) {
						close() ;
						throw new RuntimeException( "Create Channel Failure !" );
					}  catch (Exception e) {
						close() ;
					}
				}
			}
		}
		return this ;
	}
	
	@Override
	public < C extends Command< R > , R extends Response >  void send( C command ) throws MemcachedException {
		if( command == null ) {
			throw new MemcachedException("Command Null Exception !");
		}
		if( channel == null || !channel.isActive() ) {
			throw new MemcachedException("Channel Already Closed Exception !");
		}
		protocol.addResponse( command ) ; 
		byte[] bytes = command.encode() ;
		channel.write( bytes ) ; //需要 Future 吗 
	}
	
	protected void startChecker() {
		if( this.checkerThread != null ) {
			checkerThread.interrupt() ;
			this.checkerThread = null ;
		}
		this.checkerThread = new Thread( new Checker() ) ;
		this.checkerThread.start() ;
	}
	
	
	public void close() {
		if( channel != null && channel.isActive() ) { 
			channel.close() ;
		}
		if( protocol != null ) {
			protocol.wakeUpAll() ;
			protocol = null ;
		}
		channel = null ;
		if( boot != null ) {
			boot.shutdown() ;
			boot = null ;
		}
	}
	
	class Checker implements Runnable {
		@Override
		public void run() {
			try {
				channel.closeFuture().sync() ;
				close() ;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

}
