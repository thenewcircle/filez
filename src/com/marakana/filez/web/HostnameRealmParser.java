package com.marakana.filez.web;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.marakana.filez.service.Params;

public class HostnameRealmParser extends RealmParser.Support {

	public static class Factory implements RealmParser.Factory {
		@Override
		public RealmParser build(Params params) {
			String n = HostnameRealmParser.class.getName();
			return new HostnameRealmParser(Pattern.compile(params.getString(n
					+ ".pattern", "^([^\\-\\.]+)-([^\\-\\.]+)\\..*")),
					params.getInteger(n + ".realmGroupId", 1),
					params.getInteger(n + ".contextGroupId", 2));
		}
	}

	public HostnameRealmParser(Pattern parsePattern, int realmGroupId,
			int contextGroupId) {
		super(parsePattern, realmGroupId, contextGroupId);
	}

	@Override
	protected String getSource(HttpServletRequest req) {
		return req.getServerName();
	}
}
