package com.dy.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dy.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
