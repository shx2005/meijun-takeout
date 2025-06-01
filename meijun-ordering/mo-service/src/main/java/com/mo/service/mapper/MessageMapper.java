package com.mo.service.mapper;

import com.mo.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper {
    void saveMessage(Message message);
    Message getMessageById(Long id);
    int deleteMessage(Long id);
}
