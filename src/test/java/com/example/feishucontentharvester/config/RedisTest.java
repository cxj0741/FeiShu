package com.example.feishucontentharvester.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void redisTest(){
        System.out.println(redisTemplate);
        System.out.println(redisTemplate.opsForValue().get("row"));
    }

    @Test
    public String invoiceNumber() {
        Long sequence = redisTemplate.opsForValue().increment("invoice_number_key");
        return String.format("%08d", sequence);
    }

    @Test
    public void testIns()
    {
        String s = invoiceNumber();
        System.out.println(s);
    }
}
