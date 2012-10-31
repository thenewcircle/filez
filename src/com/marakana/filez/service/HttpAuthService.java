package com.marakana.filez.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marakana.filez.domain.Realm;
import com.marakana.filez.domain.UsernameAndPassword;

public class HttpAuthService implements AuthService {

	public static class Factory implements AuthService.Factory {

		@Override
		public AuthService build(Params params) throws AuthServiceException {
			final String n = HttpAuthService.class.getName();
			return new HttpAuthService(params.getString(n + ".urlFormat"),
					params.getInteger(n + ".connectionTimeout", 5000),
					params.getInteger(n + ".maxRetries", 3));
		}
	}

	private static final Logger logger = LoggerFactory
			.getLogger(HttpAuthService.class);

	private final String urlFormat;
	private final int maxRetries;
	private final int connectionTimeout;

	public HttpAuthService(String urlFormat, int connectionTimeout,
			int maxRetries) {
		this.urlFormat = urlFormat;
		this.connectionTimeout = connectionTimeout;
		this.maxRetries = Math.max(maxRetries, 1);
		if (logger.isInfoEnabled()) {
			logger.info("Initialized with urlFormat=[" + urlFormat
					+ "], connectionTimeout=[" + connectionTimeout
					+ "] ms, and maxRetries=[" + maxRetries + "]");
		}
		HttpURLConnection.setFollowRedirects(true);
	}

	private String getBasicAuthorizationHeader(
			UsernameAndPassword usernameAndPassword) {
		return "Basic "
				+ DatatypeConverter.printBase64Binary(String.format("%s:%s",
						usernameAndPassword.getUsername(),
						usernameAndPassword.getPassword()).getBytes());
	}

	@Override
	public AuthResult auth(UsernameAndPassword usernameAndPassword, Realm realm)
			throws AuthServiceException {
		return this.auth(usernameAndPassword, realm, 1);
	}

	private AuthResult auth(UsernameAndPassword usernameAndPassword,
			Realm realm, int tryCounter) throws AuthServiceException {
		if (logger.isTraceEnabled()) {
			logger.trace("Auth " + usernameAndPassword + " for " + realm
					+ " (#" + tryCounter + " try)");
		}
		try {
			URL url = new URL(String.format(this.urlFormat, realm.getName(),
					realm.getContext()));
			int response;
			String responseMessage;
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			try {
				connection.setRequestProperty("Authorization",
						getBasicAuthorizationHeader(usernameAndPassword));
				connection.setRequestProperty("User-Agent",
						"Marakana Filez v1.0");
				connection.setReadTimeout(this.connectionTimeout);
				connection.setUseCaches(false);
				connection.connect();
				response = connection.getResponseCode();
				responseMessage = connection.getResponseMessage();
			} finally {
				connection.disconnect();
			}
			if (logger.isDebugEnabled() && response != 200 && response != 204) {
				logger.debug("Got HTTP " + response + " (" + responseMessage
						+ ") while trying to authenticate "
						+ usernameAndPassword + " for " + realm);
			}
			switch (response) {
			case 200:
			case 204:
				if (logger.isTraceEnabled()) {
					logger.trace(usernameAndPassword
							+ " is authorized to access " + realm + " (after "
							+ tryCounter + " tries)");
				}
				return AuthResult.OK;
			case 401:
				if (logger.isTraceEnabled()) {
					logger.trace(usernameAndPassword
							+ " is not authorized to access " + realm
							+ " (after " + tryCounter + " tries)");
				}
				return AuthResult.UNAUTHORIZED;
			case 403:
				if (logger.isTraceEnabled()) {
					logger.trace(usernameAndPassword
							+ " is forbidden from accessing " + realm
							+ " (after " + tryCounter + " tries)");
				}
				return AuthResult.FORBIDDEN;
			case 503:
				if (tryCounter <= maxRetries) {
					try {
						Thread.sleep(tryCounter * 500);
						return this.auth(usernameAndPassword, realm,
								tryCounter + 1);
					} catch (InterruptedException e) {
						// fall through
					}
				}
				// fall through
			default:
				throw new AuthServiceException("Failed to auth "
						+ usernameAndPassword + " for " + realm
						+ ". Got unexpected response code [" + response
						+ "] and message [" + responseMessage + "]");
			}
		} catch (IOException e) {
			throw new AuthServiceException("Failed to auth "
					+ usernameAndPassword + " for " + realm, e);
		}
	}
}
