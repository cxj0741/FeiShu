package com.example.feishucontentharvester.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.example.feishucontentharvester.constant.NewTableConstant;
import com.example.feishucontentharvester.service.impl.FeiShuServiceNewTableImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class FeiShuNewTableTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FeiShuServiceNewTableImpl feiShuServiceNewTable;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private FeiShuServiceApi feiShuServiceApi;

    @Test
    public void createNewTableTest(){
        feiShuServiceNewTable.createNewTable();
    }

    @Test
    public void initTableTest(){
        try {
            feiShuServiceNewTable.initNewTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getID(){
        String sheetId = feiShuServiceApi.getSheetId((String) redisTemplate.opsForValue().get("spreadsheetToken"));
        System.out.println(sheetId);
    }

    @Test
    public void addTest(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");

        headers.set("Authorization","Bearer "+redisTemplate.opsForValue().get("token"));

        String requestBody = "{\"valueRange\":{\"range\":\"302ef2!A1:E1\",\"values\":[[\"编号\",\"更新日志\",\"文章链接\",\"文章摘要\",\"文章关键字\"]]}}";



        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody,headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                NewTableConstant.FEISHU_ADD_URL+
                        redisTemplate.opsForValue().get("spreadsheetToken")+"/values_append",
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

    }
    @Test
    public void copyDataFromToTest(){
        feiShuServiceNewTable.copyDataFromTo();
    }
}

