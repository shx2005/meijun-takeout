package com.mo.web.controller;

import com.mo.api.dto.SendMessageDTO;
import com.mo.api.service.SupportService;
import com.mo.api.vo.SendMessageVO;
import com.mo.common.enumeration.MessageStaus;
import com.mo.common.result.Result;
import com.mo.entity.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SupportController 单元测试类
 */
class SupportControllerTest {

    private SupportService mockSupportService;
    private SupportController controller;

    @BeforeEach
    void setUp() {
        // 初始化 mock 对象
        mockSupportService = Mockito.mock(SupportService.class);
        // 创建 controller 实例并注入 mock 服务
        controller = new SupportController(mockSupportService);
    }

    @Test
    void testSendMessageFromCustomer_success() {
        // 准备测试数据
        LocalDateTime now = LocalDateTime.now();
        SendMessageDTO dto = new SendMessageDTO();
        dto.setContent("测试内容");
        dto.setEmployeeId(1001L);
        dto.setOrderId(2001L);
        dto.setSenderType(0);
        dto.setTitle("测试标题");
        dto.setUserId(3001L);

        // 构造预期的 Message 对象
        Message expectedMessage = new Message();
        expectedMessage.setContent("测试内容");
        expectedMessage.setEmployeeId(1001L);
        expectedMessage.setOrderId(2001L);
        expectedMessage.setSenderType(0);
        expectedMessage.setTitle("测试标题");
        expectedMessage.setUserId(3001L);
        expectedMessage.setStaus(MessageStaus.unread); // 应该被设置为未读
        expectedMessage.setCreateTime(now); // 可能由框架自动设置
        expectedMessage.setUpdateTime(now); // 可能由框架自动设置

        // 构造模拟返回值
        SendMessageVO mockVO = new SendMessageVO(1L, "success");

        // 当 supportService.sendMessage 被调用时返回模拟值
        when(mockSupportService.sendMessage(any(Message.class))).thenReturn(mockVO);

        // 执行被测方法
        Result<SendMessageVO> result = controller.sendMessageFromCustomer(dto);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("OK", result.getMsg());
        assertNotNull(result.getData());
        assertEquals(mockVO, result.getData());

        // 验证 supportService.sendMessage 被正确调用一次
        verify(mockSupportService, times(1)).sendMessage(argThat(message -> {
            // 验证 message 的属性是否正确
            assertEquals(dto.getContent(), message.getContent());
            assertEquals(dto.getEmployeeId(), message.getEmployeeId());
            assertEquals(dto.getOrderId(), message.getOrderId());
            assertEquals(dto.getSenderType(), message.getSenderType());
            assertEquals(dto.getTitle(), message.getTitle());
            assertEquals(dto.getUserId(), message.getUserId());
            assertEquals(MessageStaus.unread, message.getStaus());
            return true;
        }));
    }
}
