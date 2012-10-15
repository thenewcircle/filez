package com.marakana.filez.web;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.marakana.filez.service.Params;

public class PathRealmParser extends RealmParser.Support {

	public static class Factory implements RealmParser.Factory {
		@Override
		public RealmParser build(Params params) {
			String n = PathRealmParser.class.getName();
			return new PathRealmParser(Pattern.compile(params.getString(n
					+ ".pattern", "^/([^/]+)/([^/]+)/.*$")), params.getInteger(
					n + ".realmGroupId", 1), params.getInteger(n
					+ ".contextGroupId", 2));
		}
	}

	public PathRealmParser(Pattern parsePattern, int realmGroupId,
			int contextGroupId) {
		super(parsePattern, realmGroupId, contextGroupId);
	}

	@Override
	protected String getSource(HttpServletRequest req) {
		return req.getRequestURI();
	}
}
