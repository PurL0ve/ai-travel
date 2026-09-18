package com.ai.travel.common.util;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * 字符串工具类
 */
public class StringUtils {

    /**
     * 判断字符串是否为空（null 或 空字符串）
     */
    public static boolean isEmpty(CharSequence str) {
        return CharSequenceUtil.isEmpty(str);
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(CharSequence str) {
        return CharSequenceUtil.isNotEmpty(str);
    }

    /**
     * 判断字符串是否为空白（null、空字符串或仅包含空白字符）
     */
    public static boolean isBlank(CharSequence str) {
        return CharSequenceUtil.isBlank(str);
    }

    /**
     * 判断字符串是否不为空白
     */
    public static boolean isNotBlank(CharSequence str) {
        return CharSequenceUtil.isNotBlank(str);
    }

    /**
     * 字符串转小写
     */
    public static String toLowerCase(String str) {
        return str != null ? str.toLowerCase() : null;
    }

    /**
     * 字符串转大写
     */
    public static String toUpperCase(String str) {
        return str != null ? str.toUpperCase() : null;
    }

    /**
     * 去除字符串两端空白
     */
    public static String trim(String str) {
        return CharSequenceUtil.trim(str);
    }

    /**
     * 去除字符串左侧空白
     */
    public static String trimLeft(String str) {
        if (str == null) {
            return null;
        }
        int start = 0;
        while (start < str.length() && Character.isWhitespace(str.charAt(start))) {
            start++;
        }
        return str.substring(start);
    }

    /**
     * 去除字符串右侧空白
     */
    public static String trimRight(String str) {
        if (str == null) {
            return null;
        }
        int end = str.length();
        while (end > 0 && Character.isWhitespace(str.charAt(end - 1))) {
            end--;
        }
        return str.substring(0, end);
    }

    /**
     * 截取字符串，超出部分用指定字符省略
     */
    public static String truncate(CharSequence str, int maxLength, String suffix) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str.toString();
        }
        if (suffix == null) {
            suffix = "";
        }
        return str.subSequence(0, maxLength - suffix.length()).toString() + suffix;
    }

    /**
     * 截取字符串，超出部分省略
     */
    public static String truncate(CharSequence str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str.toString();
        }
        return str.subSequence(0, maxLength).toString();
    }

    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 首字母小写
     */
    public static String uncapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 判断两个字符串是否相等（忽略大小写）
     */
    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        return CharSequenceUtil.equalsIgnoreCase(str1, str2);
    }

    /**
     * 判断字符串是否包含指定子串（忽略大小写）
     */
    public static boolean containsIgnoreCase(CharSequence str, CharSequence searchStr) {
        return CharSequenceUtil.containsIgnoreCase(str, searchStr);
    }

    /**
     * 重复字符串
     */
    public static String repeat(String str, int times) {
        return CharSequenceUtil.repeat(str, times);
    }

    /**
     * 反转字符串
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * 移除字符串中的指定子串
     */
    public static String removeAll(CharSequence str, CharSequence strToRemove) {
        return CharSequenceUtil.removeAll(str, strToRemove);
    }

}