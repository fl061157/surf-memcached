package com.cplatform.surf.command;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import com.cplatform.surf.MemcachedProtocolException;
import com.cplatform.surf.response.Response;
import com.cplatform.surf.response.TakeResponse;
import com.cplatform.surf.seriable.Transcoder;

public abstract class TakeCommand< T extends TakeResponse  >  extends Command<T> {     //TODO

	enum State {
		Begin ,
		Data ,
		End ,
	}
	private State state ;
	private final static String VALUE = "VALUE ";
	private final static int LEAST_LENGTH  = 5 ;
	
	
	public TakeCommand() { 
		super() ;
		this.state = State.Begin ;
	}
	
	@Override
	public Command<T> decode(ChannelHandlerContext ctx, ByteBuf in)
			throws MemcachedProtocolException {
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
				T tR = buildEmptyResponse() ;
				in.skipBytes( SKIP ); 
				this.setR( tR ) ;
				this.state = State.End ;
				return this ;
			}
			if( r.equals( ERROR ) ) {
				in.skipBytes( SKIP ) ;
				this.setR( buildErrorResponse() ) ; 
				return this ;
			}
			if( r.startsWith( CLIENT_ERROR ) ) { 
				this.setR( buildClientErrorResponse( r.split(SPACE)[1] ) ) ;     
				in.skipBytes( SKIP ) ;
				return this ;
			}
			if( ! r.startsWith( VALUE ) ) {
				throw new MemcachedProtocolException("Parse GetCommand Header Start Value Error !") ;
			}
			T tR = buildResponse( r ) ;
			this.setR( tR ) ; 
			this.state = State.Data ;
			in.skipBytes( SKIP ) ;
			return null ;
		}
		
		if( this.state == State.Data ) {
			int dataLength =  ((TakeResponse)this.getR()).getLength() ; //TODO
			if( in.readableBytes() < dataLength + SKIP ) {
				return null ;
			}
			ByteBuf data = in.readBytes( dataLength ) ;
			in.skipBytes( SKIP ) ;
			byte[] bytes = data.array() ;
			setResponseValues(bytes) ;
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

	
	
	public abstract T buildResponse( String line ) throws MemcachedProtocolException ;
	
	public abstract T buildEmptyResponse() ;
	
	public abstract T buildErrorResponse() ;
	
	public abstract T buildClientErrorResponse( String msg ) ;
	
	public void setResponseValues( byte[] bytes ) {
		TakeResponse tR = (TakeResponse)this.getR() ;
		tR.setValues( bytes ) ;
		Transcoder transcoder = Transcoder.getTranscoder( tR.getFlag() ) ;  
		Object object = transcoder.bytes2Object( bytes ) ;  
		tR.setObject(object) ; 
	}
	
}
