package com.cplatform.surf.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

import com.cplatform.surf.MemcachedProtocolException;
import com.cplatform.surf.response.GetResponse;
import com.cplatform.surf.response.Response;
import com.cplatform.surf.seriable.Transcoder;

@Deprecated
public class GetCommand extends Command<GetResponse> { 
	
	enum State {
		Begin ,
		Data ,
		End ,
	}
	private State state ;
	private final static String VALUE = "VALUE ";
	private final static int LEAST_LENGTH  = 5 ;
	
	private final static byte[] GET_BYTE = new byte[] { 'g' , 'e' , 't' } ;
	private final static byte[] CTRL_BYTE = new byte[] { '\r' , '\n' } ;
	private final static byte[] SPACE_BYTE = new byte[] { ' ' } ;
	
	private String key ;
	
	public GetCommand( ) {
		super() ;
		this.state = State.Begin ;
	}
	
	public GetCommand buildCommand(String key) {
		this.key = key ;
		return this ; 
	}
	
	
	
	@Override
	public byte[] encode() {
		ByteBuf buf = Unpooled.copiedBuffer( GET_BYTE , SPACE_BYTE , key.getBytes( Charset.defaultCharset() ) , CTRL_BYTE ) ; 
		int readerIndex = buf.readableBytes() ;
		byte[] bytes = new byte[readerIndex] ;
		buf.readBytes(bytes) ;
		return bytes ;
	}
	
	public Command<GetResponse> decode( ChannelHandlerContext ctx, ByteBuf in ) throws MemcachedProtocolException {
		if( this.state == State.Begin ) {
			if( in.readableBytes() < LEAST_LENGTH ) { 
				return null ;
			}
			int index = indexOf( in , LINE ) ;
			if( index == -1 ) {
				return null ;
			}
			ByteBuf buf = in.readBytes( index ) ;
			String r = toString( buf ) ;  
			if( r == null ) {
				throw new MemcachedProtocolException("Parse GetCommand Header Empty Error !") ;
			}
			if( r.equals( END ) ) {
				GetResponse eResponse = GetResponse.buildEmptyGetResponse() ;
				in.skipBytes( SKIP ); 
				this.setR( eResponse ) ;
				this.state = State.End ;
				return this ;
			}
			if( r.equals( ERROR ) ) {
				in.skipBytes( SKIP ) ;
				this.setR( GetResponse.buildErrorResponse() ) ; 
				return this ;
			}
			if( r.startsWith( CLIENT_ERROR ) ) { 
				this.setR( GetResponse.buildClientErrorResponse( r.split(SPACE)[1] ) ) ;    
				in.skipBytes( SKIP ) ;
				return this ;
			}
			if( ! r.startsWith( VALUE ) ) {
				throw new MemcachedProtocolException("Parse GetCommand Header Start Value Error !") ;
			}
			GetResponse getResponse = GetResponse.build( r ) ; 
			this.setR( getResponse ) ; 
			this.state = State.Data ;
			in.skipBytes( SKIP ) ;
			return null ;
		}
		
		if( this.state == State.Data ) {
			int dataLength = getGetResponse().getLength() ;  
			if( in.readableBytes() < dataLength + SKIP ) {
				return null ;
			}
			ByteBuf data = in.readBytes( dataLength ) ;
			in.skipBytes( SKIP ) ;
			byte[] bytes = data.array() ;
			setGetResponseValues(bytes) ;
			this.state = State.End ;
			return null ;
		}
		if( this.state == State.End ) {
			if( in.readableBytes() < END_CTRL_LENGTH ) {
				return null ;
			}
			ByteBuf endBuf = in.readBytes( END_CTRL_LENGTH ) ; 
			if( ! toString(endBuf).equals( END_CTRL ) ) {  
				throw new MemcachedProtocolException("Get Data Protocol End Error !");
			}
			return this ;
		}
		return null ;
	}
	
	
	
	
	
	
	
	
	protected GetResponse getGetResponse() {
		return (GetResponse)this.getR(); 
	}
	
	protected void setGetResponseValues( byte[] bytes ) { //TODO 优化 之 
		GetResponse gr  = getGetResponse();
		gr.setValue( bytes ) ;
		Transcoder transcoder = Transcoder.getTranscoder( gr.getFlag() ) ; 
		Object object = transcoder.bytes2Object( gr.getValue() ) ; 
		gr.setObject(object) ; 
	}
	
	
}
