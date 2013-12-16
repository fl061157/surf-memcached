package com.cplatform.surf.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

import com.cplatform.surf.MemcachedProtocolException;
import com.cplatform.surf.response.GetsOneResponse;

/**
 * 
 * @author fangliang
 *
 */
//TODO 继续抽象封装
public class GetsOneCommand extends TakeCommand<GetsOneResponse> {
	
	private String key ;
	private final static byte[] GETS_BYTE = new byte[] { 'g' , 'e' , 't' , 's' } ;
	private final static byte[] CTRL_BYTE = new byte[] { '\r' , '\n' } ;
	private final static byte[] SPACE_BYTE = new byte[] { ' ' } ;
	
	
	public GetsOneCommand buildKey( String key ) {
		this.key = key ;
		return this ;
	}
	
	public GetsOneCommand( ) {
		super() ;
	}
	
	@Override
	public GetsOneResponse buildClientErrorResponse(String msg) {
		return GetsOneResponse.buildClientErrorResponse(msg); 
	}
	
	@Override
	public GetsOneResponse buildEmptyResponse() {
		return GetsOneResponse.buildEmptyResponse() ;
	}
	
	@Override
	public GetsOneResponse buildErrorResponse() {
		return GetsOneResponse.buildErrorResponse() ; 
	}
	
	@Override
	public GetsOneResponse buildResponse(String line)
			throws MemcachedProtocolException {
		return GetsOneResponse.buildResponse(line); 
	}
	
	
	@Override
	public byte[] encode() {
		ByteBuf buf = Unpooled.copiedBuffer( GETS_BYTE , SPACE_BYTE , key.getBytes( Charset.defaultCharset() ) , CTRL_BYTE ) ; 
		int readerIndex = buf.readableBytes() ;
		byte[] bytes = new byte[readerIndex] ;
		buf.readBytes(bytes) ;
		return bytes ;
	}
	
}
