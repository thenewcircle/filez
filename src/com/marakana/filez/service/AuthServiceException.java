package com.marakana.filez.service;

public class AuthServiceException extends RuntimeException {

	private static final long serialVersionUID = -6830485652171215899L;

	public AuthServiceException() {
		super();
	}

	public AuthServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthServiceException(String message) {
		super(message);
	}
}
