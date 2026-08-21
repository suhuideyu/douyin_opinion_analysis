package com.dy.comment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String videoId;
    private String nickname;
    private String region;
    private LocalDateTime publishTime;
    private String content;
    private Integer likes;
    private Integer sentiment;
    private LocalDateTime createdAt;
}
