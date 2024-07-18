package com.example.feishucontentharvester.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledService {

    @Autowired
    private FeishuServiceApiImpl feishuServiceApi;
    /**
     * 每隔半小时执行一次的方法。
     */
    @Scheduled(fixedDelay = 1000)
    public void executeEveryHalfHour() {
        feishuServiceApi.getAccessToken();
        System.out.println("刷新token方法在 " + java.time.LocalDateTime.now() + " 被执行");
    }
}
