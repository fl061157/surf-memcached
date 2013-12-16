package com.cplatform.surf.command;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import com.cplatform.surf.MemcachedProtocolException;
import com.cplatform.surf.response.DeleteResponse;
import com.cplatform.surf.response.Response;

public class DeleteCommand extends Command<DeleteResponse> {

	private final static String DELETE = "DELETED";
	private final static String NOT_FOUND = "NOT_FOUND"; 
	private final static byte[] DELETE_BYTE = new byte[] { 'd' , 'e' , 'l' , 'e' , 't' , 'e' } ;
	private String key ;
	
	public DeleteCommand(  ) {
		super() ;
 	}
	
	public DeleteCommand buildKey( String key ) {
		this.key = key ;
		return this ;
	}
	
	
	@Override
	public byte[] encode() {
		ByteBuf buf = Unpooled.copiedBuffer( DELETE_BYTE , SPACE_BYTE , key.getBytes( Charset.defaultCharset() ) , CTRL_BYTE ) ; 
		int readerIndex = buf.readableBytes() ;
		byte[] bytes = new byte[readerIndex] ;
		buf.readBytes(bytes) ;
		return bytes ;
	}
	
	@Override
	public Command<DeleteResponse> decode(ChannelHandlerContext ctx, ByteBuf in)
			throws MemcachedProtocolException {
		int index = -1 ;
		index = indexOf( in , LINE ) ;
		if( index == -1 ) {
			return null ;
		}
		ByteBuf buf = in.readBytes( index ) ;
		String line = toString(buf) ;
		in.skipBytes( SKIP ) ;
		if( line.equals( DELETE ) ) {
			DeleteResponse deleteResponse = DeleteResponse.buildOkResponse() ;
			this.setR( deleteResponse ) ;
			return this ;
		} else if( line.equals(NOT_FOUND) ) {
			DeleteResponse deleteResponse = DeleteResponse.buildNotFoundResponse() ;
			this.setR( deleteResponse ) ;
			return this ;
		} else if( line.equals( ERROR ) ) {
			DeleteResponse deleteResponse = DeleteResponse.buildErrorResponse("DELETE ERROR!" ) ;
			this.setR( deleteResponse ) ;
			return this ;
		} else if( line.equals( CLIENT_ERROR ) ) {
			DeleteResponse deleteResponse = DeleteResponse.buildErrorResponse( line.split( SPACE )[1]  ) ; 
			this.setR( deleteResponse ) ;
			return this ;
		} else if( line.equals( SERVER_ERROR ) ) {
			//TODO
			DeleteResponse deleteResponse = DeleteResponse.buildErrorResponse("DELETE SERVER ERROR!" ) ; 
			this.setR( deleteResponse ) ;
			return this ;
		}
		throw new MemcachedProtocolException("DELETE PROTOCOL ERROR !") ;
	}

}
