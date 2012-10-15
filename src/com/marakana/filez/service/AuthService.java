package com.marakana.filez.service;

import com.marakana.filez.domain.Realm;
import com.marakana.filez.domain.UsernameAndPassword;

public interface AuthService {
	public static interface Factory {
		public AuthService build(Params params) throws AuthServiceException;
	}

	public static enum AuthResult {
		OK, UNAUTHORIZED, FORBIDDEN
	}

	public AuthResult auth(UsernameAndPassword usernameAndPassword, Realm realm)
			throws AuthServiceException;
}
