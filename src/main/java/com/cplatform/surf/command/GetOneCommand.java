package com.cplatform.surf.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

import com.cplatform.surf.MemcachedProtocolException;
import com.cplatform.surf.response.GetOneResponse;

public class GetOneCommand extends  TakeCommand<GetOneResponse> {   
	
	private final static byte[] GET_BYTE = new byte[] { 'g' , 'e' , 't' } ;
	private final static byte[] CTRL_BYTE = new byte[] { '\r' , '\n' } ;
	private final static byte[] SPACE_BYTE = new byte[] { ' ' } ;
	
	private String key ;
	
	public GetOneCommand( ) {  
		super() ;
	}

	public GetOneCommand buildKey( String key ) {
		this.key = key ;
		return this ;
	}
	
	
	@Override
	public  GetOneResponse buildResponse(String line) throws MemcachedProtocolException  {
		return GetOneResponse.buildResponse(line);
	}

	@Override
	public GetOneResponse buildEmptyResponse() {
		return GetOneResponse.buildEmptyResponse() ;
	}

	@Override
	public GetOneResponse buildErrorResponse() {
		return GetOneResponse.buildErrorResponse() ;
	}

	@Override
	public GetOneResponse buildClientErrorResponse(String msg) {
		return GetOneResponse.buildClientErrorResponse(msg); 
	}

	@Override
	public byte[] encode() {
		ByteBuf buf = Unpooled.copiedBuffer( GET_BYTE , SPACE_BYTE , key.getBytes( Charset.defaultCharset() ) , CTRL_BYTE ) ; 
		int readerIndex = buf.readableBytes() ;
		byte[] bytes = new byte[readerIndex] ;
		buf.readBytes(bytes) ;
		return bytes ;
	}

}
