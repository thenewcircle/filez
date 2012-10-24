package com.marakana.filez.web;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marakana.filez.service.Params;

public class HostToPathAccessControlFilter implements Filter {
	private static Logger logger = LoggerFactory
			.getLogger(HostToPathAccessControlFilter.class);

	private Pattern hostnamePattern;
	private Pattern pathPattern;

	@Override
	public void init(FilterConfig config) throws ServletException {
		try {
			Context ctx = new InitialContext();
			try {
				Params params = WebUtil.asParams((Context) ctx
						.lookup("java:comp/env/"));
				String n = this.getClass().getName();
				this.hostnamePattern = Pattern.compile(params.getString(n
						+ ".hostnamePattern",
						"^([^\\.\\-]+)-([^\\.\\-]+)\\..+$"));
				this.pathPattern = Pattern.compile(params.getString(n
						+ ".pathPattern", "^/([^/]+)/([^/]+)/files/.*$"));
			} finally {
				ctx.close();
			}
			logger.debug("Init'd");
		} catch (Exception e) {
			throw new ServletException("Failed to init", e);
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) req;
		HttpServletResponse httpResp = (HttpServletResponse) resp;
		String hostname = httpReq.getServerName();
		Matcher hostnameMatcher = this.hostnamePattern.matcher(hostname);
		if (hostnameMatcher.matches()) {
			String path = httpReq.getRequestURI();
			Matcher pathMatcher = this.pathPattern.matcher(path);
			if (pathMatcher.matches()) {
				String hostnameRealm = hostnameMatcher.group(1);
				String hostnameContext = hostnameMatcher.group(2);
				String pathRealm = pathMatcher.group(1);
				String pathContext = pathMatcher.group(2);
				if (hostnameRealm != null && hostnameRealm.equals(pathRealm)
						&& hostnameContext != null
						&& hostnameContext.equals(pathContext)) {
					if (logger.isTraceEnabled()) {
						logger.trace("Hostname to path check passed ["
								+ hostnameRealm + "=" + pathRealm + "], ["
								+ hostnameContext + "=" + pathContext
								+ "]. Allowing the request through.");
					}
					chain.doFilter(req, resp);
					return;
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("Mismatch [" + hostnameRealm + "] != ["
								+ pathRealm + "] OR [" + hostnameContext
								+ "] != [" + pathContext + "]");
					}
				}
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("Cannot match path [" + path + "]");
				}
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("Cannot match hostname [" + hostname + "]");
			}
		}
		httpResp.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	@Override
	public void destroy() {

	}
}
