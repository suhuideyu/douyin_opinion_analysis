package com.dy.comment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dy.comment.entity.Comment;

public interface CommentService {
    Page<Comment> listByVideo(String videoId, int page, int size, String region, Integer sentiment, String sort);
    Page<Comment> search(String keyword, String videoId, int page, int size);
}
