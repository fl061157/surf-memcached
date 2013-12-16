package com.cplatform.surf;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Hashing implements Hashing {
	
	private MessageDigest digest ;
	
	public Md5Hashing() {
		try {
			digest = MessageDigest.getInstance("MD5") ;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException( e ) ; 
		}
	}
	
	@Override
	public long hash(String key) {
		digest.reset() ;
		digest.update( key.getBytes( Charset.defaultCharset() ) )  ; 
		byte[] bKey = digest.digest(); 
        long res = ((long) (bKey[3] & 0xFF) << 24) | 
        		   ((long) (bKey[2] & 0xFF) << 16) | 
        		   ((long) (bKey[1] & 0xFF) << 8)  | (long) (bKey[0] & 0xFF);
        return res;
	}

}
