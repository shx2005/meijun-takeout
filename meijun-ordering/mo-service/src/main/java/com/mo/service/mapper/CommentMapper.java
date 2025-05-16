package com.mo.service.mapper;

import com.mo.entity.OrderComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper {
    void addOrderComment(@Param("orderComment") OrderComment orderComment);
}
