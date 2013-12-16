package com.cplatform.surf.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

import com.cplatform.surf.MemcachedProtocolException;
import com.cplatform.surf.response.IncResponse;
import com.cplatform.surf.response.Response;

public class IncrDecrComand extends Command<IncResponse>{ 

	private final static String NOT_FOUND = "NOT_FOUND"; 
	
	private Which which ;
	private String key ;
	private long value ;
	
	public IncrDecrComand(  ) {
		super() ;
	}
	
	
	
	public static enum Which {
		Incr(new byte[] {  'i' , 'n' , 'c' , 'r' } ) ,
		Decr(new byte[] {  'd' , 'e' , 'c' , 'r' } ) ;
		private byte[] command ;
		private Which( byte[] command ) {
			this.command = command ;
		}
	}
	
	public IncrDecrComand buildKey( String key ) {
		this.key = key ;
		return this ;
	}
	
	public IncrDecrComand buildValue( long value ) {
		this.value = value ;
		return this ;
	}
	
	public IncrDecrComand buildWhich( Which which ) {
		this.which = which ;
		return this ;
	}
	
	
	@Override
	public byte[] encode() {
		byte[] keyBytes = key.getBytes( Charset.defaultCharset() ) ; 
		ByteBuf buf = Unpooled.buffer() ;
		buf.writeBytes( which.command ) ;
		buf.writeBytes( SPACE_BYTE ) ;
		buf.writeBytes( keyBytes ) ;
		buf.writeBytes( SPACE_BYTE ) ;
		int i = stringSize( value ) ;
		byte[] vBytes = new byte[ i ] ;
		getBytes( value , i , vBytes ) ;
		buf.writeBytes( vBytes ) ;
		buf.writeBytes(CTRL_BYTE) ;
		int readerIndex = buf.readableBytes() ;
		byte[] bytes = new byte[readerIndex] ;
		buf.readBytes(bytes) ; 
		return bytes ;
	}
	
	@Override
	public Command<IncResponse> decode(ChannelHandlerContext ctx, ByteBuf in)
			throws MemcachedProtocolException {
		int index = -1 ;
		index = indexOf( in , LINE ) ;
		if( index == -1 ) {
			return null ;
		}
		ByteBuf buf = in.readBytes(index) ;
		in.skipBytes( SKIP ) ;
		String line = toString(buf) ;
		if( isDigit(line) ) {
			long value = Long.parseLong( line ) ;
			IncResponse incResponse = IncResponse.buildIncResponse(value) ;
			this.setR( incResponse ) ;
			return this ;
		} else if( line.equals( NOT_FOUND )  ) {
			IncResponse incResponse = IncResponse.buildNotFoundResponse() ;
			this.setR( incResponse  ) ;
			return this ;
		} else if( line.equals( ERROR ) ) {
			IncResponse incResponse = IncResponse.buildErrorResponse() ;
			this.setR( incResponse ) ;
			return this ;
		} else if( line.startsWith( CLIENT_ERROR ) ) {
			IncResponse incResponse = IncResponse.buildClientErrorResponse( line.split(SPACE)[1] ) ; 
			this.setR( incResponse ) ;
			return this ;
		}
		return null;
	}

	private boolean isDigit( String line ) {
		return Character.isDigit( line.charAt(0) ) ; 
	}
	
	
	public static final int stringSize(long x) {
		long p = 10;
		for (int i = 1; i < 19; i++) {
			if (x < p)
				return i;
			p = 10 * p;
		}
		return 19;
	}
	
	
	
	
	
	final static byte[] DigitOnes = { '0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3',
		'4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2',
		'3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8',
		'9', };

	
	final static byte[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
		'9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
		'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
		'z' };

	final static byte[] DigitTens = { '0', '0', '0', '0', '0', '0', '0', '0',
		'0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2',
		'2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3',
		'3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4',
		'4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
		'6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7',
		'7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8',
		'8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9',
		'9', };
	
	public static void getBytes(long i, int index, byte[] buf) {
		long q;
		int r;
		int pos = index;
		byte sign = 0;

		if (i < 0) {
			sign = '-';
			i = -i;
		}

		// Get 2 digits/iteration using longs until quotient fits into an int
		while (i > Integer.MAX_VALUE) {
			q = i / 100;
			// really: r = i - (q * 100);
			r = (int) (i - ((q << 6) + (q << 5) + (q << 2)));
			i = q;
			buf[--pos] = DigitOnes[r];
			buf[--pos] = DigitTens[r];
		}

		// Get 2 digits/iteration using ints
		int q2;
		int i2 = (int) i;
		while (i2 >= 65536) {
			q2 = i2 / 100;
			// really: r = i2 - (q * 100);
			r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
			i2 = q2;
			buf[--pos] = DigitOnes[r];
			buf[--pos] = DigitTens[r];
		}

		// Fall thru to fast mode for smaller numbers
		// assert(i2 <= 65536, i2);
		for (;;) {
			q2 = (i2 * 52429) >>> (16 + 3);
			r = i2 - ((q2 << 3) + (q2 << 1)); // r = i2-(q2*10) ...
			buf[--pos] = digits[r];
			i2 = q2;
			if (i2 == 0)
				break;
		}
		if (sign != 0) {
			buf[--pos] = sign;
		}
	}
	
	
	
	
	
	
//	
//	
//	public static final int viewLength( long x ) {
//		int div = 10 ;
//		int i = 0 ;
//		for( ; ; ) {
//			i++ ;
//			if( ( x = x / div)  == 0 ) { 
//				return i > 19 ? 19 : i ;
//			} 
//		}
//	}
	
	
    public static byte[] long2bytes(long num) {  
        byte[] b = new byte[8];  
        for (int i=0;i<8;i++) {  
            b[i] = (byte)(num>>>(56-(i*8)));  
        }  
        return b;  
    }  
	
	
}
