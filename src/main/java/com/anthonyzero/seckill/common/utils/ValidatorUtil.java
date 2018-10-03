package com.anthonyzero.seckill.common.utils;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {

	private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

	/**
	 * 验证是否是手机号码
	 * @param value
	 * @return
	 */
	public static boolean isMobile(String value) {
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		Matcher matcher = mobile_pattern.matcher(value);
		return matcher.matches();
	}

	public static void main(String[] args) {
		System.out.println(isMobile("15823485122"));
	}
}
