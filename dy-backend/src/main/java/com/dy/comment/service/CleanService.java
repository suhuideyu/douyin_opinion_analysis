package com.dy.comment.service;

import java.util.List;
import java.util.Map;

public interface CleanService {
    List<Map<String, String>> getPendingFiles();
    Map<String, Object> runClean(List<String> fileNames);
    byte[] exportFiles(List<String> fileNames);
}
