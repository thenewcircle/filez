package com.marakana.filez.web;

import java.util.Enumeration;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.marakana.filez.domain.UsernameAndPassword;
import com.marakana.filez.service.Params;

public class WebUtil {

	private WebUtil() {

	}

	public static String dump(HttpServletRequest req,
			UsernameAndPassword usernameAndPassword) {
		StringBuilder out = new StringBuilder();
		out.append("Remote Addr=[").append(req.getRemoteAddr()).append("]")
				.append(", ");
		if (usernameAndPassword != null) {
			out.append("User=[").append(usernameAndPassword).append("]")
					.append(", ");
		}
		out.append("Server Name=[").append(req.getServerName()).append("]")
				.append(", ");
		out.append("Method=[").append(req.getMethod()).append("]").append(", ");
		out.append("URL=[").append(req.getRequestURI());
		String v = req.getQueryString();
		if (v != null) {
			out.append('?').append(v);
		}
		out.append("]").append(", ");
		out.append("Protocol=[").append(req.getProtocol()).append("]")
				.append(", ");
		out.append("Session-ID=[").append(req.getRequestedSessionId())
				.append("]").append(", ");
		for (@SuppressWarnings("unchecked")
		Enumeration<String> names = req.getHeaderNames(); names
				.hasMoreElements();) {
			String name = names.nextElement();
			String value = req.getHeader(name);
			out.append("Header [").append(name).append('=').append(value)
					.append("], ");
		}
		if ("POST".equals(req.getMethod())) {
			for (@SuppressWarnings("unchecked")
			Enumeration<String> names = req.getParameterNames(); names
					.hasMoreElements();) {
				String name = names.nextElement();
				for (String value : req.getParameterValues(name)) {
					out.append("Param [").append(name).append('=')
							.append(value).append("], ");
				}
			}
		}
		Cookie[] cookies = req.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				out.append("Cookie [").append(cookie.getName()).append('=')
						.append(cookie.getValue()).append("], ");
			}
		}
		return out.toString();
	}

	public static Params asParams(final Context ctx) {
		return new Params.Support() {
			@Override
			public Object get(String key) {
				try {
					return ctx.lookup(key);
				} catch (NameNotFoundException e) {
					return null;
				} catch (NamingException e) {
					throw new RuntimeException(
							"Failed to lookup [" + key + "]", e);
				}
			}
		};
	}

	public static Params asParams(final ServletContext ctx) {
		return new Params.Support() {
			@Override
			public Object get(String key) {
				return ctx.getInitParameter(key);
			}
		};
	}

	public static String getInitParameter(FilterConfig config, String name,
			String defaultValue) {
		String value = config.getInitParameter(name);
		return value == null ? defaultValue : value;
	}
}
