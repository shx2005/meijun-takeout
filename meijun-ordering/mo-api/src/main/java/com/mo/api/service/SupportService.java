package com.mo.api.service;

import com.mo.api.vo.SendMessageVO;
import com.mo.entity.Message;

public interface SupportService {
    SendMessageVO sendMessage(Message message);
}
