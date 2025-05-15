package com.mo.core.common.utils;

import com.mo.common.utils.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RedisUtilTest {

    // 被测试的对象
    @InjectMocks
    private RedisUtil redisUtil;

    // 模拟的 RedisTemplate
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    // 模拟 ValueOperations（用于字符串操作）
    @Mock
    private ValueOperations<String, Object> valueOperations;

    // 模拟 HashOperations（用于哈希操作）
    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @BeforeEach
    public void setUp() {
        // 初始化 Mockito 注解
        MockitoAnnotations.openMocks(this);

        // 当调用 opsForValue 时返回模拟的 valueOperations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 当调用 opsForHash 时返回模拟的 hashOperations
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    public void testSet() {
        String key = "testKey";
        Object value = "testValue";

        redisUtil.set(key, value);

        verify(valueOperations, times(1)).set(key, value);
    }

    @Test
    public void testGet() {
        String key = "testKey";
        Object expectedValue = "testValue";

        when(valueOperations.get(key)).thenReturn(expectedValue);

        Object result = redisUtil.get(key);

        assertEquals(expectedValue, result);
    }

    @Test
    public void testDelete() {
        String key = "deleteKey";

        redisUtil.delete(key);

        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    public void testExpire() {
        String key = "expireKey";
        long timeout = 10;
        TimeUnit unit = TimeUnit.SECONDS;

        redisUtil.expire(key, timeout, unit);

        verify(redisTemplate, times(1)).expire(key, timeout, unit);
    }

    @Test
    public void testPutHash() {
        String key = "hashKey";
        String hashField = "field";
        Object value = "hashValue";

        redisUtil.putHash(key, hashField, value);

        verify(hashOperations, times(1)).put(key, hashField, value);
    }

    @Test
    public void testGetHash() {
        String key = "hashKey";
        String hashField = "field";
        Object expectedValue = "hashValue";

        when(hashOperations.get(key, hashField)).thenReturn(expectedValue);

        Object result = redisUtil.getHash(key, hashField);

        assertEquals(expectedValue, result);
    }
}
