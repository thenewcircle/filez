package com.marakana.filez.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marakana.filez.domain.UsernameAndPassword;
import com.marakana.filez.service.Params;

public class BasicAuthorizationUsernameAndPasswordParser implements
		UsernameAndPasswordParser {
	public static class Factory implements UsernameAndPasswordParser.Factory {
		@Override
		public UsernameAndPasswordParser build(Params params) {
			return new BasicAuthorizationUsernameAndPasswordParser();
		}
	}

	private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";
	private static final Pattern USER_PASSWORD_PATTERN = Pattern
			.compile("^(.+?)\\:(.+)$");

	private static Logger logger = LoggerFactory
			.getLogger(BasicAuthorizationUsernameAndPasswordParser.class);

	@Override
	public UsernameAndPassword getUserAndPassword(HttpServletRequest req) {
		String authorization = req.getHeader("Authorization");
		if (authorization == null) {
			logger.debug("Missing Authorization header");
		} else if (!authorization.startsWith(AUTHORIZATION_HEADER_PREFIX)) {
			if (logger.isWarnEnabled()) {
				logger.warn("Unsupported Authorization header: "
						+ authorization);
			}
		} else {
			try {
				authorization = authorization
						.substring(AUTHORIZATION_HEADER_PREFIX.length());
				authorization = new String(
						DatatypeConverter.parseBase64Binary(authorization));
				Matcher matcher = USER_PASSWORD_PATTERN.matcher(authorization);
				if (matcher.matches()) {
					return new UsernameAndPassword(matcher.group(1),
							matcher.group(2));
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("Failed to parse Authorization header ["
								+ authorization + "]");
					}
				}
			} catch (RuntimeException e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Failed to decode Authorization header ["
							+ authorization + "]", e);
				}
			}
		}
		return null;
	}
}
