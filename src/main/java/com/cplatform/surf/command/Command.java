package com.cplatform.surf.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

import com.cplatform.surf.FutureImpl;
import com.cplatform.surf.MemcachedProtocolException;
import com.cplatform.surf.response.Response;

/**
 * 
 * @author fangliang
 * @mail fl061157@gmail.com 
 */
public abstract class Command< R extends Response > { 

	private final FutureImpl<R> futureImpl ;
	private R t ;
	protected final static ByteBuf LINE = Unpooled.wrappedBuffer(new byte[] { '\r', '\n' } ) ; 
	protected final static byte[] SPACE_BYTE = new byte[] { ' ' } ; 
	protected final static byte[] CTRL_BYTE = new byte[] { '\r' , '\n' } ;
	protected final int SKIP = 2 ;
	protected final static String ERROR = "ERROR"; 
	protected final static String CLIENT_ERROR = "CLIENT_ERROR";
	protected final static String SERVER_ERROR = "SERVER_ERROR";
	protected final static String CTRL = "\r\n";
	protected final static String ERROR_CTRL = "ERROR\r\n";
	protected final static int ERROR_CTRL_LENGTH = 7 ;
	protected final static String END = "END";
	protected final static String END_CTRL = "END\r\n";
	protected final static int END_CTRL_LENGTH = 5 ;
	protected final static String SPACE = " " ; 
	 
	
	
	public Command() {
		this.futureImpl = new FutureImpl<R>() ; 
	}
	
	public abstract Command<R> decode( ChannelHandlerContext ctx, ByteBuf in ) throws MemcachedProtocolException ;
	
	public abstract byte[] encode() ;
	
	protected byte[] encode(byte[] ...bytes) {
		ByteBuf buf = Unpooled.copiedBuffer( bytes ) ; //抽象
		int readerIndex = buf.readableBytes() ;
		byte[] datas = new byte[readerIndex] ;
		buf.readBytes(datas) ;  
		return datas  ;
	} 
	
	public R getResponse() {
		try {
			return futureImpl.get() ;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null ;
	}
	
	public void setResponse(  ) {
		futureImpl.setResult( this.t  ) ;
	}
	
	public void errorWakeUp( Throwable failure ) {
		futureImpl.failure(failure) ;
	}
	
	
	public void setR( R t ) { 
		this.t = t ;
	}
	
	public Response getR() {
		return this.t ;
	}
	
	protected String toString( ByteBuf buf ) {
		return buf.toString( Charset.defaultCharset() ) ; 
	}
	
	
	protected int indexOf(ByteBuf haystack, ByteBuf needle) {
        for (int i = haystack.readerIndex(); i < haystack.writerIndex(); i ++) {
            int haystackIndex = i;
            int needleIndex;
            for (needleIndex = 0; needleIndex < needle.capacity(); needleIndex ++) {
                if (haystack.getByte(haystackIndex) != needle.getByte(needleIndex)) {
                    break;
                } else {
                    haystackIndex ++;
                    if (haystackIndex == haystack.writerIndex() &&
                        needleIndex != needle.capacity() - 1) {
                        return -1;
                    }
                }
            }

            if (needleIndex == needle.capacity()) {
                // Found the needle from the haystack!
                return i - haystack.readerIndex();
            }
        }
        return -1;
    }
	
	
}
