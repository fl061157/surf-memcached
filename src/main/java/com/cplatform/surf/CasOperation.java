package com.cplatform.surf;

public interface CasOperation<T> {
	
	public int maxTries() ;
	
	public T newValue( int currentCAS , T currentValue ) ; 
	
}
