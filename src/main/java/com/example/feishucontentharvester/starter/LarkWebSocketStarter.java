package com.example.feishucontentharvester.starter;

import com.lark.oapi.ws.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

// TODO ???
@Component
public class LarkWebSocketStarter {

    private final Client client;

    @Autowired
    public LarkWebSocketStarter(Client client) {
        this.client = client;
    }

    @PostConstruct
    public void startLarkWebSocket() {
        client.start();
    }
}