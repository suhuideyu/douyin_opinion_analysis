package com.dy.comment.controller;

import com.dy.comment.annotation.RequireRole;
import com.dy.comment.dto.CollectRequest;
import com.dy.comment.dto.Result;
import com.dy.comment.service.CollectService;
import com.dy.comment.utils.CollectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/collect")
public class CollectController {

    @Autowired
    private CollectService collectService;

    @RequireRole
    @PostMapping("/start")
    public Result<String> start(@RequestBody CollectRequest req) {
        String videoId = CollectUtils.extractVideoId(req.getVideoId());
        int max = req.getMaxComments();
        if (max < 100) max = 100;
        if (max > 1000) max = 1000;
        collectService.startCollect(videoId, max);
        return Result.ok("采集任务已启动");
    }

    @RequireRole
    @GetMapping("/status")
    public Result<Map<String, String>> status(@RequestParam String videoId) {
        return Result.ok(collectService.getStatus(videoId));
    }
}
