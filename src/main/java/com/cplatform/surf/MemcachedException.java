package com.cplatform.surf;

public class MemcachedException extends Exception {

	private static final long serialVersionUID = 4853008270857883546L;

	public MemcachedException() {
		super();
	}

	public MemcachedException(String message, Throwable cause) {
		super(message, cause);
	}

	public MemcachedException(String message) {
		super(message);
	}

	public MemcachedException(Throwable cause) {
		super(cause);
	}

}
