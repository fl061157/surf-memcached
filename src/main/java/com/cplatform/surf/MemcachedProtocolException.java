package com.cplatform.surf;

import io.netty.handler.codec.CodecException;


/**
 * 
 * @author fangliang
 * @mail fl061157@gmail.com 
 */
public class MemcachedProtocolException extends CodecException {

	private static final long serialVersionUID = 5685733414976897230L;
	
	public MemcachedProtocolException() {
		super() ;
	}

	public MemcachedProtocolException(String message, Throwable cause) {
		super(message, cause);
	}

	public MemcachedProtocolException(String message) {
		super(message);
	}

	public MemcachedProtocolException(Throwable cause) {
		super(cause);
	}

}
