package com.mo.service.impl;

import com.mo.api.service.SupportService;
import com.mo.api.vo.SendMessageVO;
import com.mo.common.constant.SendMessageConstant;
import com.mo.entity.Message;
import com.mo.service.mapper.MessageMapper;
import org.springframework.stereotype.Service;

@Service
public class SupportServiceImpl implements SupportService {

    private final MessageMapper messageMapper;

    public SupportServiceImpl(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public SendMessageVO sendMessage(Message message) {
        messageMapper.saveMessage(message);

        return SendMessageVO.builder()
                .id(message.getId())
                .status(SendMessageConstant.OK)
                .build();
    }
}
