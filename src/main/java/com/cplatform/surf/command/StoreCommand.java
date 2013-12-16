package com.cplatform.surf.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

import com.cplatform.surf.MemcachedProtocolException;
import com.cplatform.surf.response.Response;
import com.cplatform.surf.response.StoreResponse;
import com.cplatform.surf.seriable.Transcoder;

public class StoreCommand extends Command<StoreResponse> {
	
	
	private final static String STORED = "STORED";
	private final static String NOT_STORED = "NOT_STORED"; 
	private final static String EXISTS = "EXISTS";
	private final static String NOT_FOUND = "NOT_FOUND";
	
	private final static int DEFAULT_EXPECT_TIME = 0 ;
	
	
	private final static byte[] SPACE_BYTE = new byte[] { ' ' } ; 
	private final static byte[] CTRL_BYTE = new byte[] { '\r' , '\n' } ;
	private String key ;
	private Object value ;
	private int expectTime = DEFAULT_EXPECT_TIME ;
	private int cas ;
	
	private Store store ;
	public static enum Store {
		SET( new byte[] { 's' , 'e' , 't' } ), 
		Add( new byte[] { 'a' , 'd' , 'd' } ),
		REPLACE( new byte[] { 'r' ,'e' , 'p' , 'l' , 'a' , 'c' , 'e' } ) ,
		CAS( new  byte[] { 'c' , 'a' , 's' } ) ,
		APPEND( new byte[] { 'a' , 'p' , 'p' , 'e' , 'n' , 'd' } ) ,
		PREPEND(new byte[] { 'p' , 'r' ,'e' , 'p' , 'e' , 'n' , 'd' } ) ;
		private Store(byte[] store) {
			this.store = store ;
		}
		private byte[] store ;
		public byte[] getStore() {
			return store;
		}
	}
	public StoreCommand buildKey( String key ) {
		this.key = key ;
		return this ;
	}
	public StoreCommand buildValue( Object value ) {
		this.value = value ;
		return this ;
	}
	public StoreCommand buildStore( Store store ) {
		this.store = store ;
		return this ;
	}
	
	public StoreCommand buildCas( int cas ) {
		this.cas = cas ;
		return this ;
	}
	
	public StoreCommand buildExpectTime( int expectTime ) {
		this.expectTime = expectTime ;
		return this ;
	}
	
	public StoreCommand( ) { 
		super() ;
	}
	
	@Override
	public byte[] encode() {
		byte[] store = this.store.getStore() ;
		byte[] keyb = key.getBytes( Charset.defaultCharset() ) ;
		Transcoder transcoder = Transcoder.transcoder( value ) ; // 此步骤抽象出来
		byte[] data = transcoder.object2Bytes( value ) ; 
		byte[] fBytes = String.valueOf( transcoder.getFlag() ).getBytes() ; //TODO  
		byte[] expectBytes = String.valueOf( expectTime ).getBytes() ;
		byte[] length = String.valueOf( data.length ).getBytes() ;
		ByteBuf buf = null ;
		if( this.store == Store.CAS ) {
			buf = Unpooled.copiedBuffer( store , SPACE_BYTE , keyb , SPACE_BYTE ,
					fBytes , SPACE_BYTE , expectBytes , SPACE_BYTE , length , SPACE_BYTE , String.valueOf( cas ).getBytes() , CTRL_BYTE , data ,   CTRL_BYTE  ) ;
		} else {
			buf = Unpooled.copiedBuffer( store , SPACE_BYTE , keyb , SPACE_BYTE ,
					fBytes , SPACE_BYTE , expectBytes , SPACE_BYTE , length , CTRL_BYTE , data , CTRL_BYTE  ) ; 
		}
		int readerIndex = buf.readableBytes() ;
		byte[] bytes = new byte[readerIndex] ;
		buf.readBytes(bytes) ;
		return bytes ;
	}
	
	@Override
	public Command<StoreResponse> decode(ChannelHandlerContext ctx, ByteBuf in) 
			throws MemcachedProtocolException {
		int index = -1 ;
		index = indexOf( in , LINE ) ;
		if( index == -1 ) {
			return null ;
		}
		ByteBuf buf = in.readBytes( index ) ; 
		in.skipBytes( SKIP ) ;
		String r = toString(buf) ;
		if( r.equals( STORED ) ) {
			StoreResponse sR = StoreResponse.buildStoredResponse() ;
			this.setR( sR ) ;
			return this ;
		} else if ( r.equals( NOT_STORED ) ) {
			StoreResponse nSR = StoreResponse.buildNotStoredResponse() ;
			this.setR( nSR ) ;
			return this ;
		} else if( r.equals( ERROR ) ) {
			StoreResponse eSR = StoreResponse.buildErrorResponse() ;
			this.setR( eSR ) ;
			return this ;
		} else if( r.startsWith( CLIENT_ERROR ) ) {
			StoreResponse ceSR = StoreResponse.buildClientErrorResponse( r.split(SPACE)[1] ) ; 
			this.setR( ceSR ) ;
			return this ;
		} else if( r.equals( EXISTS )  ) {
			StoreResponse existResponse = StoreResponse.buildCasModifiedResponse() ;
			this.setR( existResponse ) ;
			return this ;
		} else if( r.equals( NOT_FOUND ) ) {
			StoreResponse notFoundResponse = StoreResponse.buildCasNotFoundResponse() ;
			this.setR( notFoundResponse ) ;
			return this ;
		} 
		throw new MemcachedProtocolException(" Stored Data Protocol Error ! ") ;
	}
	
}
