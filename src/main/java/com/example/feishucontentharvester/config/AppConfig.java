package com.example.feishucontentharvester.config;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import com.lark.oapi.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public Client getFeiShuClient(){
       return Client.newBuilder("cli_a61c85d89dfe900c","WqqwNp92yBVYmLXDrnp3pdz067iVVDLw").build();
    }

    @Bean
    public  Generation OpenAI(){
        Constants.apiKey="sk-cad62aa49af641628d4f940344a77fed";
        return new Generation();
    }

}
