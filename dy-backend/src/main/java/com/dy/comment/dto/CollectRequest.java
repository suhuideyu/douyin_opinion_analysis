package com.dy.comment.dto;

import lombok.Data;

@Data
public class CollectRequest {
    private String videoId;
    private int maxComments = 300;
}
