package com.cplatform.surf.seriable;

import java.io.IOException;

public interface Serializable {

	public  byte[] object2Bytes( Object object ) throws IOException ;
	 
	public Object  bytes2Object( byte[] bytes  ) throws IOException ; 
	
}
