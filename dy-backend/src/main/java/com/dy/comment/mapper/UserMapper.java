package com.dy.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dy.comment.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
