//package com.example.feishucontentharvester.service.init;
//
//import com.example.feishucontentharvester.service.FeiShuServiceNewTable;
//import com.example.feishucontentharvester.service.OpenAiService;
//import com.example.feishucontentharvester.service.impl.FeiShuServiceNewTableImpl;
//import com.example.feishucontentharvester.service.impl.FeishuServiceApiImpl;
//import com.example.feishucontentharvester.service.impl.OpenAiServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MyApplicationRunner implements ApplicationRunner {
//
//    @Autowired
//    private  FeishuServiceApiImpl feishuServiceApi;
//
//    @Autowired
//    private FeiShuServiceNewTableImpl feiShuServiceNewTable;
//
//    @Autowired
//    private OpenAiServiceImpl openAiService;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        //首先获取token
//        feishuServiceApi.getAccessToken();
//
//        //创建表格
//        feiShuServiceNewTable.createNewTable();
//
//        //初始化表格
//        feiShuServiceNewTable.initNewTable();
//
//        //数据迁移
//        feiShuServiceNewTable.copyDataFromTo();
//    }
//
//
//}
