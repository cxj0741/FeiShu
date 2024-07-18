package com.example.feishucontentharvester.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public class FeiShuController {
    @RestController
    @RequestMapping("/webhook")
    public class WebhookController {

        @PostMapping
        public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
            System.out.println("Received payload: " + payload);
            return new ResponseEntity<>("Received!", HttpStatus.OK);
        }

        @GetMapping
        public void test(){

        }
    }
}
