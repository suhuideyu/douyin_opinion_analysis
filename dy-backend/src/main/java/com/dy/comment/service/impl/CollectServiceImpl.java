package com.dy.comment.service.impl;

import com.dy.comment.service.CollectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CollectServiceImpl implements CollectService {

    private static final String PROJECT_ROOT = System.getProperty("user.dir") + "/..";
    private static final String PYTHON_EXE = PROJECT_ROOT + "/.venv/Scripts/python.exe";
    private static final String PYTHON_DIR = PROJECT_ROOT + "/Py_Data";
    private static final Map<String, String> STATUS_MAP = new ConcurrentHashMap<>();

    @Override
    public void startCollect(String videoId, int maxComments) {
        STATUS_MAP.put(videoId, "RUNNING");

        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                        PYTHON_EXE, "douyin_comment.py", videoId, String.valueOf(maxComments)
                );
                pb.directory(new File(PYTHON_DIR));
                pb.redirectErrorStream(true);
                pb.environment().put("PYTHONIOENCODING", "utf-8");
                Process process = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("[Collect {}] {}", videoId, line);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    STATUS_MAP.put(videoId, "SUCCESS");
                } else {
                    STATUS_MAP.put(videoId, "FAILED: exit code " + exitCode);
                }
            } catch (Exception e) {
                STATUS_MAP.put(videoId, "FAILED: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public Map<String, String> getStatus(String videoId) {
        String status = STATUS_MAP.getOrDefault(videoId, "IDLE");
        Map<String, String> result = new HashMap<>();
        result.put("videoId", videoId);
        result.put("status", status);
        return result;
    }
}
