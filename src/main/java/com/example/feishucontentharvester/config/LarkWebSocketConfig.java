package com.example.feishucontentharvester.config;


import com.lark.oapi.core.request.EventReq;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.event.CustomEventHandler;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.service.im.ImService;
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.lark.oapi.ws.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class LarkWebSocketConfig {

    @Value("${feishu.appID}")
    private String appID;

    @Value("${feishu.appSecret}")
    private String appSecret;

    @Bean
    public EventDispatcher eventDispatcher() {
        return EventDispatcher.newBuilder(appID, appSecret)
                .onP2MessageReceiveV1(new ImService.P2MessageReceiveV1Handler() {
                    @Override
                    public void handle(P2MessageReceiveV1 event) throws Exception {
                        System.out.printf("[ onP2MessageReceiveV1 access ], data: %s\n", Jsons.DEFAULT.toJson(event.getEvent()));
                    }
                })
                .onCustomizedEvent("message", new CustomEventHandler() {
                    @Override
                    public void handle(EventReq event) throws Exception {
                        System.out.printf("[ onCustomizedEvent access ], type: message, data: %s\n", new String(event.getBody(), StandardCharsets.UTF_8));
                    }
                })
                .build();
    }

    @Bean
    public Client larkClient(EventDispatcher dispatcher) {
        return new Client.Builder(appID, appSecret).eventHandler(dispatcher).build();
    }
}
