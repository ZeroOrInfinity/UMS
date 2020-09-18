package top.dcenter.ums.security.social.provider.weibo.util;

import java.util.Iterator;

public abstract class StringUtils {

	private StringUtils() {
	}

	public static <T> String join(Iterable<T> iterable) {
		Iterator<T> iterator = iterable.iterator();
		if (!iterator.hasNext()) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder(iterator.next()
				.toString());
		while (iterator.hasNext()) {
			stringBuilder.append(",");
			stringBuilder.append(iterator.next().toString());
		}
		return stringBuilder.toString();
	}

	public static String booleanToString(boolean value) {
		return value ? "1" : "0";
	}

}