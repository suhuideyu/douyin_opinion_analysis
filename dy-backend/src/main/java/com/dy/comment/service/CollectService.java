package com.dy.comment.service;

import java.util.Map;

public interface CollectService {
    void startCollect(String videoId, int maxComments);
    Map<String, String> getStatus(String videoId);
}
