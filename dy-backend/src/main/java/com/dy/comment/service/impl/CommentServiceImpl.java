package com.dy.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dy.comment.entity.Comment;
import com.dy.comment.interceptor.RequestContext;
import com.dy.comment.mapper.CommentMapper;
import com.dy.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    private void applyUserFilter(LambdaQueryWrapper<Comment> wrapper) {
        Long userId = RequestContext.getUserId();
        Integer role = RequestContext.getRole();
        if (userId != null && (role == null || role != 1)) {
            wrapper.eq(Comment::getUserId, userId);
        }
    }

    @Override
    public Page<Comment> listByVideo(String videoId, int page, int size, String region, Integer sentiment, String sort) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getVideoId, videoId);
        applyUserFilter(wrapper);

        if (region != null && !region.isEmpty()) {
            wrapper.eq(Comment::getRegion, region);
        }
        if (sentiment != null) {
            wrapper.eq(Comment::getSentiment, sentiment);
        }

        if ("likes_desc".equals(sort)) {
            wrapper.orderByDesc(Comment::getLikes);
        } else if ("likes_asc".equals(sort)) {
            wrapper.orderByAsc(Comment::getLikes);
        } else if ("time_asc".equals(sort)) {
            wrapper.orderByAsc(Comment::getPublishTime);
        } else {
            wrapper.orderByDesc(Comment::getPublishTime);
        }

        return commentMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Page<Comment> search(String keyword, String videoId, int page, int size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (videoId != null && !videoId.isEmpty()) {
            wrapper.eq(Comment::getVideoId, videoId);
        }
        applyUserFilter(wrapper);
        wrapper.like(Comment::getContent, keyword);
        wrapper.orderByDesc(Comment::getPublishTime);
        return commentMapper.selectPage(new Page<>(page, size), wrapper);
    }
}
