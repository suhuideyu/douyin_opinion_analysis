package com.dy.comment.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectUtils {

    private static final Pattern MEDIA_ID_PATTERN = Pattern.compile("/(?:video|note)/(\\d+)");

    public static String extractVideoId(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new RuntimeException("请输入视频链接或编号");
        }
        raw = raw.trim();

        // 1. 纯数字 → 直接返回
        if (raw.matches("\\d+")) {
            return raw;
        }

        // 2. 包含 /video/数字 或 /note/数字 的长链接 → 正则提取
        Matcher m = MEDIA_ID_PATTERN.matcher(raw);
        if (m.find()) {
            return m.group(1);
        }

        // 3. 包含 douyin.com 的链接（短链接等）→ 原样传给 Python 让浏览器处理重定向
        if (raw.contains("douyin.com")) {
            return raw;
        }

        throw new RuntimeException("无法识别视频编号，请检查链接是否正确");
    }
}
