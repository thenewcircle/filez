package com.marakana.filez.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marakana.filez.domain.Realm;
import com.marakana.filez.service.Params;

public interface RealmParser {
	public static interface Factory {
		public RealmParser build(Params params);
	}

	public static abstract class Support implements RealmParser {
		private Logger logger = LoggerFactory.getLogger(this.getClass());

		private final Pattern parsePattern;
		private final int realmGroupId;
		private final int contextGroupId;

		public Support(Pattern parsePattern, int realmGroupId,
				int contextGroupId) {
			this.parsePattern = parsePattern;
			this.realmGroupId = realmGroupId;
			this.contextGroupId = contextGroupId;
		}

		@Override
		public Realm getRealm(HttpServletRequest req) {
			String source = this.getSource(req);
			Matcher matcher = this.parsePattern.matcher(source);
			if (matcher.matches()) {
				return new Realm(matcher.group(realmGroupId),
						matcher.group(contextGroupId));
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("Failed to parse realm from [" + source + "]");
				}
				return null;
			}
		}

		protected abstract String getSource(HttpServletRequest req);
	}

	public Realm getRealm(HttpServletRequest req);
}
