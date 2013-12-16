package com.cplatform.surf;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 
 * @author fangliang
 * @mail fl061157@gmail.com 
 * @param <R>
 */
public class FutureImpl<R> implements Future<R> {

	private final Object sync ;
	private boolean isDone ;
	private boolean isCancled ;
	private Throwable failure ;
	private R result ;
	
	public FutureImpl() {
		this( new Object() ) ; 
 	}
	
	public FutureImpl( Object sync ) {
		this.sync = sync ;
	}

	
	public void setResult( R result ) {
		synchronized ( sync ) {
			this.result = result ;
			notifyHaveResult() ;
		}
	}
	
	
	public R getResult() {
		synchronized ( sync ) {
			return this.result ;
		}
	}
	
	private void notifyHaveResult() {
		this.isDone = true  ;
		this.sync.notifyAll() ;
	}
	
	
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		synchronized ( sync ) { 
			this.isCancled = true ;
			notifyHaveResult() ;
			return true ;
		}
	}

	@Override
	public boolean isCancelled() {
		synchronized (sync) {
			return this.isCancled ;
		}
	}

	@Override
	public boolean isDone() {
		synchronized ( sync ) {
			return false;
		}
	}
	
	
	public void failure(Throwable failure) {
		synchronized (this.sync) {
			this.failure = failure;
			notifyHaveResult();
		}
	}
	

	@Override
	public R get() throws InterruptedException, ExecutionException {
		synchronized (sync) {
			for( ; ; ) {
				if( this.isDone ) {
					if( this.isCancled ) {
						throw new IllegalStateException(" Cancled ! ") ;
					}
					if( this.failure != null ) {
						throw new ExecutionException( failure ) ;
					}
					if( this.result != null ) {
						return this.result ;
					}
				}
				this.sync.wait() ;
			}
		}
	}

	@Override
	public R get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		long startTime = System.currentTimeMillis() ;
		long timeoutMillis = TimeUnit.MILLISECONDS.convert( timeout, unit ) ;
		synchronized (sync) {
			for( ; ; ) {
				if( this.isDone ) {
					if( this.isCancled ) {
						throw new IllegalStateException(" Cancled ! ") ;
					}
					if( this.failure != null ) {
						throw new ExecutionException( failure ) ;
					}
					if( this.result != null ) {
						return this.result ;
					}
				} else if(  ( System.currentTimeMillis() - startTime ) > timeoutMillis   ) {
					throw new TimeoutException(" TimeOut !") ;
				}
				this.sync.wait(timeoutMillis) ;
			}
		}
	}
	
	
	
}
