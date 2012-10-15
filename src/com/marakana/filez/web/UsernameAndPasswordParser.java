package com.marakana.filez.web;

import javax.servlet.http.HttpServletRequest;

import com.marakana.filez.domain.UsernameAndPassword;
import com.marakana.filez.service.Params;

public interface UsernameAndPasswordParser {
	public UsernameAndPassword getUserAndPassword(HttpServletRequest req);

	public static interface Factory {
		public UsernameAndPasswordParser build(Params params);
	}
}
