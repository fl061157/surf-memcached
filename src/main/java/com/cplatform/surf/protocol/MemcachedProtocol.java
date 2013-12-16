package com.cplatform.surf.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.concurrent.BlockingQueue;

import com.cplatform.surf.LinkedTransferQueue;
import com.cplatform.surf.MemcachedException;
import com.cplatform.surf.MemcachedProtocolException;
import com.cplatform.surf.command.Command;
import com.cplatform.surf.response.Response;

/**
 * 
 * @author fangliang
 *
 */
@Sharable
public class MemcachedProtocol extends ByteToMessageDecoder<Command<Response>> {   

	private BlockingQueue< Command<Response>> queue = new LinkedTransferQueue<  Command<Response> >() ; 
	
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	public void addResponse( Command future ) { 
		this.queue.add( future ) ;
	}
	
	@SuppressWarnings("rawtypes")
	private Command command ;
	
	public void wakeUpAll() {
		Command<Response> command = null ;
		while ( ( command = queue.poll() ) != null ) {
			command.errorWakeUp(new MemcachedException("cao")) ;
		}
		this.queue = null ;
	}
	
	
	public MemcachedProtocol() {
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Command<Response> decode(ChannelHandlerContext ctx, ByteBuf in)  
			throws Exception {
		if( in.readableBytes() < 3 ) {
			return null ;
		}
		if( command == null ) {
			command = queue.take() ;
		}
		in.markReaderIndex() ;
		Command<Response> c = null ;
		try {
			c = command.decode(ctx, in) ;
		} catch( MemcachedProtocolException e ) { 
			fireException(in, this.command , e) ;
		} 
		if( c != null ) {
			this.command = null ;
		}
		return c;
	}
	
	private void fireException( ByteBuf in , Command<Response> command , MemcachedProtocolException e ) throws MemcachedProtocolException {
		in.clear() ;
		if( command != null ) {
			command.errorWakeUp( e ) ;
		}
		throw e ;
	}
	
	
	@Override
	public Command<Response> decodeLast(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		return decode(ctx, in); 
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
	}
	
	
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		ctx.fireExceptionCaught( new MemcachedException(" Channel Closed Exception !") ) ; 
	}
	
	
	

}
