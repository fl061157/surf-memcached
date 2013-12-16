package com.cplatform.surf.seriable;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fangliang
 *
 */

//TODO
public enum Transcoder implements Serializable {
	
	
	StringTranscoder( 1 << 8 ) {
		
		@Override
		public Object bytes2Object(byte[] bytes)  {
			try {
				return new String(bytes , defaultCharset  );
			} catch (Exception e) {
				throw new RuntimeException(" Decode String Error ! ") ;
			} 
		}
		
		@Override
		public byte[] object2Bytes(Object object)  {
			try {
				return ((String)object).getBytes( defaultCharset ) ;
			} catch (Exception e) {
				throw new RuntimeException(" Encdoe String Error ! ") ;
			}
		}
		
		@Override
		public Class<?> objectClass() {
			return String.class ;
		}
		
		
	} ,
	
	IntegerTranscoder( 2 << 8 ) { 
		@Override
		public Integer bytes2Object(byte[] bytes)  {
			return (int)decodeLong(bytes) ; 
		}
		
		@Override
		public byte[] object2Bytes(Object object)  { 
			int length = 4 ;
			Integer i = Integer.parseInt( String.valueOf( object ) ) ; 
			return encodeEnum( i ,  length  ) ;
		}
		
		@Override
		public Class<?> objectClass() {
			return Integer.class ;
		}
		
	} ,
	
	LongTranscoder( 3 << 8 ) {
		@Override
		public Object bytes2Object(byte[] bytes) {
			return decodeLong(bytes); 
		}
		@Override
		public byte[] object2Bytes(Object object)  { 
			int length = 8 ;
			Long i = Long.parseLong( String.valueOf( object ) ) ; 
			return encodeEnum( i , length );
		}
		
		@Override
		public Class<?> objectClass() {
			return Long.class ;
		}
		
	} ,
	
	ByteTranscoder( 4 << 8 ) {
		@Override
		public Byte bytes2Object(byte[] bytes) {
			byte r = 0 ;
			if( bytes.length == 1 ) {
				r = bytes[0] ;
			}
			return r;
		}
		
		@Override
		public byte[] object2Bytes(Object object)  {
			Byte b = Byte.valueOf( object.toString() ) ; 
			return new byte[] { b }; 
		}
		
		@Override
		public Class<?> objectClass() {
			return Byte.class ;
		}
		
	} ,
	
	
	FloatTranscoder( 5 << 8 ) {
		@Override
		public Object bytes2Object(byte[] bytes) {
			int i = (int)decodeLong(bytes) ;
			return new Float( Float.intBitsToFloat( i ) ) ; 
		}
		
		@Override
		public byte[] object2Bytes(Object object) {
			return encodeEnum( Float.floatToRawIntBits( (Float) object ) , 4);  
		}
		
		@Override
		public Class<?> objectClass() {
			return Float.class ;
		}
		
		
	} ,
	
	
	DoubleTranscoder( 6 << 8 ) {

		@Override
		public Object bytes2Object(byte[] bytes) {
			Long l = decodeLong(bytes) ;
			return new Double( Double.longBitsToDouble( l ) ); 
		}
		@Override
		public byte[] object2Bytes(Object object)  {
			Double d = (Double) object ;
			return encodeEnum( Double.doubleToLongBits( d ) ,  8 ) ;
		}
		
		@Override
		public Class<?> objectClass() {
			return Double.class ;
		}
		
	} ,
	
	
	ByteArrayTranscoder( 7 << 8 ) {
		
		@Override
		public Object bytes2Object(byte[] bytes)  {
			return bytes ; 
		}
		
		@Override
		public byte[] object2Bytes(Object object) {
			return (byte[]) object ; 
		}
		
		@Override
		public Class<?> objectClass() {
			return  byte[].class ;      
		}
		
		
	} ,
	
	DateTranscoder( 8 << 8 ) {
		
		@Override
		public Object bytes2Object(byte[] bytes)  {
			return new Date( decodeLong(bytes)  );  
		}
		
		@Override
		public byte[] object2Bytes(Object object)  {
			return encodeEnum( ((Date)object).getTime()  , 8) ;
		}
		
		@Override
		public Class<?> objectClass() {
			return Date.class ;
		}
		
		
	} ,
	
	
	SerializeTranscoder( 9 << 8 ) {
		
		@Override
		public Object bytes2Object(byte[] bytes)  {
			try {
				return hessianSerializable.bytes2Object(bytes);
			} catch (IOException e) {
				throw new RuntimeException( e ) ;
			} 
		}
		
		@Override
		public byte[] object2Bytes(Object object)  {
			try {
				return hessianSerializable.object2Bytes(object);
			} catch (IOException e) {
				throw new RuntimeException( e ) ;
			} 
		}
		
		@Override
		public Class<?> objectClass() {
			return HessianSerializable.class ; //TDOO
		}
		
		
	} ;
	
	
	
	
	private int flag ;
	
	protected String defaultCharset = "UTF-8" ;
	
	protected HessianSerializable hessianSerializable = new HessianSerializable() ;
	
	public int getFlag() {
		return flag;
	}
	
	private Transcoder( int flag ) {
		this.flag = flag ;
	}
	

	
	
	public abstract Class<?> objectClass () ;
	
	public abstract byte[] object2Bytes( Object object )  ;
	 
	
	public abstract Object bytes2Object( byte[] bytes  )  ;

	
	protected byte[] encodeEnum( long l , int maxBytes ) {
		byte[] rv = new byte[maxBytes];
		for (int i = 0; i < rv.length; i++) {
			int pos = rv.length - i - 1;
			rv[pos] = (byte) ((l >> (8 * i)) & 0xff);
		}
		return rv ;
	}
	
	protected long decodeLong( byte[] bytes ) {
		long r = 0 ;
		for( byte b : bytes ) {
			r = ( r << 8 ) | ( b < 0 ? 256 + b : b ) ;
		}
		return r ; 
	}
	
	
	static Map<Integer , Transcoder> MAP = new HashMap<Integer , Transcoder>() ; 
	static {
		for( Transcoder t : Transcoder.values() ) {
			MAP.put( t.getFlag() , t ) ;
		}
	}
	public static Transcoder getTranscoder( int flag ) {
		return MAP.get( flag ) ; 
	}
	
	public static Transcoder transcoder( Object object ) {
		for( Transcoder t : Transcoder.values() ) {
			if( t.objectClass().equals( object.getClass() ) ) { 
				return t ;
			}
		}
		return Transcoder.SerializeTranscoder ;
	}
	
	
	//JUST TEST
	
//	public static void main( String[] args ) {
//		Integer i1 = new Integer( IntegerTranscoder.getFlag() ) ; 
//		Float f1 = new Float( 0.08 ) ;  
//		Double d1 = new Double(9.28 ) ;
//		String s1 = String.valueOf( "s" + StringTranscoder.getFlag() ) ; 
//		Date da1 = new Date() ;
//		User user = new User() ;
//		user.setId(1001) ; user.setName("fangliang") ;
//
//		
//		byte[] bytes = null ;
//		try {
//			bytes = Transcoder.transcoder( i1 ).object2Bytes( i1 ) ;
//			Integer i2 = (Integer)Transcoder.getTranscoder( IntegerTranscoder.getFlag() ).bytes2Object(bytes) ;
//			System.out.println(i1 + " = " + i2) ;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			bytes = Transcoder.transcoder( f1 ).object2Bytes( f1 ) ;
//			Float f2 = (Float)Transcoder.getTranscoder( FloatTranscoder.getFlag() ).bytes2Object(bytes) ;
//			System.out.println(f1 + " = " + f2) ;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		
//		try {
//			bytes = Transcoder.transcoder( d1 ).object2Bytes( d1 ) ;
//			Double d2 = (Double)Transcoder.getTranscoder( DoubleTranscoder.getFlag() ).bytes2Object(bytes) ;
//			System.out.println(d1 + " = " + d2) ;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			bytes = Transcoder.transcoder( s1 ).object2Bytes( s1 ) ;
//			String s2 = (String)Transcoder.getTranscoder( StringTranscoder.getFlag() ).bytes2Object(bytes) ;
//			System.out.println(s1 + " = " + s2) ;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			bytes = Transcoder.transcoder( da1 ).object2Bytes( da1 ) ;
//			Date da2 = (Date)Transcoder.getTranscoder( DateTranscoder.getFlag() ).bytes2Object(bytes) ;
//			System.out.println(da1 + " = " + da2) ;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		
//		try {
//			bytes = Transcoder.transcoder( user ).object2Bytes( user ) ;
//			User user2 = (User)Transcoder.getTranscoder( SerializeTranscoder.getFlag() ).bytes2Object(bytes) ;
//			System.out.println(user.getName() + " = " + user2.getName() ) ;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		
//		
//	}
	
}
